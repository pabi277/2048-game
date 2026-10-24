@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * The game rules around [Moves]: scoring, spawning, undo, win, game over.
 *
 * The golden boards here are the phase-1 replay fixtures — `(seed, moves)` in,
 * one exact board out (`rule.md` §5: a board is never lost).
 */
class GameTest {
    private fun snapshot(
        board: Board,
        score: Int = 0,
        randomState: Long = 1L,
        reached2048: Boolean = false,
        keepPlaying: Boolean = false,
        moves: Int = 0,
    ) = GameSnapshot(board, score, randomState, reached2048, keepPlaying, moves)

    private fun playing(board: Board) = Game.restore(snapshot(board))

    @Test
    fun `a new game starts with two tiles, no score and no undo`() {
        val game = Game.newGame(1L)

        assertThat(game.board.values.count { it != 0 }).isEqualTo(2)
        assertThat(game.score).isEqualTo(0)
        assertThat(game.moves).isEqualTo(0)
        assertThat(game.status).isEqualTo(GameStatus.PLAYING)
        assertThat(game.canUndo).isFalse()
        assertThat(game.undo()).isNull()
    }

    @Test
    fun `a seed always opens the same board`() {
        assertThat(Game.newGame(20261024L).board).isEqualTo(Game.newGame(20261024L).board)
        assertThat(Game.newGame(1L).board).isNotEqualTo(Game.newGame(2L).board)
    }

    @Test
    fun `opening boards are pinned for known seeds`() {
        assertThat(Game.newGame(1L).board.toString())
            .isEqualTo("0,2,0,0/0,0,0,0/0,0,0,0/0,0,2,0")
        assertThat(Game.newGame(42L).board.toString())
            .isEqualTo("0,0,0,0/0,0,0,0/0,0,0,0/2,2,0,0")
        assertThat(Game.newGame(20261024L).board.toString())
            .isEqualTo("0,0,0,0/0,0,0,0/0,2,0,2/0,0,0,0")
    }

    @Test
    fun `a valid move scores, counts and spawns exactly one tile`() {
        val game = playing(Board.rows(listOf(2, 2, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0)))

        val moved = game.move(Direction.LEFT)

        assertThat(moved).isNotSameInstanceAs(game)
        assertThat(moved.score).isEqualTo(4)
        assertThat(moved.moves).isEqualTo(1)
        // one merged pair vanishes, one new tile appears
        assertThat(moved.board.values.count { it != 0 }).isEqualTo(2)
        assertThat(moved.board.values).containsAtLeast(4, 2)
    }

    @Test
    fun `an invalid move is free - same instance, same rng, no spawn`() {
        val game = playing(Board.rows(listOf(2, 4, 8, 16), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0)))

        val moved = game.move(Direction.LEFT)

        assertThat(moved).isSameInstanceAs(game)
        assertThat(moved.snapshot).isEqualTo(game.snapshot)
    }

    @Test
    fun `undo restores the board, the score and the generator position`() {
        val start = playing(Board.rows(listOf(2, 2, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0)))
        val moved = start.move(Direction.LEFT)
        assertThat(moved.canUndo).isTrue()

        val undone = moved.undo()!!

        assertThat(undone.board).isEqualTo(start.board)
        assertThat(undone.score).isEqualTo(0)
        assertThat(undone.snapshot.randomState).isEqualTo(start.snapshot.randomState)
        assertThat(undone.moves).isEqualTo(0)
        assertThat(undone.canUndo).isFalse()
        assertThat(undone.undo()).isNull()
    }

    @Test
    fun `a move replayed after undo is identical`() {
        val start = Game.newGame(7L)
        val moved = start.move(Direction.LEFT)

        val replayed = moved.undo()!!.move(Direction.LEFT)

        assertThat(replayed.snapshot).isEqualTo(moved.snapshot)
    }

    @Test
    fun `reaching 2048 wins exactly once and blocks input until keep playing`() {
        val almost =
            playing(
                Board.rows(
                    listOf(1024, 1024, 0, 0),
                    listOf(0, 0, 0, 0),
                    listOf(0, 0, 0, 0),
                    listOf(0, 0, 0, 0),
                ),
            )

        val won = almost.move(Direction.LEFT)

        assertThat(won.board.highestTile).isEqualTo(2048)
        assertThat(won.score).isEqualTo(2048)
        assertThat(won.snapshot.reached2048).isTrue()
        assertThat(won.status).isEqualTo(GameStatus.WON)

        // The win dialog owns the board until the player answers it.
        val ignored = won.move(Direction.RIGHT)
        assertThat(ignored).isSameInstanceAs(won)

        val kept = won.keepPlaying()
        assertThat(kept.status).isEqualTo(GameStatus.PLAYING)
        assertThat(kept.move(Direction.RIGHT)).isNotSameInstanceAs(kept)
    }

    @Test
    fun `game over means full and no equal orthogonal neighbour`() {
        val dead =
            Board.rows(
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 2),
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 2),
            )
        val alive =
            Board.rows(
                listOf(2, 2, 4, 8),
                listOf(4, 8, 2, 4),
                listOf(2, 4, 8, 2),
                listOf(4, 2, 4, 8),
            )

        assertThat(Game.isGameOver(dead)).isTrue()
        assertThat(Game.isGameOver(alive)).isFalse()
        val deadGame = playing(dead)
        assertThat(playing(alive).status).isEqualTo(GameStatus.PLAYING)
        assertThat(deadGame.status).isEqualTo(GameStatus.GAME_OVER)
        assertThat(deadGame.move(Direction.LEFT)).isSameInstanceAs(deadGame)
    }

    @Test
    fun `a full board that can still merge is not game over`() {
        val fullWithMerge =
            Board.rows(
                listOf(2, 2, 4, 8),
                listOf(4, 8, 2, 4),
                listOf(2, 4, 8, 2),
                listOf(4, 2, 4, 8),
            )
        val game = playing(fullWithMerge)

        assertThat(game.status).isEqualTo(GameStatus.PLAYING)
        assertThat(game.move(Direction.LEFT).score).isEqualTo(4)
    }

    @Test
    fun `an empty cell keeps the game alive even when nothing can merge`() {
        val board =
            Board.rows(
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 2),
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 0),
            )

        assertThat(Game.isGameOver(board)).isFalse()
        assertThat(playing(board).status).isEqualTo(GameStatus.PLAYING)
    }

    @Test
    fun `2048 with keep playing already chosen is not a win any more`() {
        val board = Board.rows(listOf(2048, 2, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))
        val game = Game.restore(snapshot(board, reached2048 = true, keepPlaying = true))

        assertThat(game.status).isEqualTo(GameStatus.PLAYING)
    }

    @Test
    fun `snapshots reject impossible values`() {
        val board = Board.EMPTY

        assertThat(runCatching { snapshot(board, score = -1) }.isFailure).isTrue()
        assertThat(runCatching { snapshot(board, moves = -1) }.isFailure).isTrue()
    }
}
