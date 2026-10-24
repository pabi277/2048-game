package io.github.pabi277.tilefold.ui.theme

import kotlin.math.pow

/**
 * WCAG 2.2 relative-luminance and contrast maths, in plain Kotlin so the palette
 * can be asserted in host tests instead of by eye.
 *
 * Channels are the 0.0..1.0 values Compose exposes as `Color.red/green/blue`.
 * Deliberately no packed-ARGB decoding: `Color.value` keeps the colour in its
 * high 32 bits (with the colour-space id in the low bits), and an earlier
 * version of this file read the wrong half and reported 1.0 for every pair.
 *
 * Used by `ContrastTest` today (proves the provisional tokens are readable) and
 * by the accessibility pass in phase 6.
 */
@Suppress("MagicNumber")
internal object Contrast {
    /** Relative luminance of a colour whose channels are 0.0..1.0, per WCAG 2.2. */
    fun relativeLuminance(
        red: Double,
        green: Double,
        blue: Double,
    ): Double {
        fun channel(value: Double): Double =
            if (value <= 0.03928) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)

        return 0.2126 * channel(red) + 0.7152 * channel(green) + 0.0722 * channel(blue)
    }

    /** Contrast ratio between two relative luminances, from 1.0 to 21.0. */
    fun contrastRatio(
        luminanceA: Double,
        luminanceB: Double,
    ): Double {
        val lighter = maxOf(luminanceA, luminanceB)
        val darker = minOf(luminanceA, luminanceB)
        return (lighter + 0.05) / (darker + 0.05)
    }
}
