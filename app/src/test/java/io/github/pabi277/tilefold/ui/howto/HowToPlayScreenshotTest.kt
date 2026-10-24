// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.howto

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

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class HowToPlayScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun howToPlay() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) {
                HowToPlayScreen(onBack = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/how-to-play.png")
    }

    @Test
    fun howToPlayDarkLargeHindi() {
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
                TilefoldTheme(darkTheme = true) {
                    HowToPlayScreen(onBack = {})
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/how-to-play-dark-large.png")
    }
}
