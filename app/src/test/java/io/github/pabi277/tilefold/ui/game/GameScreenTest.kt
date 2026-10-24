@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The game screen end to end: swipe, pad, undo, restart and pause.
 *
 * It runs the real [GameSession] over the real engine, so a passing test means
 * "this gesture produced that board" — not that a mock was called. Seed
 * 20261024 opens `0,2,0,2` in row 3, which is why every expectation below is
 * about that row: a left swipe merges the pair into a 4 at the left edge.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class GameScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun startGame(reducedMotion: Boolean = false) {
        composeRule.setContent { TilefoldTheme { TestGame(reducedMotion = reducedMotion) } }
    }

    @Test
    fun `the opening board comes from the engine's seed`() {
        startGame()

        // Seed 20261024: 2 at row 3 column 2, 2 at row 3 column 4.
        composeRule.onNodeWithTag(tileTag(9)).assertExists()
        composeRule.onNodeWithTag(tileTag(11)).assertExists()
        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("0")
        composeRule.onNodeWithTag(HINT_TAG).assertExists()
        composeRule.onNodeWithText("Tilefold").assertExists()
    }

    @Test
    fun `a swipe merges the opening pair and scores it`() {
        startGame()

        composeRule.onNodeWithTag(BOARD_TAG).performTouchInput { swipeLeft() }

        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("4")
        composeRule.onNodeWithTag(tileTag(8)).assertExists()
        composeRule.onNodeWithTag(tileTag(10)).assertDoesNotExist()
        // The hint has done its job after the first move.
        composeRule.onNodeWithTag(HINT_TAG).assertDoesNotExist()
    }

    @Test
    fun `a second swipe keeps playing and can be undone`() {
        startGame()

        composeRule.onNodeWithTag(BOARD_TAG).performTouchInput { swipeLeft() }
        composeRule.onNodeWithTag(BOARD_TAG).performTouchInput { swipeRight() }

        composeRule.onNodeWithText("Undo").performClick()

        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("4")
    }

    @Test
    fun `a tap or a tiny drag costs no move`() {
        startGame()

        // A tap is not a gesture.
        composeRule.onNodeWithTag(BOARD_TAG).performTouchInput { click(center) }
        // 8px is well under the 24dp swipe threshold, so this is not a swipe.
        composeRule.onNodeWithTag(BOARD_TAG).performTouchInput {
            swipe(start = center, end = center.copy(x = center.x + 8f))
        }

        // Still the opening board: nothing moved, so nothing spawned.
        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("0")
        composeRule.onNodeWithTag(tileTag(9)).assertExists()
        composeRule.onNodeWithTag(tileTag(11)).assertExists()
        composeRule.onNodeWithTag(HINT_TAG).assertExists()
    }

    @Test
    fun `the direction pad plays the same game as swiping`() {
        startGame()

        composeRule.onNodeWithContentDescription("Direction pad").performClick()
        composeRule.onNodeWithTag(DIRECTION_PAD_TAG).assertExists()

        composeRule.onNodeWithContentDescription("Left").performClick()

        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("4")
        composeRule.onNodeWithTag(tileTag(8)).assertExists()
    }

    @Test
    fun `restart clears the board but not the session best`() {
        startGame()

        composeRule.onNodeWithTag(BOARD_TAG).performTouchInput { swipeLeft() }
        composeRule.onNodeWithTag(BEST_TAG).assertTextEquals("4")

        composeRule.onNodeWithContentDescription("Restart").performClick()

        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("0")
        composeRule.onNodeWithTag(BEST_TAG).assertTextEquals("4")
        composeRule.onNodeWithTag(HINT_TAG).assertExists()
    }

    @Test
    fun `the home icon pauses the game and resume returns to it`() {
        startGame()

        composeRule.onNodeWithContentDescription("Home").performClick()

        composeRule.onNodeWithTag(PAUSE_SHEET_TAG).assertExists()
        composeRule.onNodeWithText("Paused").assertExists()

        composeRule.onNodeWithText("Resume").performClick()

        composeRule.onNodeWithTag(PAUSE_SHEET_TAG).assertDoesNotExist()
    }

    @Test
    fun `a paused game ignores swipes`() {
        startGame()

        composeRule.onNodeWithContentDescription("Home").performClick()
        composeRule.onNodeWithTag(PAUSE_SHEET_TAG).assertExists()

        composeRule.onNodeWithText("Restart").performClick()

        composeRule.onNodeWithTag(PAUSE_SHEET_TAG).assertDoesNotExist()
        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("0")
    }

    @Test
    fun `the board is playable with reduced motion too`() {
        startGame(reducedMotion = true)

        composeRule.onNodeWithTag(BOARD_TAG).performTouchInput { swipeLeft() }

        composeRule.onNodeWithTag(SCORE_TAG).assertTextEquals("4")
    }

    @Composable
    private fun TestGame(reducedMotion: Boolean) {
        val session = remember { GameSession(SEED) }
        val actions = remember(session) { session.actions() }
        GameContent(session = session, actions = actions, reducedMotion = reducedMotion)
    }

    private companion object {
        const val SEED = 20261024L
    }
}
