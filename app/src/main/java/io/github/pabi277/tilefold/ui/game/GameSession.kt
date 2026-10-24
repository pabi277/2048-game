package io.github.pabi277.tilefold.ui.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pabi277.tilefold.domain.AdPolicy
import io.github.pabi277.tilefold.domain.DailyChallenge
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.domain.Game
import io.github.pabi277.tilefold.domain.GameMode
import io.github.pabi277.tilefold.domain.GameStatus
import io.github.pabi277.tilefold.domain.Moves
import io.github.pabi277.tilefold.domain.Stats
import io.github.pabi277.tilefold.domain.UndoAllowance

/**
 * The game screen's own state: the engine's [Game] plus what only the screen
 * knows — the tiles currently on the board ([BoardScene]), whether the hint has
 * been dismissed, whether the pad is showing, and whether the game is paused.
 *
 * Every rule still comes from `domain/`: this class calls [Moves.plan] and
 * [Game.move] and never decides what a move *means*; it only decides what to
 * draw and in which order.
 *
 * Phase 3 owns persistence, win/game-over dialogs and the undo allowance
 * (`docs/ROADMAP.md` row 3). The best score is the session maximum *and* the
 * persisted [Stats.bestScore]; nothing is written to disk directly — the
 * owning [androidx.lifecycle.ViewModel] observes [onGameChanged] and debounces
 * the write.
 */
