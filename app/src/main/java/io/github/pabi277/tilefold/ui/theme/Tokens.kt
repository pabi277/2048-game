@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * THE design tokens of the app — one value, one place.
 *
 * These are the values the UI reference renders (`design/reference/src/design.js`)
 * and the values tabulated in `docs/UI-REFERENCE.md`. `TokensSyncTest` parses
 * that document and fails when this file and the frozen tables disagree, so a
 * token cannot drift between the reference, the doc and the app.
 *
 * Rules (`rule.md` §5): no screen may define its own colour, spacing or type
 * value. A new value enters here first, and the reference first before that.
 */
internal object Tokens {
    /** The single seed both schemes derive from: warm terracotta. */
    val Seed = Color(0xFFB4552F)

    object Light {
        val Background = Color(0xFFFAF6EF)
        val Surface = Color(0xFFFFFFFF)
        val SurfaceVariant = Color(0xFFF1E9DC)
        val ScoreBox = Color(0xFFD9CDB9)
        val ScoreLabel = Color(0xFF5C4F3F)
        val ScoreValue = Color(0xFF2C241C)
        val BoardFrame = Color(0xFFE6DCCB)
        val EmptyCell = Color(0xFFD3C7B2)
        val Accent = Color(0xFFB4552F)
        val OnAccent = Color(0xFFFFF8F0)
        val TextPrimary = Color(0xFF2C241C)
        val TextSecondary = Color(0xFF6B5C4A)
        val Divider = Color(0xFFE3D9C8)
        val OutlineVariant = Color(0xFFEFE6D8)
        val Error = Color(0xFF9C3B32)
        val OnError = Color(0xFFFFF8F0)
        val Success = Color(0xFF3F7A4B)
        val OnSuccess = Color(0xFFFFFFFF)
        val Skeleton = Color(0xFFEFE6D8)
        val SkeletonShine = Color(0xFFF8F2E8)
        val Scrim = Color(0x732C241C)
    }

    object Dark {
        val Background = Color(0xFF161311)
        val Surface = Color(0xFF241F1B)
        val SurfaceVariant = Color(0xFF2E2823)
        val ScoreBox = Color(0xFF3A322A)
        val ScoreLabel = Color(0xFFC9BBA8)
        val ScoreValue = Color(0xFFF3ECE2)
        val BoardFrame = Color(0xFF2B2520)
        val EmptyCell = Color(0xFF3A322A)
        val Accent = Color(0xFFE08A5E)
        val OnAccent = Color(0xFF1B1613)
        val TextPrimary = Color(0xFFF3ECE2)
        val TextSecondary = Color(0xFFB7A794)
        val Divider = Color(0xFF43392F)
        val OutlineVariant = Color(0xFF332C25)
        val Error = Color(0xFFE98A7B)
        val OnError = Color(0xFF1B1613)
        val Success = Color(0xFF6FAF7C)
        val OnSuccess = Color(0xFF12100E)
        val Skeleton = Color(0xFF2E2823)
        val SkeletonShine = Color(0xFF3A322A)
        val Scrim = Color(0x9E000000)
    }

    /** Gold milestone theme seed; its scheme is derived in phase 4. */
    val GoldSeed = Color(0xFFA97A1F)

    /** A tile's two colours: the rounded surface and the numeral on it. */
    data class TilePair(
        val background: Color,
        val numeral: Color,
    )

    /**
     * Tile ramp — value to colours. `> 2048` uses [SUPER]. Lookup is a single
     * pure function so every value 2…2048+ maps to exactly one pair (roadmap,
     * "Phase-level data/schema notes"); the sequence must stay in step with the
     * ramp table in `docs/UI-REFERENCE.md`.
     */
    private val lightRamp: Map<Int, TilePair> =
        mapOf(
            2 to TilePair(Color(0xFFF4EBDD), Color(0xFF6B5C4A)),
            4 to TilePair(Color(0xFFF1E2C6), Color(0xFF6B5C4A)),
            8 to TilePair(Color(0xFFF2CD9E), Color(0xFF7A4A22)),
            16 to TilePair(Color(0xFFF0B87B), Color(0xFF6E3F1C)),
            32 to TilePair(Color(0xFFEC9A5C), Color(0xFF5A3318)),
            64 to TilePair(Color(0xFFE57B45), Color(0xFF5A3318)),
            128 to TilePair(Color(0xFFDC5F35), Color(0xFFFFFFFF)),
            256 to TilePair(Color(0xFFC74A2C), Color(0xFFFFFFFF)),
            512 to TilePair(Color(0xFFB03B2A), Color(0xFFFFFFFF)),
            1024 to TilePair(Color(0xFFC89434), Color(0xFF2C241C)),
            2048 to TilePair(Color(0xFFA97A1F), Color(0xFFFFF6DE)),
        )

