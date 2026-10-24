@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Golden replays: `(seed, moves)` in, one exact game out.
 *
 * If any of these fail, saved games from the previous build no longer resume and
 * the phase-1 contract ("reproducible from `(seed, moves)`") is broken — the fix
 * is the engine, never the golden.
 */
class ReplayTest {
    private fun play(
        seed: Long,
        moves: String,
    ): Game {
        val mapping =
            mapOf(
                'U' to Direction.UP,
                'D' to Direction.DOWN,
                'L' to Direction.LEFT,
                'R' to Direction.RIGHT,
            )
        return moves.fold(Game.newGame(seed)) { game, letter -> game.move(mapping.getValue(letter)) }
    }

    @Test
    fun `seed 20261024 replayed with LLUURDDLR lands on a known board`() {
        val game = play(20261024L, "LLUURDDLR")

        assertThat(game.board.toString()).isEqualTo("0,2,0,2/0,0,0,0/0,0,0,2/0,2,4,8")
        assertThat(game.score).isEqualTo(20)
        assertThat(game.moves).isEqualTo(8)
        assertThat(game.snapshot.randomState).isEqualTo(937781803555920346L)
    }

    @Test
    fun `seed 7 replayed with UDUDLRLR lands on a known board`() {
        val game = play(7L, "UDUDLRLR")

        assertThat(game.board.toString()).isEqualTo("0,0,0,4/0,0,0,2/0,2,0,2/0,0,8,2")
        assertThat(game.score).isEqualTo(20)
        assertThat(game.moves).isEqualTo(8)
        assertThat(game.snapshot.randomState).isEqualTo(1586496945815482766L)
    }

    @Test
    fun `the same seed and the same moves produce the same snapshot`() {
        assertThat(play(20261024L, "LLUURDDLR").snapshot)
            .isEqualTo(play(20261024L, "LLUURDDLR").snapshot)
    }

    @Test
    fun `different seeds produce different games`() {
        assertThat(play(1L, "LLUURDDLR").board).isNotEqualTo(play(2L, "LLUURDDLR").board)
    }

    @Test
    fun `a refused swipe does not advance the generator`() {
        val packed = Board.rows(listOf(2, 4, 8, 16), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))
        val start = Game.restore(GameSnapshot(packed, 0, 1L, false, false, 0))

        val withRefusal = start.move(Direction.LEFT).move(Direction.RIGHT)

        assertThat(withRefusal.snapshot).isEqualTo(start.move(Direction.RIGHT).snapshot)
    }
}