@Suppress("TooManyFunctions")
@Stable
internal class GameSession(
    private val seed: Long,
    initialGame: Game? = null,
    initialStats: Stats? = null,
    initialMode: GameMode = GameMode.CLASSIC,
    initialDailyEpochDay: Long? = null,
) {
    private var round = 0

    var game: Game by mutableStateOf(initialGame ?: Game.newGame(seed))
        private set

    var stats: Stats by mutableStateOf(initialStats ?: Stats())
        private set

    /** Session best: the highest score this run has shown (mirrors [stats.bestScore]). */
    var best: Int by mutableIntStateOf(maxOf(initialStats?.bestScore ?: 0, initialGame?.score ?: 0))
        private set

    var scene: BoardScene by mutableStateOf(BoardScene.still(game.board))
        private set

    var hintVisible: Boolean by mutableStateOf(true)
        private set

    var padVisible: Boolean by mutableStateOf(false)
        private set

    var paused: Boolean by mutableStateOf(false)
        private set

    var mode: GameMode by mutableStateOf(initialMode)
        private set

    var dailyEpochDay: Long? by mutableStateOf(initialDailyEpochDay)
        private set

    var undoAllowance: UndoAllowance by mutableStateOf(UndoAllowance())
        private set

    var storageError: Boolean by mutableStateOf(false)
        private set

    var showWinDialog: Boolean by mutableStateOf(game.status == GameStatus.WON)
        private set

    var showGameOverDialog: Boolean by mutableStateOf(game.status == GameStatus.GAME_OVER)
        private set

    var showRestartConfirm: Boolean by mutableStateOf(false)
        private set

    var isNewBest: Boolean by mutableStateOf(false)
        private set

    var interstitialState: AdPolicy.InterstitialState by mutableStateOf(AdPolicy.InterstitialState())
        private set

    /** Callback for persistence — set by the owning ViewModel / Route. */
    var onGameChanged: ((Game) -> Unit)? = null
    var onStatsChanged: ((Stats) -> Unit)? = null
    var onInterstitialStateChanged: ((AdPolicy.InterstitialState) -> Unit)? = null

    val status: GameStatus
        get() = game.status

    val canUndo: Boolean
        get() = game.canUndo && undoAllowance.canUndo

    val canEarnUndo: Boolean
        get() = undoAllowance.canEarnMore

    /** Everything the screen draws, derived from the state above. */
    val screenState: GameScreenState
        get() =
            GameScreenState(
                scene = scene,
                score = game.score,
                best = best,
                status = game.status,
                canUndo = canUndo,
                hintVisible = hintVisible,
                padVisible = padVisible,
                dimmed = paused || showWinDialog || showGameOverDialog || showRestartConfirm,
                winDialogVisible = showWinDialog,
                gameOverDialogVisible = showGameOverDialog,
                isNewBest = isNewBest,
                canEarnUndo = canEarnUndo,
                storageError = storageError,
                showRestartConfirm = showRestartConfirm,
            )

    /** The screen's callbacks, bound to this session once and then stable. */
    fun actions(): GameActions =
        GameActions(
            onMove = { move(it) },
            onUndo = { undo() },
            onRewardedUndo = { earnRewardedUndo() },
            onKeepPlaying = { keepPlaying() },
            onNewGameFromWin = { newGameFromDialog() },
            onNewGameFromGameOver = { newGameFromDialog() },
            onDismissGameOver = { dismissGameOver() },
            onRestart = { requestRestart() },
            onConfirmRestart = { confirmRestart() },
            onDismissRestartConfirm = { dismissRestartConfirm() },
            onHome = { pause() },
            onTogglePad = { togglePad() },
            onRetryStorage = { retryStorage() },
        )

    /**
     * Plays one move.
     *
     * @return true when the board actually changed — an invalid swipe or a tap
     *         while paused returns false and costs nothing (no spawn, no score).
     */
    @Suppress("ReturnCount")
    fun move(direction: Direction): Boolean {
        if (paused) return false
        if (showWinDialog || showGameOverDialog) return false
        val next = game.move(direction)
        if (next === game) return false
        val plan = Moves.plan(game.board, direction) ?: return false

        val wasWon = game.snapshot.reached2048
        scene = BoardScene.after(scene, plan, next.board)
        game = next
        best = maxOf(best, next.score)
        isNewBest = best > stats.bestScore && best == next.score
        hintVisible = false
        onGameChanged?.invoke(next)
        updateBestStats(next)
        refreshDialogs()
        if (!wasWon && next.snapshot.reached2048) {
            handleWin()
        }
        if (game.status == GameStatus.GAME_OVER) {
            handleGameOver()
        }
        return true
    }

    /** Puts the board, the score and the next tile back — one free undo. */
    fun undo(): Boolean {
        if (paused) return false
        if (!canUndo) return false
        val previous = game.undo() ?: return false
        game = previous
        scene = BoardScene.still(previous.board)
        hintVisible = false
        undoAllowance = undoAllowance.onUndo()
        showGameOverDialog = false
        showWinDialog = previous.status == GameStatus.WON
        onGameChanged?.invoke(previous)
        return true
    }

    /** Earns one rewarded undo behind [AdPolicy] — caller checks policy first. */
    fun earnRewardedUndo(): Boolean {
        if (!undoAllowance.canEarnMore) return false
        undoAllowance = undoAllowance.onRewarded()
        return true
    }

    fun canShowRewardedUndo(
        isAdFree: Boolean,
        hasConsent: Boolean,
    ): Boolean = AdPolicy.canShowRewardedUndo(undoAllowance, isAdFree, hasConsent)

    /** Starts a new game on a fresh seed; the session best survives. */
    fun restart() {
        round += 1
        val newGame = Game.newGame(seed + round)
        game = newGame
        scene = BoardScene.still(newGame.board)
        hintVisible = true
        paused = false
        showWinDialog = false
        showGameOverDialog = false
        showRestartConfirm = false
        isNewBest = false
        undoAllowance = UndoAllowance()
        onGameChanged?.invoke(newGame)
    }

    fun requestRestart() {
        showRestartConfirm = true
    }

    fun confirmRestart() {
        restart()
    }

    fun dismissRestartConfirm() {
        showRestartConfirm = false
    }

    fun keepPlaying() {
        if (game.status != GameStatus.WON) return
        game = game.keepPlaying()
        scene = BoardScene.still(game.board)
        showWinDialog = false
        onGameChanged?.invoke(game)
    }

    fun newGameFromDialog() {
        showWinDialog = false
        showGameOverDialog = false
        restart()
    }

    fun dismissGameOver() {
        val wasGameOver = showGameOverDialog
        showGameOverDialog = false
        if (wasGameOver) {
            val now = System.currentTimeMillis()
            val canShow = AdPolicy.canShowInterstitial(
                status = GameStatus.GAME_OVER,
                state = interstitialState,
                nowMillis = now,
                isAdFree = false,
                hasConsent = false,
            )
            // Policy is host-testable: no SDK call here, only state update when allowed.
            interstitialState = AdPolicy.onGameOver(interstitialState)
            onInterstitialStateChanged?.invoke(interstitialState)
            if (canShow) {
                interstitialState = AdPolicy.onInterstitialShown(interstitialState, now)
                onInterstitialStateChanged?.invoke(interstitialState)
            }
        }
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

    fun updateStorageError(value: Boolean) {
        storageError = value
    }

    fun retryStorage() {
        storageError = false
    }

    /** Restore from persistence — exact board/score/keep-playing flag/RNG position. */
    fun restore(
        restoredGame: Game,
        restoredStats: Stats? = null,
        restoredMode: GameMode = GameMode.CLASSIC,
        restoredDailyEpochDay: Long? = null,
    ) {
        game = restoredGame
        scene = BoardScene.still(restoredGame.board)
        best = maxOf(restoredStats?.bestScore ?: 0, restoredGame.score, best)
        if (restoredStats != null) stats = restoredStats
        mode = restoredMode
        dailyEpochDay = restoredDailyEpochDay
        showWinDialog = restoredGame.status == GameStatus.WON
        showGameOverDialog = restoredGame.status == GameStatus.GAME_OVER
        isNewBest = false
        hintVisible = restoredGame.moves == 0
        undoAllowance = UndoAllowance()
        refreshDialogs()
    }

    fun startDaily(epochDay: Long) {
        val dailyGame = DailyChallenge.newDailyGame(epochDay)
        game = dailyGame
        scene = BoardScene.still(dailyGame.board)
        mode = GameMode.DAILY
        dailyEpochDay = epochDay
        showWinDialog = false
        showGameOverDialog = false
        hintVisible = true
        paused = false
        undoAllowance = UndoAllowance()
        onGameChanged?.invoke(dailyGame)
    }

    fun onDailyComplete(
        todayEpochDay: Long,
        score: Int,
    ) {
        val newStats = DailyChallenge.updateStatsOnDailyComplete(stats, todayEpochDay, score)
        stats = newStats
        best = maxOf(best, newStats.bestScore)
        onStatsChanged?.invoke(newStats)
    }

    fun updateStatsExplicit(newStats: Stats) {
        stats = newStats
        best = maxOf(best, newStats.bestScore)
    }

    private fun refreshDialogs() {
        showWinDialog = game.status == GameStatus.WON
        showGameOverDialog = game.status == GameStatus.GAME_OVER
    }

    private fun handleWin() {
        val updated = stats.copy(
            wins = stats.wins + 1,
            bestScore = maxOf(stats.bestScore, game.score),
            highestTile = maxOf(stats.highestTile, game.board.highestTile),
        )
        stats = updated
        onStatsChanged?.invoke(updated)
        best = maxOf(best, updated.bestScore)
    }

    private fun handleGameOver() {
        // Stats: gamesPlayed, best, highestTile, wins
        val won = game.board.highestTile >= Game.WINNING_TILE
        val oldBest = stats.bestScore
        val updated = DailyChallenge.updateStatsOnClassicEnd(stats, game, won)
        val finalUpdated =
            if (mode == GameMode.DAILY && dailyEpochDay != null) {
                DailyChallenge.updateStatsOnDailyComplete(updated, dailyEpochDay!!, game.score)
            } else {
                updated
            }
        isNewBest = finalUpdated.bestScore > oldBest
        stats = finalUpdated
        onStatsChanged?.invoke(finalUpdated)
        best = maxOf(best, finalUpdated.bestScore)
        showGameOverDialog = true
        interstitialState = AdPolicy.onGameOver(interstitialState)
        onInterstitialStateChanged?.invoke(interstitialState)
    }

    private fun updateBestStats(next: Game) {
        if (next.score > stats.bestScore) {
            val updated = stats.copy(
                bestScore = next.score,
                highestTile = maxOf(stats.highestTile, next.board.highestTile),
            )
            stats = updated
            onStatsChanged?.invoke(updated)
        } else if (next.board.highestTile > stats.highestTile) {
            val updated = stats.copy(highestTile = next.board.highestTile)
            stats = updated
            onStatsChanged?.invoke(updated)
        }
    }

    /** The board the pause sheet quotes (\"highest tile 512\"). */
    val highestTile: Int
        get() = scene.tiles.maxOfOrNull { it.value } ?: 0
}
