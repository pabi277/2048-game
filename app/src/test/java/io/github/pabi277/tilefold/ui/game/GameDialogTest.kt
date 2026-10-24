@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import io.github.pabi277.tilefold.domain.Board
import io.github.pabi277.tilefold.domain.Game
import io.github.pabi277.tilefold.domain.GameSnapshot
import io.github.pabi277.tilefold.domain.GameStatus
import io.github.pabi277.tilefold.domain.Stats
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class GameDialogTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `reaching 2048 shows win dialog with Keep playing`() {
        val winGame = winGame()

        rule.setContent {
            TilefoldTheme {
                val session = remember { GameSession(seed = 1L, initialGame = winGame) }
                val actions = session.actions()
                GameContent(session = session, actions = actions, reducedMotion = true)
            }
        }

        rule.onNodeWithTag(WIN_DIALOG_TAG).assertIsDisplayed()
        rule.onNodeWithText("You made 2048").assertIsDisplayed()
        rule.onNodeWithText("Keep playing").assertIsDisplayed()
        rule.onNodeWithText("New game").assertIsDisplayed()
    }

    @Test
    fun `keep playing dismisses win dialog and resumes play`() {
        val winGame = winGame()

        rule.setContent {
            TilefoldTheme {
                val session = remember { GameSession(seed = 1L, initialGame = winGame) }
                val actions = session.actions()
                GameContent(session = session, actions = actions, reducedMotion = true)
            }
        }

        rule.onNodeWithText("Keep playing").performClick()

        rule.onNodeWithTag(WIN_DIALOG_TAG).assertDoesNotExist()
    }

    @Test
    fun `game over shows dialog with final score and new best`() {
        val overGame = gameOverGame(score = 3120, bestScore = 12480)

        rule.setContent {
            TilefoldTheme {
                val stats = Stats(bestScore = 2000)
                val session =
                    remember {
                        GameSession(seed = 1L, initialGame = overGame, initialStats = stats)
                    }
                val actions = session.actions()
                GameContent(session = session, actions = actions, reducedMotion = true)
            }
        }

        rule.onNodeWithTag(GAME_OVER_DIALOG_TAG).assertIsDisplayed()
        rule.onNodeWithText("Game over").assertIsDisplayed()
        rule.onNodeWithTag(NEW_BEST_TAG).assertIsDisplayed()
        rule.onNodeWithText("Undo last move").assertIsDisplayed()
        rule.onNodeWithText("New game").assertIsDisplayed()
    }

    @Test
    fun `undo after game over restores board`() {
        val start = Game.newGame(20261024L)
        val moved = start.move(io.github.pabi277.tilefold.domain.Direction.LEFT)
        // Force a game-over snapshot by building a dead board with an undo.
        val deadBoard =
            Board.rows(
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 2),
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 2),
            )
        val deadSnapshot =
            GameSnapshot(deadBoard, score = 100, randomState = 1L, reached2048 = false, keepPlaying = false, moves = 10)
        val deadGame = Game.restoreWithUndo(deadSnapshot, moved.snapshot)

        rule.setContent {
            TilefoldTheme {
                val session = remember { GameSession(seed = 1L, initialGame = deadGame) }
                val actions = session.actions()
                GameContent(session = session, actions = actions, reducedMotion = true)
            }
        }

        rule.onNodeWithTag(GAME_OVER_DIALOG_TAG).assertIsDisplayed()
        rule.onNodeWithText("Undo last move").performClick()
        rule.onNodeWithTag(SCORE_TAG).assertTextEquals("4")
    }

    @Test
    fun `undo allowance one free then rewarded up to three`() {
        val session = GameSession(seed = 20261024L)
        // Play two moves to have undo history.
        session.move(io.github.pabi277.tilefold.domain.Direction.LEFT)
        session.move(io.github.pabi277.tilefold.domain.Direction.DOWN)

        // First undo consumes free.
        assertThat(session.undo()).isTrue()
        assertThat(session.undoAllowance.used).isEqualTo(1)

        // No more free without rewarded.
        session.move(io.github.pabi277.tilefold.domain.Direction.LEFT)
        assertThat(session.undo()).isFalse()

        // Earn rewarded.
        assertThat(session.earnRewardedUndo()).isTrue()
        assertThat(session.undo()).isTrue()
    }

    @Test
    fun `restart confirm dialog appears and confirms`() {
        rule.setContent {
            TilefoldTheme {
                val session = remember { GameSession(seed = 20261024L) }
                val state = session.screenState.copy(showRestartConfirm = true)
                val actions = session.actions()
                GameScreen(state = state, actions = actions, reducedMotion = true)
            }
        }

        rule.onNodeWithTag(CONFIRM_DIALOG_TAG).assertIsDisplayed()
        rule.onNodeWithText("Restart this game?").assertIsDisplayed()
    }

    @Test
    fun `storage error shows banner`() {
        rule.setContent {
            TilefoldTheme {
                val session = remember { GameSession(seed = 1L).apply { setStorageError(true) } }
                val actions = session.actions()
                GameScreen(state = session.screenState, actions = actions, reducedMotion = true)
            }
        }

        rule.onNodeWithText("Progress isn't saving on this device").assertIsDisplayed()
    }

    private fun winGame(): Game {
        val board =
            Board.rows(
                listOf(1024, 1024, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
            )
        val snapshot =
            GameSnapshot(
                board,
                score = 0,
                randomState = 42L,
                reached2048 = false,
                keepPlaying = false,
                moves = 0,
            )
        val game = Game.restore(snapshot)
        return game.move(io.github.pabi277.tilefold.domain.Direction.LEFT)
    }

    @Suppress("UnusedParameter")
    private fun gameOverGame(
        score: Int,
        @Suppress("UnusedParameter") bestScore: Int,
    ): Game {
        val board =
            Board.rows(
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 2),
                listOf(2, 4, 2, 4),
                listOf(4, 2, 4, 2),
            )
        val snapshot =
            GameSnapshot(
                board,
                score = score,
                randomState = 42L,
                reached2048 = false,
                keepPlaying = false,
                moves = 5,
            )
        return Game.restore(snapshot)
    }

    private fun assertThat(value: Boolean) = Truth.assertThat(value)
}
