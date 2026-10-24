package io.github.pabi277.tilefold.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Host test (no device) for the palette maths and for the readability of the
 * *provisional* tokens in `docs/UI-REFERENCE.md`.
 *
 * If a token changes during phase 0-D review, these assertions re-run in CI and
 * fail before an unreadable combination reaches a screenshot.
 */
class ContrastTest {
    private fun ratio(foreground: Color, background: Color): Double =
        Contrast.contrastRatio(foreground.value.toLong(), background.value.toLong())

    @Test
    fun `black on white is the maximum ratio`() {
        val ratio = Contrast.contrastRatio(0xFF000000, 0xFFFFFFFF)

        assertThat(ratio).isWithin(0.01).of(21.0)
    }

    @Test
    fun `contrast is symmetric and never below one`() {
        val forward = ratio(SeedColors.Seed, SeedColors.OnSeed)
        val backward = ratio(SeedColors.OnSeed, SeedColors.Seed)

        assertThat(forward).isWithin(1e-9).of(backward)
        assertThat(forward).isAtLeast(1.0)
    }

    @Test
    fun `identical colours have a ratio of one`() {
        assertThat(ratio(SeedColors.Seed, SeedColors.Seed)).isWithin(1e-9).of(1.0)
    }

    @Test
    fun `seed button text is readable in the light scheme`() {
        // WCAG AA for normal text is 4.5:1.
        assertThat(ratio(SeedColors.Seed, SeedColors.OnSeed)).isAtLeast(4.5)
    }

    @Test
    fun `body text is readable on both backgrounds`() {
        val light = ratio(SeedColors.LightOnSurface, SeedColors.LightBackground)
        val dark = ratio(SeedColors.DarkOnSurface, SeedColors.DarkBackground)

        assertThat(light).isAtLeast(4.5)
        assertThat(dark).isAtLeast(4.5)
    }

    @Test
    fun `secondary text is readable on both backgrounds`() {
        val light = ratio(SeedColors.LightOnSurfaceVariant, SeedColors.LightBackground)
        val dark = ratio(SeedColors.DarkOnSurfaceVariant, SeedColors.DarkBackground)

        assertThat(light).isAtLeast(4.5)
        assertThat(dark).isAtLeast(4.5)
    }
}
