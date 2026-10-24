package io.github.pabi277.tilefold.ui.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.domain.Game
import io.github.pabi277.tilefold.domain.GameStatus
import io.github.pabi277.tilefold.domain.Moves

/**
 * The game screen's own state: the engine's [Game] plus what only the screen
 * knows — the tiles currently on the board ([BoardScene]), whether the hint has
 * been dismissed, whether the pad is showing, and whether the game is paused.
 *
 * Every rule still comes from `domain/`: this class calls [Moves.plan] and
 * [Game.move] and never decides what a move *means*; it only decides what to
 * draw and in which order.
 *
 * Persistence, the win/game-over dialogs and the undo allowance arrive in phase 3
 * (`docs/ROADMAP.md` row 3) — here the best score is this session's maximum and
 * nothing is written to disk.
 */
@Stable
internal class GameSession(
    private val seed: Long,
) {
    private var round = 0

    var game: Game by mutableStateOf(Game.newGame(seed))
        private set

    /** Session best: the highest score this run has shown (no store until phase 3). */
    var best: Int by mutableIntStateOf(0)
        private set

    var scene: BoardScene by mutableStateOf(BoardScene.still(game.board))
        private set

    var hintVisible: Boolean by mutableStateOf(true)
        private set

    var padVisible: Boolean by mutableStateOf(false)
        private set

    var paused: Boolean by mutableStateOf(false)
        private set

    val status: GameStatus
        get() = game.status

    val canUndo: Boolean
        get() = game.canUndo

    /** Everything the screen draws, derived from the state above. */
    val screenState: GameScreenState
        get() =
            GameScreenState(
                scene = scene,
                score = game.score,
                best = best,
                status = game.status,
                canUndo = game.canUndo,
                hintVisible = hintVisible,
                padVisible = padVisible,
                dimmed = paused,
            )

    /** The screen's callbacks, bound to this session once and then stable. */
    fun actions(): GameActions =
        GameActions(
            onMove = { move(it) },
            onUndo = { undo() },
            onRestart = { restart() },
            onHome = { pause() },
            onTogglePad = { togglePad() },
        )

    /**
     * Plays one move.
     *
     * @return true when the board actually changed — an invalid swipe or a tap
     *         while paused returns false and costs nothing (no spawn, no score).
     */
    fun move(direction: Direction): Boolean {
        if (paused) return false
        releaseWin()
        val next = game.move(direction)
        if (next === game) return false
        val plan = Moves.plan(game.board, direction) ?: return false

        scene = BoardScene.after(scene, plan, next.board)
        game = next
        best = maxOf(best, next.score)
        hintVisible = false
        return true
    }

    /** Puts the board, the score and the next tile back — one free undo. */
    fun undo(): Boolean {
        if (paused) return false
        val previous = game.undo() ?: return false
        game = previous
        scene = BoardScene.still(previous.board)
        hintVisible = false
        return true
    }

    /** Starts a new game on a fresh seed; the session best survives. */
    fun restart() {
        round += 1
        game = Game.newGame(seed + round)
        scene = BoardScene.still(game.board)
        hintVisible = true
        paused = false
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    fun togglePad() {
        padVisible = !padVisible
    }

    /** Drops the tiles that have finished fading into a merge. */
    fun clearLeaving() {
        if (scene.leaving.isNotEmpty()) {
            scene = scene.copy(leaving = emptyList())
        }
    }

    /**
     * Phase 2 has no win dialog yet, so a finished 2048 accepts "keep playing"
     * on the spot instead of freezing the board. Phase 3 replaces this with the
     * dialog (`docs/ROADMAP.md` row 3.2).
     */
    private fun releaseWin() {
        if (game.status == GameStatus.WON) {
            game = game.keepPlaying()
        }
    }

    /** The board the pause sheet quotes ("highest tile 512"). */
    val highestTile: Int
        get() = scene.tiles.maxOfOrNull { it.value } ?: 0
}
