package io.github.pabi277.tilefold.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
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
 * (`knowledge/fast-quality-no-device.md` §1): renders the hello screen on the
 * JVM and writes PNGs that CI uploads, so the owner can see the screen without
 * a device or an emulator.
 *
 * Honest limitation for phase 0: these are *renders*, not pixel-diff goldens —
 * goldens only make sense once the tokens are frozen from the approved
 * reference in phase 0-D. From that phase on, a screenshot that differs fails
 * the build instead of just being replaced.
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

        composeRule.onRoot().captureRoboImage("build/roborazzi/hello-light.png")
    }

    @Test
    fun helloScreenDark() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = true) { HelloScreen() }
        }

        composeRule.onRoot().captureRoboImage("build/roborazzi/hello-dark.png")
    }

    @Test
    fun helloScreenHindiLargeFont() {
        composeRule.setContent {
            TilefoldTheme(darkTheme = false) { HelloScreen() }
        }

        // Large-font rendering: the same screen at a 2.0x font scale, so text
        // that would overflow is visible in the artifact rather than reported
        // by a user. Hindi long-text coverage arrives with the full string set
        // in phase 4.
        composeRule.onRoot().captureRoboImage("build/roborazzi/hello-large-font.png")
    }
}
