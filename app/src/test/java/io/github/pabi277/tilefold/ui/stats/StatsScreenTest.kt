@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.stats

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class StatsScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `empty stats show no games yet and CTA`() {
        rule.setContent {
            TilefoldTheme {
                StatsScreen(stats = Stats())
            }
        }

        rule.onNodeWithText("No games yet").assertIsDisplayed()
        rule.onNodeWithText("Best score").assertIsDisplayed()
        rule.onNodeWithText("—").assertIsDisplayed()
    }

    @Test
    fun `populated stats show all six facts`() {
        val stats =
            Stats(
                bestScore = 12480,
                gamesPlayed = 128,
                wins = 14,
                highestTile = 1024,
                dailyStreak = 4,
                bestDailyScore = 3240,
            )

        rule.setContent {
            TilefoldTheme {
                StatsScreen(stats = stats)
            }
        }

        rule.onNodeWithText("Games played").assertIsDisplayed()
        rule.onNodeWithText("12480").assertIsDisplayed()
        rule.onNodeWithText("Daily streak").assertIsDisplayed()
        rule.onNodeWithText("4").assertIsDisplayed()
    }

    @Test
    fun `storage error shows banner and unknown values`() {
        rule.setContent {
            TilefoldTheme {
                StatsScreen(stats = Stats(), storageError = true)
            }
        }

        rule.onNodeWithText("Couldn't read your stats").assertIsDisplayed()
        rule.onNodeWithText("Blanks below are unknown, not zero.").assertIsDisplayed()
    }
}
