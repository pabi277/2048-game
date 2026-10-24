@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
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
class StatsScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun statsEmpty() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                StatsScreen(stats = Stats())
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/stats-empty.png")
    }

    @Test
    fun statsPopulated() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                StatsScreen(
                    stats =
                        Stats(
                            bestScore = 12480,
                            gamesPlayed = 128,
                            wins = 14,
                            highestTile = 1024,
                            dailyStreak = 4,
                            bestDailyScore = 3240,
                        ),
                )
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/stats-populated.png")
    }

    @Test
    fun statsDarkLarge() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2.0f)) {
                TilefoldTheme(darkTheme = true) {
                    StatsScreen(
                        stats =
                            Stats(
                                bestScore = 12480,
                                gamesPlayed = 128,
                                wins = 14,
                                highestTile = 1024,
                                dailyStreak = 4,
                                bestDailyScore = 3240,
                            ),
                    )
                }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/stats-dark-large.png")
    }

    @Test
    fun statsStorageError() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                StatsScreen(stats = Stats(), storageError = true)
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/stats-storage-error.png")
    }
}
