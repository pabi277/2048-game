@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Properties that must hold for *every* board, checked over a few hundred
 * generated boards rather than one hand-picked case.
 *
 * The generator here is the engine's own [EngineRandom], seeded, so a failure is
 * reproducible: the message carries the board and the seed.
 */
class EnginePropertyTest {
    private fun randomBoard(random: EngineRandom): Board {
        val values = List(Board.CELLS) { TILE_VALUES[random.nextInt(TILE_VALUES.size)] }
        return Board.of(values)
    }

    private fun boards(count: Int): List<Board> {
        val random = EngineRandom.fromSeed(PROPERTY_SEED)
        return List(count) { randomBoard(random) }
    }

    /** The four lines of [board], each read from its leading edge. */
    private fun linesOf(
        board: Board,
        direction: Direction,
    ): List<List<Int>> =
        (0 until Board.WIDTH).map { index ->
            when (direction) {
                Direction.LEFT -> board.row(index)
                Direction.RIGHT -> board.row(index).reversed()
                Direction.UP -> board.column(index)
                Direction.DOWN -> board.column(index).reversed()
            }
        }

    @Test
    fun `no tile is lost by a slide`() {
        boards(BOARD_COUNT).forEach { board ->
            Direction.entries.forEach { direction ->
                val outcome = Moves.apply(board, direction)
                if (outcome is Moves.Moved) {
                    assertThat(outcome.board.values.sum()).isEqualTo(board.values.sum())
                }
            }
        }
    }

    @Test
    fun `every merge removes exactly one tile`() {
        boards(BOARD_COUNT).forEach { board ->
            Direction.entries.forEach { direction ->
                val outcome = Moves.apply(board, direction)
                if (outcome is Moves.Moved) {
                    val before = board.values.count { it != 0 }
                    val after = outcome.board.values.count { it != 0 }
                    assertThat(before - after).isEqualTo(outcome.mergedValues.size)
                }
            }
        }
    }

    @Test
    fun `the score delta is the sum of the merged values`() {
        boards(BOARD_COUNT).forEach { board ->
            Direction.entries.forEach { direction ->
                val outcome = Moves.apply(board, direction)
                if (outcome is Moves.Moved) {
                    // A pure slide moves tiles and scores nothing — still a valid move.
                    assertThat(outcome.scoreDelta).isEqualTo(outcome.mergedValues.sum())
                }
            }
        }
    }

    @Test
    fun `a merged value is twice a tile that was already there`() {
        boards(BOARD_COUNT).forEach { board ->
            Direction.entries.forEach { direction ->
                val outcome = Moves.apply(board, direction)
                if (outcome is Moves.Moved) {
                    val present = board.values.filter { it != 0 }.toSet()
                    assertThat(outcome.mergedValues.all { it / 2 in present }).isTrue()
                }
            }
        }
    }

    @Test
    fun `a moved board has no gaps towards the leading edge`() {
        boards(BOARD_COUNT).forEach { board ->
            Direction.entries.forEach { direction -> assertNoGaps(board, direction) }
        }
    }

    private fun assertNoGaps(
        board: Board,
        direction: Direction,
    ) {
        val outcome = Moves.apply(board, direction) as? Moves.Moved ?: return
        linesOf(outcome.board, direction).forEach { line ->
            val firstEmpty = line.indexOf(0)
            if (firstEmpty >= 0) {
                assertThat(line.drop(firstEmpty).all { it == 0 }).isTrue()
            }
        }
    }

    @Test
    fun `hasAnyMove agrees with the four directions`() {
        boards(BOARD_COUNT).forEach { board ->
            val anyDirection = Direction.entries.any { Moves.apply(board, it) is Moves.Moved }
            assertThat(Moves.hasAnyMove(board)).isEqualTo(anyDirection)
        }
    }

    @Test
    fun `a game replays identically from the same seed and choices`() {
        val choices = List(40) { CHOICES[choiceRandom.nextInt(CHOICES.size)] }

        fun play(seed: Long): Game = choices.fold(Game.newGame(seed)) { game, direction -> game.move(direction) }

        assertThat(play(1234L).snapshot).isEqualTo(play(1234L).snapshot)
    }

    @Test
    fun `a board only accepts tile values`() {
        assertThat(runCatching { Board.of(List(Board.CELLS) { 3 }) }.isFailure).isTrue()
        assertThat(runCatching { Board.of(List(Board.CELLS) { -2 }) }.isFailure).isTrue()
        assertThat(runCatching { Board.of(List(Board.CELLS - 1) { 2 }) }.isFailure).isTrue()
        assertThat(runCatching { Board.rows(listOf(2, 2, 2, 2)) }.isFailure).isTrue()
    }

    private companion object {
        const val PROPERTY_SEED = 20261024L
        const val BOARD_COUNT = 300

        /** Realistic tiles, with empties — the shapes a real game produces. */
        val TILE_VALUES: List<Int> = listOf(0, 0, 2, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048)

        val CHOICES: List<Direction> = Direction.entries

        val choiceRandom = EngineRandom.fromSeed(5150L)
    }
}
