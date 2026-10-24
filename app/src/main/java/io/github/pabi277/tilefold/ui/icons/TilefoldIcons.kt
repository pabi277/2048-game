@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The app's icon set: the exact **Lucide** glyphs the approved reference uses
 * (`docs/UI-REFERENCE.md` § Icons), adapted into Compose `ImageVector`s.
 *
 * Lucide is **ISC**-licensed (Copyright (c) Lucide Contributors). The path data
 * below is the published 24x24 geometry of `lucide-static` 1.61.0, converted
 * mechanically: each `d` attribute is handed to [PathParser] unchanged, so no
 * glyph was redrawn by hand and no other icon set is mixed in. The notice ships
 * on the licences screen (phase 4) and is logged in `docs/DECISIONS.md`.
 *
 * Colours are never baked in: every path is stroked black and tinted by
 * `Icon(tint = …)` from the theme's tokens.
 */
internal object TilefoldIcons {
    /** Lucide `house` — Home. */
    val House: ImageVector by lazy {
        strokeIcon(
            name = "house",
            paths =
                arrayOf(
                    "M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8",
                    "M3 10a2 2 0 0 1 .709-1.528l7-6a2 2 0 0 1 2.582 0l7 6A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 " +
                        "0 0 1-2-2z",
                ),
        )
    }

    /** Lucide `rotate-ccw` — Restart. */
    val RotateCcw: ImageVector by lazy {
        strokeIcon(
            name = "rotate-ccw",
            paths =
                arrayOf(
                    "M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8",
                    "M3 3v5h5",
                ),
        )
    }

    /** Lucide `undo-2` — Undo. */
    val Undo2: ImageVector by lazy {
        strokeIcon(
            name = "undo-2",
            paths =
                arrayOf(
                    "M9 14 4 9l5-5",
                    "M4 9h10.5a5.5 5.5 0 0 1 5.5 5.5a5.5 5.5 0 0 1-5.5 5.5H11",
                ),
        )
    }

    /** Lucide `arrow-up` — Pad up / pad toggle. */
    val ArrowUp: ImageVector by lazy {
        strokeIcon(
            name = "arrow-up",
            paths =
                arrayOf(
                    "m5 12 7-7 7 7",
                    "M12 19V5",
                ),
        )
    }

    /** Lucide `arrow-down` — Pad down. */
    val ArrowDown: ImageVector by lazy {
        strokeIcon(
            name = "arrow-down",
            paths =
                arrayOf(
                    "M12 5v14",
                    "m19 12-7 7-7-7",
                ),
        )
    }

    /** Lucide `arrow-left` — Pad left. */
    val ArrowLeft: ImageVector by lazy {
        strokeIcon(
            name = "arrow-left",
            paths =
                arrayOf(
                    "m12 19-7-7 7-7",
                    "M19 12H5",
                ),
        )
    }

    /** Lucide `arrow-right` — Pad right. */
    val ArrowRight: ImageVector by lazy {
        strokeIcon(
            name = "arrow-right",
            paths =
                arrayOf(
                    "M5 12h14",
                    "m12 5 7 7-7 7",
                ),
        )
    }

    /** Lucide `play` — Resume. */
    val Play: ImageVector by lazy {
        strokeIcon(
            name = "play",
            paths =
                arrayOf(
                    "M5 5a2 2 0 0 1 3.008-1.728l11.997 6.998a2 2 0 0 1 .003 3.458l-12 7A2 2 0 0 1 5 19z",
                ),
        )
    }

    /** Lucide `circle-help` — Banner. */
    val Help: ImageVector by lazy {
        strokeIcon(
            name = "circle-help",
            paths =
                arrayOf(
                    "M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3",
                    "M12 17h.01",
                    "M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0Z",
                ),
        )
    }

    /** Lucide `x` — Dismiss. */
    val X: ImageVector by lazy {
        strokeIcon(
            name = "x",
            paths =
                arrayOf(
                    "M18 6 6 18",
                    "m6 6 12 12",
                ),
        )
    }

    /** Lucide `chevron-right` — Settings row trail. */
    val ChevronRight: ImageVector by lazy {
        strokeIcon(
            name = "chevron-right",
            paths = arrayOf("m9 18 6-6-6-6"),
        )
    }

    /** Lucide `lock` — Gold locked. */
    val Lock: ImageVector by lazy {
        strokeIcon(
            name = "lock",
            paths = arrayOf(
                "M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6" +
                    "a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z",
            ),
        )
    }

    /** Lucide `settings` — Settings. */
    val Settings: ImageVector by lazy {
        strokeIcon(
            name = "settings",
            paths = arrayOf(
                "M12 20a8 8 0 1 0 0-16 8 8 0 0 0 0 16z",
                "M12 14a2 2 0 1 0 0-4 2 2 0 0 0 0 4z",
                "M12 2v2",
                "M12 20v2",
                "m5 5 1.5-1.5",
                "m5 14-1.5-1.5",
                "M20 12h2",
                "M2 12h2",
                "m19 5-1.5 1.5",
                "m5 19-1.5-1.5",
            ),
        )
    }

    /**
     * Builds a 24x24 stroke-only vector from SVG path strings.
     *
     * [PathParser] turns each `d` attribute into the `PathNode` list [ImageVector.Builder.addPath]
     * expects, so the geometry here is byte-for-byte the published Lucide geometry.
     */
    private fun strokeIcon(
        name: String,
        paths: Array<String>,
    ): ImageVector {
        val builder =
            ImageVector.Builder(
                name = name,
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = VIEWPORT,
                viewportHeight = VIEWPORT,
            )
        paths.forEach { data ->
            builder.addPath(
                pathData = PathParser().parsePathString(data).toNodes(),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = STROKE_WIDTH,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
        return builder.build()
    }

    private const val VIEWPORT = 24f
    private const val STROKE_WIDTH = 2f
}
