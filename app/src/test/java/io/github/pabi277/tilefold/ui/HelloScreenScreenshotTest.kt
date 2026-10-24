@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The screenshot lane of the no-device quality gate
 * (`knowledge/fast-quality-no-device.md` §1), and — from phase 0-D on — the
 * **UI contract**: the images under `app/src/test/screenshots/` are goldens, so
 * a visual difference fails `testDebugUnitTest` in CI instead of being quietly
 * re-rendered.
 *
 * Record deliberately (never on CI) with `./gradlew recordRoborazziDebug`;
 * review the new images against the approved reference before committing them.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class HelloScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun helloScreenLight() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) { HelloScreen() }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/hello-light.png")
    }

    @Test
    fun helloScreenDark() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = true) { HelloScreen() }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/hello-dark.png")
    }

    @Test
    fun helloScreenLargeFont() {
        // 2.0x font scale, the Android accessibility setting this app must
        // survive (rule.md §5). Hindi copy arrives with the full string set in
        // phase 4; the large-font run is proved here from phase 0-D on.
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = 2.0f),
            ) {
                TilefoldTheme(darkTheme = false) { HelloScreen() }
            }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/hello-large-font.png")
    }
}
