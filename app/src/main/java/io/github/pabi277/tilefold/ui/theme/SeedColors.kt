@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Seed and provisional surfaces for the phase-0 theme.
 *
 * These values are the *proposal* recorded in `docs/UI-REFERENCE.md`. They are
 * not frozen: the owner approves the UI reference site in phase 0-D and only
 * then do these become the token set every screen must use. No screen may
 * introduce its own colour value.
 */
internal object SeedColors {
    /** Warm terracotta. The single seed the whole Material 3 scheme is built from. */
    val Seed = Color(0xFFB4552F)

    val LightBackground = Color(0xFFFAF6EF)
    val LightSurface = Color(0xFFFFFFFF)
    val LightOnSurface = Color(0xFF2C241C)
    val LightOnSurfaceVariant = Color(0xFF6B5C4A)
    val LightOutline = Color(0xFFE3D9C8)
    val LightSecondaryContainer = Color(0xFFD9CDB9)
    val LightOnSecondaryContainer = Color(0xFF2C241C)
    val LightError = Color(0xFF9C3B32)

    val DarkBackground = Color(0xFF161311)
    val DarkSurface = Color(0xFF241F1B)
    val DarkOnSurface = Color(0xFFF3ECE2)
    val DarkOnSurfaceVariant = Color(0xFFB7A794)
    val DarkOutline = Color(0xFF43392F)
    val DarkSecondaryContainer = Color(0xFF3A322A)
    val DarkOnSecondaryContainer = Color(0xFFF3ECE2)
    val DarkError = Color(0xFFE08A5E)

    /** Text drawn on top of [Seed] (used for buttons and the title accent). */
    val OnSeed = Color(0xFFFFF8F0)
}
