@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.unit.dp

/**
 * The app's icon set: the exact **Lucide** glyphs the approved reference uses
 * (`docs/UI-REFERENCE.md` § Icons), adapted into Compose `ImageVector`s.
 *
 * Lucide is **ISC**-licensed (Copyright (c) Lucide Contributors). The path data
 * below is the published 24x24 geometry of `lucide-static` 1.61.0, converted
 * mechanically — no glyph was redrawn by hand and no other icon set is mixed in.
 * The notice ships on the licences screen (phase 4) and is logged in
 * `docs/DECISIONS.md`.
 *
 * Colours are never baked in: every path is stroked black and tinted by
 * `Icon(tint = …)` from the theme's tokens.
 */
internal object TilefoldIcons {
    /** Lucide `house` — Home. */
    val House: ImageVector by lazy {
        strokeIcon(
            name = "house",
            {
                moveTo(15f, 21f)
                lineTo(15f, 13f)
                arcTo(1f, 1f, 0f, false, false, 14f, 12f)
                lineTo(10f, 12f)
                arcTo(1f, 1f, 0f, false, false, 9f, 13f)
                lineTo(9f, 21f)
            },
            {
                moveTo(3f, 10f)
                arcTo(2f, 2f, 0f, false, true, 3.709f, 8.472f)
                lineTo(10.709f, 2.472f)
                arcTo(2f, 2f, 0f, false, true, 13.291f, 2.472f)
                lineTo(20.291f, 8.472f)
                arcTo(2f, 2f, 0f, false, true, 21f, 10f)
                lineTo(21f, 19f)
                arcTo(2f, 2f, 0f, false, true, 19f, 21f)
                lineTo(5f, 21f)
                arcTo(2f, 2f, 0f, false, true, 3f, 19f)
                close()
            },
        )
    }

    /** Lucide `rotate-ccw` — Restart. */
    val RotateCcw: ImageVector by lazy {
        strokeIcon(
            name = "rotate-ccw",
            {
                moveTo(3f, 12f)
                arcTo(9f, 9f, 0f, true, false, 12f, 3f)
                arcTo(9.75f, 9.75f, 0f, false, false, 5.26f, 5.74f)
                lineTo(3f, 8f)
            },
            {
                moveTo(3f, 3f)
                lineTo(3f, 8f)
                lineTo(8f, 8f)
            },
        )
    }

    /** Lucide `undo-2` — Undo. */
    val Undo2: ImageVector by lazy {
        strokeIcon(
            name = "undo-2",
            {
                moveTo(9f, 14f)
                lineTo(4f, 9f)
                lineTo(9f, 4f)
            },
            {
                moveTo(4f, 9f)
                lineTo(14.5f, 9f)
                arcTo(5.5f, 5.5f, 0f, false, true, 20f, 14.5f)
                arcTo(5.5f, 5.5f, 0f, false, true, 14.5f, 20f)
                lineTo(11f, 20f)
            },
        )
    }

    /** Lucide `arrow-up` — Up. */
    val ArrowUp: ImageVector by lazy {
        strokeIcon(
            name = "arrow-up",
            {
                moveTo(5f, 12f)
                lineTo(12f, 5f)
                lineTo(19f, 12f)
            },
            {
                moveTo(12f, 19f)
                lineTo(12f, 5f)
            },
        )
    }

    /** Lucide `arrow-down` — Down. */
    val ArrowDown: ImageVector by lazy {
        strokeIcon(
            name = "arrow-down",
            {
                moveTo(12f, 5f)
                lineTo(12f, 19f)
            },
            {
                moveTo(19f, 12f)
                lineTo(12f, 19f)
                lineTo(5f, 12f)
            },
        )
    }

    /** Lucide `arrow-left` — Left. */
    val ArrowLeft: ImageVector by lazy {
        strokeIcon(
            name = "arrow-left",
            {
                moveTo(12f, 19f)
                lineTo(5f, 12f)
                lineTo(12f, 5f)
            },
            {
                moveTo(19f, 12f)
                lineTo(5f, 12f)
            },
        )
    }

    /** Lucide `arrow-right` — Right. */
    val ArrowRight: ImageVector by lazy {
        strokeIcon(
            name = "arrow-right",
            {
                moveTo(5f, 12f)
                lineTo(19f, 12f)
            },
            {
                moveTo(12f, 5f)
                lineTo(19f, 12f)
                lineTo(12f, 19f)
            },
        )
    }

    /** Lucide `play` — Resume. */
    val Play: ImageVector by lazy {
        strokeIcon(
            name = "play",
            {
                moveTo(5f, 5f)
                arcTo(2f, 2f, 0f, false, true, 8.008f, 3.272f)
                lineTo(20.005f, 10.27f)
                arcTo(2f, 2f, 0f, false, true, 20.008f, 13.728f)
                lineTo(8.008f, 20.728f)
                arcTo(2f, 2f, 0f, false, true, 5f, 19f)
                close()
            },
        )
    }

    /**
     * Builds a 24×24 stroke-only vector.
     *
     * Path nodes are assembled with [PathBuilder] and handed straight to
     * [ImageVector.Builder.addPath], so the geometry here is exactly the
     * geometry extracted from the SVG — no wrapper DSL in between.
     */
    private fun strokeIcon(
        name: String,
        vararg shapes: PathBuilder.() -> Unit,
    ): ImageVector {
        val builder =
            ImageVector.Builder(
                name = name,
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = VIEWPORT,
                viewportHeight = VIEWPORT,
            )
        shapes.forEach { shape ->
            builder.addPath(
                pathData = PathBuilder().apply(shape).getNodes(),
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
