package io.github.pabi277.tilefold.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Host test (no device) for the palette maths and for the readability of the
 * frozen tokens in `docs/UI-REFERENCE.md`.
 *
 * If a token changes during phase 0-D review, these assertions re-run in CI and
 * fail before an unreadable combination reaches a screenshot. `TokensSyncTest`
 * proves the values themselves equal the doc; this file proves the maths.
 */
class ContrastTest {
    private fun luminanceOf(color: Color): Double =
        Contrast.relativeLuminance(color.red.toDouble(), color.green.toDouble(), color.blue.toDouble())

    private fun ratio(
        foreground: Color,
        background: Color,
    ): Double = Contrast.contrastRatio(luminanceOf(foreground), luminanceOf(background))

    @Test
    fun `black on white is the maximum ratio`() {
        assertThat(ratio(Color.Black, Color.White)).isWithin(0.01).of(21.0)
    }

    @Test
    fun `a known wcag pair matches the published ratio`() {
        // #767676 on white is the canonical 4.54:1 example from WCAG material.
        assertThat(ratio(Color(0xFF767676), Color.White)).isWithin(0.05).of(4.54)
    }

    @Test
    fun `contrast is symmetric and never below one`() {
        val forward = ratio(Tokens.Seed, Tokens.Light.OnAccent)
        val backward = ratio(Tokens.Light.OnAccent, Tokens.Seed)

        assertThat(forward).isWithin(1e-9).of(backward)
        assertThat(forward).isAtLeast(1.0)
    }

    @Test
    fun `identical colours have a ratio of one`() {
        assertThat(ratio(Tokens.Seed, Tokens.Seed)).isWithin(1e-9).of(1.0)
    }

    @Test
    fun `button text is readable on the accent`() {
        // WCAG AA for normal text is 4.5:1.
        assertThat(ratio(Tokens.Seed, Tokens.Light.OnAccent)).isAtLeast(4.5)
    }

    @Test
    fun `body text is readable on both backgrounds`() {
        val light = ratio(Tokens.Light.TextPrimary, Tokens.Light.Background)
        val dark = ratio(Tokens.Dark.TextPrimary, Tokens.Dark.Background)

        assertThat(light).isAtLeast(4.5)
        assertThat(dark).isAtLeast(4.5)
    }

    @Test
    fun `secondary text is readable on both backgrounds`() {
        val light = ratio(Tokens.Light.TextSecondary, Tokens.Light.Background)
        val dark = ratio(Tokens.Dark.TextSecondary, Tokens.Dark.Background)

        assertThat(light).isAtLeast(4.5)
        assertThat(dark).isAtLeast(4.5)
    }
}
