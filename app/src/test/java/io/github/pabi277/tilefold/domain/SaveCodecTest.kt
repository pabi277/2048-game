@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * The save blob: it must round-trip a game exactly, and it must refuse anything
 * it cannot trust (`rule.md` §5 — a board is never lost, a half-board is never
 * restored, a newer schema is never guessed at).
 */
class SaveCodecTest {
    private fun board(cells: List<Int> = List(Board.CELLS) { 0 }) = Board.of(cells)

    private fun payload(
        schemaVersion: Int = 1,
        cells: List<Int> = List(Board.CELLS) { 0 },
        rng: Long = 42L,
        extra: String = "",
    ): String {
        val boardJson = cells.joinToString(",")
        return """{"schemaVersion":$schemaVersion,"board":[$boardJson],"score":12,"randomState":$rng,$extra"moves":3}"""
    }

    @Test
    fun `a game with an undo snapshot survives a round trip`() {
        val played = Game.newGame(20261024L).move(Direction.LEFT).move(Direction.DOWN)

        val restored = SaveCodec.decode(SaveCodec.encode(played))

        assertThat(restored).isNotNull()
        assertThat(restored!!.snapshot).isEqualTo(played.snapshot)
        assertThat(restored.canUndo).isEqualTo(played.canUndo)
        assertThat(restored.undoSnapshot).isEqualTo(played.undoSnapshot)
    }

    @Test
    fun `the undo snapshot really undoes after a reload`() {
        val played = Game.newGame(7L).move(Direction.LEFT)

        val restored = SaveCodec.decode(SaveCodec.encode(played))!!

        assertThat(restored.undo()!!.snapshot).isEqualTo(played.undo()!!.snapshot)
    }

    @Test
    fun `the blob is versioned and readable`() {
        val encoded = SaveCodec.encode(Game.newGame(1L))

        assertThat(encoded).contains("\"schemaVersion\":${SaveCodec.CURRENT_SCHEMA_VERSION}")
        assertThat(encoded).contains("\"randomState\"")
        assertThat(encoded).contains("\"board\"")
        assertThat(SaveCodec.CURRENT_SCHEMA_VERSION).isEqualTo(1)
    }

    @Test
    fun `the generator position is part of the save, so a reload continues the game`() {
        val played = Game.newGame(99L).move(Direction.LEFT)
        val restored = SaveCodec.decode(SaveCodec.encode(played))!!

        val nextFromOriginal = played.move(Direction.RIGHT)
        val nextFromRestored = restored.move(Direction.RIGHT)

        assertThat(nextFromRestored.snapshot).isEqualTo(nextFromOriginal.snapshot)
    }

    @Test
    fun `nothing to read still means nothing to read`() {
        assertThat(SaveCodec.decode(null)).isNull()
        assertThat(SaveCodec.decode("")).isNull()
        assertThat(SaveCodec.decode("   ")).isNull()
    }

    @Test
    fun `corrupt blobs are refused, not repaired`() {
        assertThat(SaveCodec.decode("definitely not json")).isNull()
        assertThat(SaveCodec.decode("{\"schemaVersion\":\"one\"}")).isNull()
        assertThat(SaveCodec.decode("{\"schemaVersion\":1}")).isNull()
        assertThat(SaveCodec.decode("[]")).isNull()
    }

    @Test
    fun `a future schema is refused instead of guessed at`() {
        assertThat(SaveCodec.decode(payload(schemaVersion = 2))).isNull()
        assertThat(SaveCodec.decode(payload(schemaVersion = 0))).isNull()
    }

    @Test
    fun `impossible boards are refused`() {
        assertThat(SaveCodec.decode(payload(cells = List(15) { 2 }))).isNull()
        assertThat(SaveCodec.decode(payload(cells = List(Board.CELLS) { 3 }))).isNull()
        assertThat(SaveCodec.decode(payload(cells = List(Board.CELLS) { -2 }))).isNull()
    }

    @Test
    fun `a zero generator state is refused`() {
        assertThat(SaveCodec.decode(payload(rng = 0L))).isNull()
    }

    @Test
    fun `an older payload with missing fields still decodes`() {
        val minimal =
            """{"schemaVersion":1,"board":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4],"randomState":7}"""

        val restored = SaveCodec.decode(minimal)

        assertThat(restored).isNotNull()
        assertThat(restored!!.score).isEqualTo(0)
        assertThat(restored.moves).isEqualTo(0)
        assertThat(restored.board.highestTile).isEqualTo(4)
    }

    @Test
    fun `unknown keys are ignored so a field can be added without a schema bump`() {
        val withExtra = payload(extra = "\"somethingNew\":true,\"score2\":9,")

        assertThat(SaveCodec.decode(withExtra)).isNotNull()
    }

    @Test
    fun `a board that already holds 2048 is treated as reached`() {
        val cells = List(Board.CELLS) { 0 }.toMutableList().also { it[0] = 2048 }
        val minimal = """{"schemaVersion":1,"board":[${cells.joinToString(",")}],"randomState":7}"""

        val restored = SaveCodec.decode(minimal)!!

        assertThat(restored.snapshot.reached2048).isTrue()
        assertThat(restored.snapshot.keepPlaying).isFalse()
    }
}
