package io.github.pabi277.tilefold.ui.theme

import kotlin.math.pow

/**
 * WCAG 2.2 relative-luminance and contrast maths, in plain Kotlin so the palette
 * can be asserted in host tests instead of by eye.
 *
 * Used by `ContrastTest` today (proves the provisional tokens are readable) and
 * by the accessibility pass in phase 6.
 */
@Suppress("MagicNumber")
internal object Contrast {
    /** Relative luminance of an 8-bit sRGB colour, per WCAG 2.2. */
    fun relativeLuminance(red: Int, green: Int, blue: Int): Double {
        fun channel(value: Int): Double {
            val c = value.coerceIn(0, 255) / 255.0
            return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(red) + 0.7152 * channel(green) + 0.0722 * channel(blue)
    }

    /** Contrast ratio between two relative luminances, from 1.0 to 21.0. */
    fun contrastRatio(luminanceA: Double, luminanceB: Double): Double {
        val lighter = maxOf(luminanceA, luminanceB)
        val darker = minOf(luminanceA, luminanceB)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /** Contrast ratio between two 0xAARRGGBB colours (alpha is ignored). */
    fun contrastRatio(colorA: Long, colorB: Long): Double {
        fun luminanceOf(color: Long): Double =
            relativeLuminance(
                red = ((color shr 16) and 0xFF).toInt(),
                green = ((color shr 8) and 0xFF).toInt(),
                blue = (color and 0xFF).toInt(),
            )
        return contrastRatio(luminanceOf(colorA), luminanceOf(colorB))
    }
}
