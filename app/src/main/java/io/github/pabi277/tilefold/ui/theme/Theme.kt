@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import io.github.pabi277.tilefold.domain.ThemeChoice

/**
 * One theme, built only from [Tokens]. There are no per-screen colour or type
 * constants anywhere else in the app; the values come from the owner-approved
 * reference site and are tabulated in `docs/UI-REFERENCE.md`.
 *
 * The app's Material 3 scheme is generated from the same seed the reference
 * uses, and the tile ramp is *not* part of the M3 scheme (Material has no slot
 * for "tile 512"), so it travels beside it in [TilefoldPalette].
 */
private val LightColors =
    lightColorScheme(
        primary = Tokens.Light.Accent,
        onPrimary = Tokens.Light.OnAccent,
        primaryContainer = Tokens.Light.ScoreBox,
        onPrimaryContainer = Tokens.Light.ScoreValue,
        secondary = Tokens.Light.TextSecondary,
        onSecondary = Tokens.Light.Surface,
        secondaryContainer = Tokens.Light.SurfaceVariant,
        onSecondaryContainer = Tokens.Light.TextPrimary,
        tertiary = Tokens.Light.Success,
        onTertiary = Tokens.Light.OnSuccess,
        background = Tokens.Light.Background,
        onBackground = Tokens.Light.TextPrimary,
        surface = Tokens.Light.Surface,
        onSurface = Tokens.Light.TextPrimary,
        surfaceVariant = Tokens.Light.SurfaceVariant,
        onSurfaceVariant = Tokens.Light.TextSecondary,
        outline = Tokens.Light.Divider,
        outlineVariant = Tokens.Light.OutlineVariant,
        error = Tokens.Light.Error,
        onError = Tokens.Light.OnError,
        scrim = Tokens.Light.Scrim,
    )

private val DarkColors =
    darkColorScheme(
        primary = Tokens.Dark.Accent,
        onPrimary = Tokens.Dark.OnAccent,
        primaryContainer = Tokens.Dark.ScoreBox,
        onPrimaryContainer = Tokens.Dark.ScoreValue,
        secondary = Tokens.Dark.TextSecondary,
        onSecondary = Tokens.Dark.Background,
        secondaryContainer = Tokens.Dark.SurfaceVariant,
        onSecondaryContainer = Tokens.Dark.TextPrimary,
        tertiary = Tokens.Dark.Success,
        onTertiary = Tokens.Dark.OnSuccess,
        background = Tokens.Dark.Background,
        onBackground = Tokens.Dark.TextPrimary,
        surface = Tokens.Dark.Surface,
        onSurface = Tokens.Dark.TextPrimary,
        surfaceVariant = Tokens.Dark.SurfaceVariant,
        onSurfaceVariant = Tokens.Dark.TextSecondary,
        outline = Tokens.Dark.Divider,
        outlineVariant = Tokens.Dark.OutlineVariant,
        error = Tokens.Dark.Error,
        onError = Tokens.Dark.OnError,
        scrim = Tokens.Dark.Scrim,
    )

private val GoldColors =
    lightColorScheme(
        primary = Tokens.GoldSeed,
        onPrimary = Tokens.Light.OnAccent,
        primaryContainer = Tokens.Light.ScoreBox,
        onPrimaryContainer = Tokens.Light.ScoreValue,
        secondary = Tokens.Light.TextSecondary,
        onSecondary = Tokens.Light.Surface,
        secondaryContainer = Tokens.Light.SurfaceVariant,
        onSecondaryContainer = Tokens.Light.TextPrimary,
        tertiary = Tokens.Light.Success,
        onTertiary = Tokens.Light.OnSuccess,
        background = Tokens.Light.Background,
        onBackground = Tokens.Light.TextPrimary,
        surface = Tokens.Light.Surface,
        onSurface = Tokens.Light.TextPrimary,
        surfaceVariant = Tokens.Light.SurfaceVariant,
        onSurfaceVariant = Tokens.Light.TextSecondary,
        outline = Tokens.Light.Divider,
        outlineVariant = Tokens.Light.OutlineVariant,
        error = Tokens.Light.Error,
        onError = Tokens.Light.OnError,
        scrim = Tokens.Light.Scrim,
    )

/** Type roles from `docs/UI-REFERENCE.md` § Type scale, mapped to M3 roles. */
internal val TilefoldTypography =
    Typography(
        displaySmall =
            TextStyle(
                fontSize = TypeScale.AppTitle,
                lineHeight = TypeScale.AppTitleLine,
                fontWeight = FontWeight.Bold,
                letterSpacing = TypeScale.AppTitleTracking,
            ),
        headlineSmall =
            TextStyle(
                fontSize = TypeScale.ScreenTitle,
                lineHeight = TypeScale.ScreenTitleLine,
                fontWeight = FontWeight.SemiBold,
            ),
        headlineMedium =
            TextStyle(
                fontSize = TypeScale.ScoreValue,
                lineHeight = TypeScale.ScoreValueLine,
                fontWeight = FontWeight.Bold,
            ),
        titleLarge =
            TextStyle(
                fontSize = TypeScale.ScreenTitle,
                lineHeight = TypeScale.ScreenTitleLine,
                fontWeight = FontWeight.SemiBold,
            ),
        bodyLarge =
            TextStyle(
                fontSize = TypeScale.Body,
                lineHeight = TypeScale.BodyLine,
                fontWeight = FontWeight.Normal,
            ),
        bodyMedium =
            TextStyle(
                fontSize = TypeScale.Secondary,
                lineHeight = TypeScale.SecondaryLine,
                fontWeight = FontWeight.Normal,
            ),
        bodySmall =
            TextStyle(
                fontSize = TypeScale.Caption,
                lineHeight = TypeScale.CaptionLine,
                fontWeight = FontWeight.Normal,
            ),
        labelLarge =
            TextStyle(
                fontSize = TypeScale.Button,
                lineHeight = TypeScale.ButtonLine,
                fontWeight = FontWeight.SemiBold,
            ),
        labelSmall =
            TextStyle(
                fontSize = TypeScale.ScoreLabel,
                lineHeight = TypeScale.ScoreLabelLine,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = TypeScale.ScoreLabelTracking,
            ),
    )

