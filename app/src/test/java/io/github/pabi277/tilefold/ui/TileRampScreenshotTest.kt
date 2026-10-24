@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import io.github.pabi277.tilefold.ui.theme.TilefoldThemeDefaults
import io.github.pabi277.tilefold.ui.theme.Tokens
import io.github.pabi277.tilefold.ui.theme.TypeScale
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Screenshot **goldens** for the frozen tile ramp (phase 0-D.3).
 *
 * The reference site renders the same steps; this test renders them with the
 * app's own [Tokens], so a difference between the approved design and the
 * Compose values fails the build instead of surprising us in phase 2.
 *
 * The composable below lives in the test source set on purpose: it is evidence,
 * not app UI, so it never ships and cannot be mistaken for a screen.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class TileRampScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tileRampLight() {
        composeRule.setContent { TilefoldTheme(darkTheme = false) { TileRamp() } }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/tile-ramp-light.png")
    }

    @Test
    fun tileRampDark() {
        composeRule.setContent { TilefoldTheme(darkTheme = true) { TileRamp() } }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/tile-ramp-dark.png")
    }
}

@Composable
private fun TileRamp() {
    val palette = TilefoldThemeDefaults.palette
    val steps = Tokens.rampValues + 4096
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Tile ramp — ${if (palette.dark) "dark" else "light"}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        steps.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { value ->
                    val pair = palette.tile(value)
                    Box(
                        modifier =
                            Modifier
                                .size(72.dp)
                                .background(pair.background, RoundedCornerShape(Corners.Tile)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = value.toString(),
                            color = pair.numeral,
                            fontSize = TypeScale.tileNumeral(value),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        Text(
            text = "score box",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp),
        )
        ScoreSwatch(palette.scoreLabel, palette.scoreValue)
    }
}

@Composable
private fun ScoreSwatch(
    label: Color,
    value: Color,
) {
    val palette = TilefoldThemeDefaults.palette
    Box(
        modifier =
            Modifier
                .background(palette.scoreBox, RoundedCornerShape(Corners.ScoreBox))
                .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "SCORE", style = MaterialTheme.typography.labelSmall, color = label)
            Text(text = "8,412", style = MaterialTheme.typography.headlineMedium, color = value)
        }
    }
}
