// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import android.content.res.Configuration
import android.os.LocaleList
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Locale

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class HomeScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeNoSave() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                HomeScreen(
                    state = HomeState(hasSavedGame = false, bestScore = 12480, isEmpty = false),
                    onNewGame = {},
                    onContinue = {},
                    onDaily = {},
                    onHowToPlay = {},
                    onStats = {},
                    onSettings = {},
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-no-save.png")
    }

    @Test
    fun homeInProgress() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                HomeScreen(
                    state = HomeState(hasSavedGame = true, savedScore = 8412, savedHighestTile = 512, bestScore = 12480),
                    onNewGame = {},
                    onContinue = {},
                    onDaily = {},
                    onHowToPlay = {},
                    onStats = {},
                    onSettings = {},
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-in-progress.png")
    }

    @Test
    fun homeDailyDone() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                HomeScreen(
                    state = HomeState(dailyCompleted = true, dailyStreak = 4, todaysScore = 1620, bestScore = 12480),
                    onNewGame = {},
                    onContinue = {},
                    onDaily = {},
                    onHowToPlay = {},
                    onStats = {},
                    onSettings = {},
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-daily-done.png")
    }

    @Test
    fun homeEmpty() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                HomeScreen(
                    state = HomeState(hasSavedGame = false, bestScore = 0, isEmpty = true),
                    onNewGame = {},
                    onContinue = {},
                    onDaily = {},
                    onHowToPlay = {},
                    onStats = {},
                    onSettings = {},
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-empty.png")
    }

    @Test
    fun homeStorageError() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                HomeScreen(
                    state = HomeState(hasSavedGame = false, bestScore = 12480, storageError = true),
                    onNewGame = {},
                    onContinue = {},
                    onDaily = {},
                    onHowToPlay = {},
                    onStats = {},
                    onSettings = {},
                    onRetry = {},
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-storage-error.png")
    }

    @Test
    fun homeLoading() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                HomeScreen(
                    state = HomeState(isLoading = true),
                    onNewGame = {},
                    onContinue = {},
                    onDaily = {},
                    onHowToPlay = {},
                    onStats = {},
                    onSettings = {},
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-loading.png")
    }

    @Test
    fun homeHindi() {
        val hiConfig = Configuration().apply {
            setLocales(LocaleList.forLanguageTags("hi"))
        }
        composeRule.setContent {
            val appContext = androidx.test.core.app.ApplicationProvider
                .getApplicationContext<android.content.Context>()
            val wrapped = appContext.createConfigurationContext(hiConfig)
            CompositionLocalProvider(
                LocalConfiguration provides hiConfig,
                LocalContext provides wrapped,
            ) {
                TilefoldTheme(darkTheme = false) {
                    HomeScreen(
                        state = HomeState(hasSavedGame = false, bestScore = 12480, isEmpty = false),
                        onNewGame = {},
                        onContinue = {},
                        onDaily = {},
                        onHowToPlay = {},
                        onStats = {},
                        onSettings = {},
                    )
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-hindi.png")
    }

    @Test
    fun homeHindiLargeFont() {
        val hiConfig = Configuration().apply {
            setLocales(LocaleList.forLanguageTags("hi"))
            fontScale = 2.0f
        }
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalConfiguration provides hiConfig,
                LocalDensity provides Density(density.density, fontScale = 2.0f),
            ) {
                TilefoldTheme(darkTheme = false) {
                    HomeScreen(
                        state = HomeState(hasSavedGame = false, bestScore = 12480, isEmpty = false),
                        onNewGame = {},
                        onContinue = {},
                        onDaily = {},
                        onHowToPlay = {},
                        onStats = {},
                        onSettings = {},
                    )
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/home-hindi-large.png")
    }
}