/** The shapes every surface uses — the same radii the reference renders. */
internal val TilefoldShapes =
    Shapes(
        extraSmall = RoundedCornerShape(Corners.Tile),
        small = RoundedCornerShape(Corners.Button),
        medium = RoundedCornerShape(Corners.Card),
        large = RoundedCornerShape(Corners.Board),
        extraLarge = RoundedCornerShape(Corners.Dialog),
    )

/**
 * Everything the M3 scheme has no slot for: tile ramp, board, scores, chips.
 * Internal, like [Tokens]: a public class may not expose an internal type.
 */
internal data class TilefoldPalette(
    val dark: Boolean,
    val boardFrame: Color,
    val emptyCell: Color,
    val scoreBox: Color,
    val scoreLabel: Color,
    val scoreValue: Color,
    val success: Color,
    val onSuccess: Color,
    val skeleton: Color,
    val skeletonShine: Color,
    val accent: Color,
    val onAccent: Color,
) {
    /** Value → colours, via the single pure ramp function. */
    fun tile(value: Int): Tokens.TilePair = Tokens.tile(value, dark)
}

private val LightPalette =
    TilefoldPalette(
        dark = false,
        boardFrame = Tokens.Light.BoardFrame,
        emptyCell = Tokens.Light.EmptyCell,
        scoreBox = Tokens.Light.ScoreBox,
        scoreLabel = Tokens.Light.ScoreLabel,
        scoreValue = Tokens.Light.ScoreValue,
        success = Tokens.Light.Success,
        onSuccess = Tokens.Light.OnSuccess,
        skeleton = Tokens.Light.Skeleton,
        skeletonShine = Tokens.Light.SkeletonShine,
        accent = Tokens.Light.Accent,
        onAccent = Tokens.Light.OnAccent,
    )

private val DarkPalette =
    TilefoldPalette(
        dark = true,
        boardFrame = Tokens.Dark.BoardFrame,
        emptyCell = Tokens.Dark.EmptyCell,
        scoreBox = Tokens.Dark.ScoreBox,
        scoreLabel = Tokens.Dark.ScoreLabel,
        scoreValue = Tokens.Dark.ScoreValue,
        success = Tokens.Dark.Success,
        onSuccess = Tokens.Dark.OnSuccess,
        skeleton = Tokens.Dark.Skeleton,
        skeletonShine = Tokens.Dark.SkeletonShine,
        accent = Tokens.Dark.Accent,
        onAccent = Tokens.Dark.OnAccent,
    )

private val GoldPalette =
    TilefoldPalette(
        dark = false,
        boardFrame = Tokens.Light.BoardFrame,
        emptyCell = Tokens.Light.EmptyCell,
        scoreBox = Tokens.Light.ScoreBox,
        scoreLabel = Tokens.Light.ScoreLabel,
        scoreValue = Tokens.Light.ScoreValue,
        success = Tokens.Light.Success,
        onSuccess = Tokens.Light.OnSuccess,
        skeleton = Tokens.Light.Skeleton,
        skeletonShine = Tokens.Light.SkeletonShine,
        accent = Tokens.GoldSeed,
        onAccent = Tokens.Light.OnAccent,
    )

internal val LocalTilefoldPalette = staticCompositionLocalOf { LightPalette }

/** Shorthand for the current palette, e.g. `TilefoldThemeDefaults.palette.tile(128)`. */
internal object TilefoldThemeDefaults {
    val palette: TilefoldPalette
        @Composable get() = LocalTilefoldPalette.current
}

private fun paletteFor(choice: ThemeChoice, systemDark: Boolean): TilefoldPalette =
    when (choice) {
        ThemeChoice.LIGHT -> LightPalette
        ThemeChoice.DARK -> DarkPalette
        ThemeChoice.GOLD -> GoldPalette
        ThemeChoice.SYSTEM -> if (systemDark) DarkPalette else LightPalette
    }

private fun colorsFor(choice: ThemeChoice, systemDark: Boolean) =
    when (choice) {
        ThemeChoice.LIGHT -> LightColors
        ThemeChoice.DARK -> DarkColors
        ThemeChoice.GOLD -> GoldColors
        ThemeChoice.SYSTEM -> if (systemDark) DarkColors else LightColors
    }

@Composable
fun TilefoldTheme(
    themeChoice: ThemeChoice = ThemeChoice.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = paletteFor(themeChoice, darkTheme)
    val colors = colorsFor(themeChoice, darkTheme)
    CompositionLocalProvider(LocalTilefoldPalette provides palette) {
        MaterialTheme(
            colorScheme = colors,
            typography = TilefoldTypography,
            shapes = TilefoldShapes,
            content = content,
        )
    }
}
