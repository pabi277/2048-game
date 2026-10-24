@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * One theme, generated from one seed (`SeedColors.Seed`). Per
 * `knowledge/app-engineering.md` §3.1 there are no per-screen colour or font
 * constants anywhere else in the app.
 *
 * The scheme is hand-derived from the seed today because the palette table in
 * `docs/UI-REFERENCE.md` is still a proposal; phase 0-D freezes it, and a later
 * phase may swap in generated tonal palettes from the same seed without changing
 * a single screen.
 */
private val LightColors =
    lightColorScheme(
        primary = SeedColors.Seed,
        onPrimary = SeedColors.OnSeed,
        primaryContainer = SeedColors.LightSecondaryContainer,
        onPrimaryContainer = SeedColors.LightOnSecondaryContainer,
        secondary = SeedColors.LightOnSurfaceVariant,
        onSecondary = SeedColors.LightSurface,
        secondaryContainer = SeedColors.LightSecondaryContainer,
        onSecondaryContainer = SeedColors.LightOnSecondaryContainer,
        background = SeedColors.LightBackground,
        onBackground = SeedColors.LightOnSurface,
        surface = SeedColors.LightSurface,
        onSurface = SeedColors.LightOnSurface,
        surfaceVariant = SeedColors.LightSecondaryContainer,
        onSurfaceVariant = SeedColors.LightOnSurfaceVariant,
        outline = SeedColors.LightOutline,
        error = SeedColors.LightError,
        onError = SeedColors.LightSurface,
    )

private val DarkColors =
    darkColorScheme(
        primary = SeedColors.DarkPrimary,
        onPrimary = SeedColors.DarkBackground,
        primaryContainer = SeedColors.DarkSecondaryContainer,
        onPrimaryContainer = SeedColors.DarkOnSecondaryContainer,
        secondary = SeedColors.DarkOnSurfaceVariant,
        onSecondary = SeedColors.DarkBackground,
        secondaryContainer = SeedColors.DarkSecondaryContainer,
        onSecondaryContainer = SeedColors.DarkOnSecondaryContainer,
        background = SeedColors.DarkBackground,
        onBackground = SeedColors.DarkOnSurface,
        surface = SeedColors.DarkSurface,
        onSurface = SeedColors.DarkOnSurface,
        surfaceVariant = SeedColors.DarkSecondaryContainer,
        onSurfaceVariant = SeedColors.DarkOnSurfaceVariant,
        outline = SeedColors.DarkOutline,
        error = SeedColors.DarkError,
        onError = SeedColors.DarkBackground,
    )

/** Type roles from `docs/UI-REFERENCE.md` § Type scale, mapped to M3 roles. */
private val TilefoldTypography =
    Typography(
        displaySmall =
            TextStyle(
                fontSize = 40.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
            ),
        headlineSmall =
            TextStyle(
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        bodyLarge =
            TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
            ),
        bodyMedium =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
            ),
        labelLarge =
            TextStyle(
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        labelSmall =
            TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
            ),
    )

@Composable
fun TilefoldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = TilefoldTypography,
        content = content,
    )
}
