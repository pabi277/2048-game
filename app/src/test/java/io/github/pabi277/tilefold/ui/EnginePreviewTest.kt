@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.pabi277.tilefold.domain.Board
import io.github.pabi277.tilefold.domain.Game
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The debug preview has to *work*, not look designed: one click on an arrow must
 * move tiles, and undo must put them back. This is the host-side version of
 * "install the progress APK and press the arrows" (roadmap 1.4).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class EnginePreviewTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `the preview opens on the seeded board with four arrows`() {
        composeRule.setContent { TilefoldTheme { EnginePreview(seed = SEED) } }

        composeRule.onNodeWithTag(ENGINE_STATUS_TAG).assertTextEquals("Score 0 · Moves 0 · PLAYING")
        listOf("←", "↑", "↓", "→").forEach { arrow ->
            composeRule.onNodeWithText(arrow).assertExists()
        }
        // Seed 20261024 opens 0,2,0,2 on the third row.
        composeRule.onNodeWithTag(engineCellTag(2, 1)).assertTextEquals("2")
        composeRule.onNodeWithTag(engineCellTag(2, 3)).assertTextEquals("2")
        composeRule.onNodeWithTag(engineCellTag(0, 0)).assertDoesNotExist()
    }

    @Test
    fun `an arrow click moves tiles and undo puts them back`() {
        composeRule.setContent { TilefoldTheme { EnginePreview(seed = SEED) } }

        composeRule.onNodeWithText("↓").performClick()

        composeRule.onNodeWithTag(ENGINE_STATUS_TAG).assertTextEquals("Score 0 · Moves 1 · PLAYING")
        composeRule.onNodeWithTag(engineCellTag(3, 1)).assertTextEquals("2")
        composeRule.onNodeWithTag(engineCellTag(3, 3)).assertTextEquals("2")
        composeRule.onNodeWithTag(engineCellTag(2, 1)).assertDoesNotExist()

        composeRule.onNodeWithText("Undo").performClick()

        composeRule.onNodeWithTag(ENGINE_STATUS_TAG).assertTextEquals("Score 0 · Moves 0 · PLAYING")
        composeRule.onNodeWithTag(engineCellTag(2, 1)).assertTextEquals("2")
    }

    @Test
    fun `new game keeps the board playable`() {
        composeRule.setContent { TilefoldTheme { EnginePreview(seed = SEED) } }

        composeRule.onNodeWithText("↓").performClick()
        composeRule.onNodeWithText("New game").performClick()

        composeRule.onNodeWithTag(ENGINE_STATUS_TAG).assertTextEquals("Score 0 · Moves 0 · PLAYING")
        composeRule.onNodeWithTag(ENGINE_BOARD_TAG).assertExists()
    }

    @Test
    fun `the preview draws a full 4x4 grid`() {
        composeRule.setContent { TilefoldTheme { EnginePreview(seed = SEED) } }

        val game = Game.newGame(SEED)
        repeat(Board.WIDTH) { row ->
            repeat(Board.WIDTH) { col ->
                if (game.board.valueAt(row, col) != 0) {
                    composeRule.onNodeWithTag(engineCellTag(row, col)).assertExists()
                }
            }
        }
    }

    private companion object {
        const val SEED = ENGINE_PREVIEW_SEED
    }
}
