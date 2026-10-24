@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.pabi277.tilefold.domain.Board
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.domain.GameStatus
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The phase-2 screenshot **goldens**: the game screen in the states the approved
 * reference draws, rendered from the app's own tokens so a visual difference
 * fails `testDebugUnitTest` instead of being noticed on a phone.
 *
 * Every case renders a *still* scene ([BoardScene.still]) — no animation is in
 * flight — so the images are deterministic. The comparison against
 * `design/reference/screenshots/07-game-new.png`, `08-game-mid.png`,
 * `09-game-paused.png`, `10-game-dark.png` and `11-game-large-font.png` is
 * recorded in `docs/phases/02-board-ui.md`.
 *
 * Record deliberately with `./gradlew recordRoborazziDebug` (or the CI
 * `[record-goldens]` job), never by deleting a case.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class GameScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun gameNew() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                Screen(board = FRESH, score = 0, best = 12480, hintVisible = true, canUndo = false)
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-new.png")
    }

    @Test
    fun gameMid() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                Screen(board = MID, score = 8412, best = 12480, hintVisible = false, canUndo = true)
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-mid.png")
    }

    @Test
    fun gameDark() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = true) {
                Screen(board = MID, score = 8412, best = 12480, hintVisible = false, canUndo = true)
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-dark.png")
    }

    @Test
    fun gameLargeFont() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2.0f)) {
                TilefoldTheme(darkTheme = false) {
                    Screen(board = MID, score = 8412, best = 12480, hintVisible = false, canUndo = true)
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-large-font.png")
    }

    @Test
    fun gamePaused() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                Box(Modifier) {
                    Screen(
                        board = MID,
                        score = 8412,
                        best = 12480,
                        hintVisible = false,
                        canUndo = true,
                        dimmed = true,
                    )
                    PauseSheet(
                        score = 8412,
                        highestTile = 1024,
                        onResume = {},
                        onRestart = {},
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-paused.png")
    }

    @Test
    fun gamePadding() {
        // The direction pad is optional (settings, phase 4). Drawing it in its own
        // golden keeps its spacing reviewable without hiding it behind a setting.
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                Screen(
                    board = MID,
                    score = 8412,
                    best = 12480,
                    hintVisible = false,
                    canUndo = true,
                    padVisible = true,
                )
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-pad.png")
    }

    @Test
    fun gameStorageError() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                Screen(
                    board = MID,
                    score = 8412,
                    best = 12480,
                    hintVisible = false,
                    canUndo = true,
                    storageError = true,
                )
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-storage-error.png")
    }

    @Test
    fun win() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                ScreenWithDialog(
                    board = WIN,
                    score = 21044,
                    best = 21044,
                    winDialog = true,
                )
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/win.png")
    }

    @Test
    fun winDarkLarge() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2.0f)) {
                TilefoldTheme(darkTheme = true) {
                    ScreenWithDialog(
                        board = WIN,
                        score = 21044,
                        best = 21044,
                        winDialog = true,
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/win-dark-large.png")
    }

    @Test
    fun gameOver() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                ScreenWithDialog(
                    board = OVER,
                    score = 3120,
                    best = 3120,
                    gameOverDialog = true,
                    isNewBest = true,
                )
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-over.png")
    }

    @Test
    fun gameOverDarkLarge() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2.0f)) {
                TilefoldTheme(darkTheme = true) {
                    ScreenWithDialog(
                        board = OVER,
                        score = 3120,
                        best = 12480,
                        gameOverDialog = true,
                        isNewBest = false,
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/game-over-dark-large.png")
    }

    @Test
    fun confirmRestart() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                GameScreen(
                    state =
                        GameScreenState(
                            scene = BoardScene.still(MID),
                            score = 8412,
                            best = 12480,
                            status = GameStatus.PLAYING,
                            canUndo = true,
                            hintVisible = false,
                            padVisible = false,
                            dimmed = true,
                            showRestartConfirm = true,
                        ),
                    actions = NO_ACTIONS,
                    reducedMotion = true,
                )
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/confirm-restart.png")
    }

    @Composable
    private fun Screen(
        board: Board,
        score: Int,
        best: Int,
        hintVisible: Boolean,
        canUndo: Boolean,
        dimmed: Boolean = false,
        padVisible: Boolean = false,
        storageError: Boolean = false,
    ) {
        GameScreen(
            state =
                GameScreenState(
                    scene = BoardScene.still(board),
                    score = score,
                    best = best,
                    status = GameStatus.PLAYING,
                    canUndo = canUndo,
                    hintVisible = hintVisible,
                    padVisible = padVisible,
                    dimmed = dimmed,
                    storageError = storageError,
                ),
            actions = NO_ACTIONS,
            reducedMotion = true,
        )
    }

    @Composable
    private fun ScreenWithDialog(
        board: Board,
        score: Int,
        best: Int,
        winDialog: Boolean = false,
        gameOverDialog: Boolean = false,
        isNewBest: Boolean = false,
    ) {
        GameScreen(
            state =
                GameScreenState(
                    scene = BoardScene.still(board),
                    score = score,
                    best = best,
                    status = if (winDialog) GameStatus.WON else if (gameOverDialog) GameStatus.GAME_OVER else GameStatus.PLAYING,
                    canUndo = true,
                    hintVisible = false,
                    padVisible = false,
                    dimmed = true,
                    winDialogVisible = winDialog,
                    gameOverDialogVisible = gameOverDialog,
                    isNewBest = isNewBest,
                    canEarnUndo = false,
                ),
            actions = NO_ACTIONS,
            reducedMotion = true,
        )
    }

    private companion object {
        /** A screenshot cannot press anything; the actions exist only to satisfy the screen. */
        val NO_ACTIONS =
            GameActions(
                onMove = { _: Direction -> },
                onUndo = {},
                onRestart = {},
                onHome = {},
                onTogglePad = {},
            )

        /** The reference's `BOARDS.fresh` (`design/reference/src/screens.jsx`). */
        val FRESH =
            Board.rows(
                listOf(0, 2, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 4, 0),
                listOf(0, 0, 0, 0),
            )

        /** The reference's `BOARDS.mid` — every ramp step from 2 to 1024, plus gaps. */
        val MID =
            Board.rows(
                listOf(2, 4, 8, 16),
                listOf(32, 64, 128, 256),
                listOf(512, 1024, 2, 0),
                listOf(0, 8, 32, 64),
            )

        val WIN =
            Board.rows(
                listOf(2048, 1024, 512, 256),
                listOf(4, 8, 16, 32),
                listOf(2, 4, 8, 16),
                listOf(0, 2, 0, 4),
            )

        val OVER =
            Board.rows(
                listOf(2, 4, 8, 16),
                listOf(16, 8, 4, 2),
                listOf(2, 4, 8, 16),
                listOf(16, 8, 4, 2),
            )
    }
}