    private val darkRamp: Map<Int, TilePair> =
        mapOf(
            2 to TilePair(Color(0xFF3E362E), Color(0xFFEFE4D4)),
            4 to TilePair(Color(0xFF4A3F33), Color(0xFFEFE4D4)),
            8 to TilePair(Color(0xFF8A5A32), Color(0xFFFFF6EC)),
            16 to TilePair(Color(0xFFA2663A), Color(0xFFFFF6EC)),
            32 to TilePair(Color(0xFFB87244), Color(0xFFFFF6EC)),
            64 to TilePair(Color(0xFFC97F4B), Color(0xFFFFFFFF)),
            128 to TilePair(Color(0xFFD2653B), Color(0xFFFFFFFF)),
            256 to TilePair(Color(0xFFBC4E2E), Color(0xFFFFFFFF)),
            512 to TilePair(Color(0xFF9E3D28), Color(0xFFFFFFFF)),
            1024 to TilePair(Color(0xFFC39A46), Color(0xFF241F1B)),
            2048 to TilePair(Color(0xFFA8822E), Color(0xFFFFF6DE)),
        )

    private val SuperLight = TilePair(Color(0xFF6E4E12), Color(0xFFFFF6DE))
    private val SuperDark = TilePair(Color(0xFF7A5A16), Color(0xFFFFF6DE))

    /** Ramp steps in ascending order, for tests and the swatch screenshot. */
    val rampValues: List<Int> = listOf(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048)

    fun tile(
        value: Int,
        dark: Boolean,
    ): TilePair {
        val ramp = if (dark) darkRamp else lightRamp
        val superPair = if (dark) SuperDark else SuperLight
        return ramp[value] ?: superPair
    }

    fun tilePairs(dark: Boolean): Map<Int, TilePair> = if (dark) darkRamp else lightRamp

    fun superPair(dark: Boolean): TilePair = if (dark) SuperDark else SuperLight
}

/**
 * Measurements, in dp (1 CSS px in the reference = 1 dp here). Only the 4/8
 * scale exists; `Tokens` is the sole place a screen may read them from.
 */
internal object Dimens {
    val grid = listOf(4.dp, 8.dp, 12.dp, 16.dp, 24.dp, 32.dp)

    val ScreenPaddingHorizontal = 24.dp
    val ScreenPaddingVertical = 16.dp
    val MinTarget = 48.dp
    val IconButton = 48.dp
    val DirectionPadKey = 56.dp

    val ScoreGap = 12.dp
    val SameRowGap = 8.dp
    val ControlGap = 12.dp
    val BoardControlsGap = 16.dp

    val BoardPadding = 10.dp
    val TileGap = 8.dp
    val BoardElevation = 0.dp
    val ScoreBoxElevation = 0.dp
    val DirectionPadElevation = 2.dp

    val DialogMaxWidth = 320.dp
}

/** Corner radii, in dp. */
internal object Corners {
    val Board = 16.dp
    val Tile = 8.dp
    val ScoreBox = 12.dp
    val Button = 12.dp
    val Card = 16.dp
    val Banner = 12.dp
    val Dialog = 24.dp
    val SheetTop = 24.dp
}

/**
 * Type scale in sp, exactly the roles recorded in `docs/UI-REFERENCE.md`.
 * Android applies the user's font scale on top of these, which is why the sizes
 * are sp and not dp.
 */
internal object TypeScale {
    val AppTitle = 40.sp
    val AppTitleLine = 48.sp
    val AppTitleTracking = (-0.5).sp

    val ScreenTitle = 24.sp
    val ScreenTitleLine = 32.sp

    val DialogTitle = 22.sp
    val DialogTitleLine = 28.sp

    val ScoreValue = 26.sp
    val ScoreValueLine = 32.sp

    val ScoreLabel = 12.sp
    val ScoreLabelLine = 16.sp
    val ScoreLabelTracking = 0.96.sp // 0.08em at 12sp

    val Body = 16.sp
    val BodyLine = 24.sp

    val Secondary = 14.sp
    val SecondaryLine = 20.sp

    val Button = 16.sp
    val ButtonLine = 20.sp

    val Caption = 12.sp
    val CaptionLine = 16.sp

    /** Tile numerals by digit count: 1–2 digits 32sp, 3 → 26, 4 → 21, 5+ → 18. */
    fun tileNumeral(value: Int): TextUnit =
        when (value.toString().length) {
            1, 2 -> 32.sp
            3 -> 26.sp
            4 -> 21.sp
            else -> 18.sp
        }

    /**
     * Numeral growth caps, so a 2.0× font scale never breaks the board or a
     * dialog (deliberate native adaptation, recorded in `docs/UI-REFERENCE.md`).
     */
    const val TILE_NUMERAL_MAX_SCALE = 1.25f
    const val DIALOG_SCORE_MAX_SCALE = 1.25f
    const val IN_SCREEN_SCORE_MAX_SCALE = 1.5f
}
