@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * The slide/merge rules, one hand-checked case at a time.
 *
 * Every expectation here is computed by hand from the rules in
 * `docs/UI-REFERENCE.md` ("How to play") — nothing was read off the engine's
 * output, so a rule change has to argue with this file.
 */
class MovesTest {
    private fun moved(outcome: MoveOutcome): Moves.Moved {
        assertThat(outcome).isInstanceOf(Moves.Moved::class.java)
        return outcome as Moves.Moved
    }

    @Test
    fun `a pair merges once, from the leading edge inward`() {
        val board = Board.rows(listOf(2, 2, 2, 2), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))

        val result = moved(Moves.apply(board, Direction.LEFT))

        assertThat(result.board.toString()).isEqualTo("4,4,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(result.scoreDelta).isEqualTo(8)
        assertThat(result.mergedValues).containsExactly(4, 4).inOrder()
    }

    @Test
    fun `a tile that merged does not merge again in the same move`() {
        val board = Board.rows(listOf(4, 4, 8, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))

        val result = moved(Moves.apply(board, Direction.LEFT))

        assertThat(result.board.toString()).isEqualTo("8,8,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(result.scoreDelta).isEqualTo(8)
    }

    @Test
    fun `unequal neighbours never merge, they just slide`() {
        val board = Board.rows(listOf(2, 2, 4, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))

        val result = moved(Moves.apply(board, Direction.LEFT))

        assertThat(result.board.toString()).isEqualTo("4,4,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(result.scoreDelta).isEqualTo(4)
        assertThat(result.mergedValues).containsExactly(4)
    }

    @Test
    fun `a lone tile slides to the leading edge`() {
        val board = Board.rows(listOf(0, 0, 0, 2), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))

        val result = moved(Moves.apply(board, Direction.LEFT))

        assertThat(result.board.toString()).isEqualTo("2,0,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(result.scoreDelta).isEqualTo(0)
        assertThat(result.mergedValues).isEmpty()
    }

    @Test
    fun `left and right mirror each other`() {
        val board = Board.rows(listOf(0, 2, 2, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))

        val left = moved(Moves.apply(board, Direction.LEFT))
        val right = moved(Moves.apply(board, Direction.RIGHT))

        assertThat(left.board.toString()).isEqualTo("4,0,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(right.board.toString()).isEqualTo("0,0,0,4/0,0,0,0/0,0,0,0/0,0,0,0")
    }

    @Test
    fun `up and down work on columns`() {
        val board =
            Board.rows(
                listOf(2, 0, 0, 0),
                listOf(2, 0, 0, 0),
                listOf(2, 0, 0, 0),
                listOf(2, 0, 0, 0),
            )

        val up = moved(Moves.apply(board, Direction.UP))
        val down = moved(Moves.apply(board, Direction.DOWN))

        assertThat(up.board.toString()).isEqualTo("4,0,0,0/4,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(down.board.toString()).isEqualTo("0,0,0,0/0,0,0,0/4,0,0,0/4,0,0,0")
        assertThat(up.scoreDelta).isEqualTo(8)
        assertThat(down.scoreDelta).isEqualTo(8)
    }

    @Test
    fun `merges resolve from the leading edge, not the tail`() {
        val board = Board.rows(listOf(4, 4, 4, 4), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))

        val left = moved(Moves.apply(board, Direction.LEFT))
        val right = moved(Moves.apply(board, Direction.RIGHT))

        assertThat(left.board.toString()).isEqualTo("8,8,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(right.board.toString()).isEqualTo("0,0,8,8/0,0,0,0/0,0,0,0/0,0,0,0")
    }

    @Test
    fun `a direction that changes nothing is NoMove`() {
        val packed = Board.rows(listOf(2, 4, 8, 16), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))

        assertThat(Moves.apply(packed, Direction.LEFT)).isEqualTo(Moves.NoMove)
    }

    @Test
    fun `NoMove is returned instead of an empty move`() {
        val board = Board.rows(listOf(2, 4, 8, 16), listOf(4, 8, 16, 32), listOf(2, 4, 8, 16), listOf(4, 8, 16, 32))

        Direction.entries.forEach { direction ->
            assertThat(Moves.apply(board, direction)).isEqualTo(Moves.NoMove)
        }
        assertThat(Moves.hasAnyMove(board)).isFalse()
    }

    @Test
    fun `hasAnyMove is true when any direction can move`() {
        val board = Board.rows(listOf(2, 2, 8, 16), listOf(4, 8, 16, 32), listOf(2, 4, 8, 16), listOf(4, 8, 16, 32))

        assertThat(Moves.hasAnyMove(board)).isTrue()
    }

    @Test
    fun `score delta always equals the sum of merged values`() {
        val board =
            Board.rows(
                listOf(2, 2, 4, 4),
                listOf(8, 0, 8, 0),
                listOf(0, 16, 16, 0),
                listOf(32, 32, 32, 32),
            )

        val result = moved(Moves.apply(board, Direction.LEFT))

        assertThat(result.scoreDelta).isEqualTo(result.mergedValues.sum())
        assertThat(result.board.toString()).isEqualTo("4,8,16,8/16,0,0,0/32,0,0,0/64,64,0,0")
    }

    @Test
    fun `a move never changes a cell that is not part of its line`() {
        val board =
            Board.rows(
                listOf(2, 0, 0, 0),
                listOf(4, 0, 0, 0),
                listOf(8, 0, 0, 0),
                listOf(16, 0, 0, 0),
            )

        val result = moved(Moves.apply(board, Direction.RIGHT))

        assertThat(result.board.column(0)).containsExactly(2, 4, 8, 16).inOrder()
        assertThat(result.board.column(3)).containsExactly(0, 0, 0, 0).inOrder()
    }
}
