@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

private val SEPARATOR = Regex(":?-{2,}:?")
private val HEX = Regex("#[0-9A-Fa-f]{6}")
private val RAMP_LABEL = Regex("\\d+|> ?\\d+")
private val NUMBER = Regex("\\d+")

/**
 * The Kotlin half of the 0-D freeze guard.
 *
 * `design/reference/scripts/check-tokens.mjs` proves the reference site and the
 * doc agree; this test proves the **app** agrees with the same doc, by parsing
 * the frozen tables out of `docs/UI-REFERENCE.md` and comparing every value.
 * Change a hex in either place and CI fails with evidence instead of a screen
 * quietly drifting from the approved design.
 *
 * It also re-checks contrast from the app's own values, so the a11y promise does
 * not depend on a web build.
 */
class TokensSyncTest {
    private val doc: String by lazy {
        // Gradle runs unit tests with the module directory as the working dir.
        val candidates = listOf("../docs/UI-REFERENCE.md", "docs/UI-REFERENCE.md")
        val found = candidates.firstOrNull { File(it).isFile }
        val file =
            requireNotNull(found) {
                "docs/UI-REFERENCE.md not found from ${File(".").absolutePath}"
            }
        File(file).readText()
    }

    private fun section(heading: String): String {
        val start = doc.indexOf(heading)
        assertThat(start).isAtLeast(0)
        val rest = doc.substring(start + heading.length)
        val next = Regex("\n#{3,} ").find(rest)
        val end = next?.range?.first ?: rest.length
        return rest.substring(0, end)
    }

    /** Markdown table rows as trimmed, backtick-free cells (header and separator dropped). */
    private fun rows(text: String): List<List<String>> {
        val table = mutableListOf<List<String>>()
        for (line in text.lines()) {
            val isTableRow = line.trimStart().startsWith("|")
            if (isTableRow) {
                val inner = line.trim().removePrefix("|").removeSuffix("|")
                val cells = mutableListOf<String>()
                for (cell in inner.split('|')) {
                    cells.add(cell.replace("`", "").replace("*", "").trim())
                }
                val isSeparator = cells.all { it.matches(SEPARATOR) }
                if (!isSeparator) {
                    table.add(cells)
                }
            }
        }
        return table
    }

    private fun hexOf(value: String): Color {
        val digits = value.removePrefix("#")
        val rgb = digits.substring(0, 6)
        return Color(("FF$rgb").toLong(16))
    }

    private fun documentedColors(heading: String): Map<String, Color> {
        val colors = mutableMapOf<String, Color>()
        for (cells in rows(section(heading))) {
            if (cells.size >= 3) {
                val match = HEX.find(cells[2])
                if (match != null) {
                    colors[cells[1]] = hexOf(match.value)
                }
            }
        }
        return colors
    }

    /** One ramp row as `key to pair`, or `null` when the row is not a ramp step. */
    private fun rampEntry(cells: List<String>): Pair<String, Tokens.TilePair>? {
        if (cells.size < 3) return null
        val label = cells[0]
        if (!label.matches(RAMP_LABEL)) return null
        val background = HEX.find(cells[1]) ?: return null
        val numeral = HEX.find(cells[2]) ?: return null
        val key = if (label.startsWith(">")) "super" else label
        return key to Tokens.TilePair(hexOf(background.value), hexOf(numeral.value))
    }

    private fun palette(dark: Boolean): Map<String, Color> {
        val darkRoles =
            mapOf(
                "bg" to Tokens.Dark.Background,
                "surface" to Tokens.Dark.Surface,
                "surfaceVariant" to Tokens.Dark.SurfaceVariant,
                "scoreBox" to Tokens.Dark.ScoreBox,
                "scoreLabel" to Tokens.Dark.ScoreLabel,
                "scoreValue" to Tokens.Dark.ScoreValue,
                "boardFrame" to Tokens.Dark.BoardFrame,
                "emptyCell" to Tokens.Dark.EmptyCell,
                "accent" to Tokens.Dark.Accent,
                "onAccent" to Tokens.Dark.OnAccent,
                "textPrimary" to Tokens.Dark.TextPrimary,
                "textSecondary" to Tokens.Dark.TextSecondary,
                "divider" to Tokens.Dark.Divider,
                "outlineVariant" to Tokens.Dark.OutlineVariant,
                "error" to Tokens.Dark.Error,
                "onError" to Tokens.Dark.OnError,
                "success" to Tokens.Dark.Success,
                "onSuccess" to Tokens.Dark.OnSuccess,
                "skeleton" to Tokens.Dark.Skeleton,
                "skeletonShine" to Tokens.Dark.SkeletonShine,
            )
        val lightRoles =
            mapOf(
                "bg" to Tokens.Light.Background,
                "surface" to Tokens.Light.Surface,
                "surfaceVariant" to Tokens.Light.SurfaceVariant,
                "scoreBox" to Tokens.Light.ScoreBox,
                "scoreLabel" to Tokens.Light.ScoreLabel,
                "scoreValue" to Tokens.Light.ScoreValue,
                "boardFrame" to Tokens.Light.BoardFrame,
                "emptyCell" to Tokens.Light.EmptyCell,
                "accent" to Tokens.Light.Accent,
                "onAccent" to Tokens.Light.OnAccent,
                "textPrimary" to Tokens.Light.TextPrimary,
                "textSecondary" to Tokens.Light.TextSecondary,
                "divider" to Tokens.Light.Divider,
                "outlineVariant" to Tokens.Light.OutlineVariant,
                "error" to Tokens.Light.Error,
                "onError" to Tokens.Light.OnError,
                "success" to Tokens.Light.Success,
                "onSuccess" to Tokens.Light.OnSuccess,
                "skeleton" to Tokens.Light.Skeleton,
                "skeletonShine" to Tokens.Light.SkeletonShine,
            )
        return if (dark) darkRoles else lightRoles
    }

    private fun luminance(color: Color): Double =
        Contrast.relativeLuminance(color.red.toDouble(), color.green.toDouble(), color.blue.toDouble())

    private fun ratio(
        foreground: Color,
        background: Color,
    ): Double {
        val foregroundLuminance = luminance(foreground)
        val backgroundLuminance = luminance(background)
        return Contrast.contrastRatio(foregroundLuminance, backgroundLuminance)
    }

    @Test
    fun `light colour roles match the frozen doc`() {
        val documented = documentedColors("### Colour — light theme")
        val actual = palette(dark = false)

        assertThat(documented.keys).isNotEmpty()
        assertThat(actual.keys).containsExactlyElementsIn(documented.keys)
        for ((token, color) in documented) {
            assertThat(actual.getValue(token)).isEqualTo(color)
        }
    }

    @Test
    fun `dark colour roles match the frozen doc`() {
        val documented = documentedColors("### Colour — dark theme")
        val actual = palette(dark = true)

        assertThat(documented.keys).isNotEmpty()
        assertThat(actual.keys).containsExactlyElementsIn(documented.keys)
        for ((token, color) in documented) {
            assertThat(actual.getValue(token)).isEqualTo(color)
        }
    }

    @Test
    fun `tile ramp matches the frozen doc in both themes`() {
        val headings = mapOf("### Tile ramp — light" to false, "### Tile ramp — dark" to true)
        for ((heading, dark) in headings) {
            val documented = mutableMapOf<String, Tokens.TilePair>()
            for (cells in rows(section(heading))) {
                val entry = rampEntry(cells)
                if (entry != null) {
                    documented[entry.first] = entry.second
                }
            }

            assertThat(documented.size).isEqualTo(12)
            for ((key, pair) in documented) {
                val actual = if (key == "super") Tokens.superPair(dark) else Tokens.tile(key.toInt(), dark)
                assertThat(actual).isEqualTo(pair)
            }
        }
    }

    @Test
    fun `every tile numeral keeps its contrast floor`() {
        for (dark in listOf(false, true)) {
            val pairs = Tokens.tilePairs(dark).toMutableMap()
            pairs[4096] = Tokens.superPair(dark)
            for ((value, pair) in pairs) {
                val contrast = ratio(pair.numeral, pair.background)
                assertThat(contrast).isAtLeast(3.0)
                assertThat(value).isAtLeast(2)
            }
        }
    }

    @Test
    fun `body size text keeps a 4_5 to 1 floor`() {
        val pairs =
            listOf(
                Tokens.Light.TextPrimary to Tokens.Light.Background,
                Tokens.Light.TextSecondary to Tokens.Light.Background,
                Tokens.Light.ScoreLabel to Tokens.Light.ScoreBox,
                Tokens.Light.ScoreValue to Tokens.Light.ScoreBox,
                Tokens.Light.OnAccent to Tokens.Light.Accent,
                Tokens.Light.Error to Tokens.Light.Surface,
                Tokens.Light.OnSuccess to Tokens.Light.Success,
                Tokens.Dark.TextPrimary to Tokens.Dark.Background,
                Tokens.Dark.TextSecondary to Tokens.Dark.Background,
                Tokens.Dark.ScoreLabel to Tokens.Dark.ScoreBox,
                Tokens.Dark.ScoreValue to Tokens.Dark.ScoreBox,
                Tokens.Dark.OnAccent to Tokens.Dark.Accent,
                Tokens.Dark.Error to Tokens.Dark.Surface,
                Tokens.Dark.OnSuccess to Tokens.Dark.Success,
            )
        for ((foreground, background) in pairs) {
            assertThat(ratio(foreground, background)).isAtLeast(4.5)
        }
    }

    @Test
    fun `the ramp covers every reachable value exactly once`() {
        // 2…2048 are the power-of-two steps; anything above folds into "super".
        val reachable = (1..11).map { 1 shl it }
        assertThat(Tokens.rampValues).containsExactlyElementsIn(reachable).inOrder()
        for (value in reachable) {
            assertThat(Tokens.tile(value, dark = false)).isNotEqualTo(Tokens.tile(value * 2, dark = false))
        }
        assertThat(Tokens.tile(4096, dark = false)).isEqualTo(Tokens.superPair(dark = false))
        assertThat(Tokens.tile(65536, dark = true)).isEqualTo(Tokens.superPair(dark = true))
    }

    @Test
    fun `type roles, numeral caps and spacing match the doc`() {
        val typeRows = rows(section("### Type scale"))

        fun roleNumbers(label: String): List<Int> {
            val row = typeRows.first { it[0] == label }
            val numbers = mutableListOf<Int>()
            for (match in NUMBER.findAll(row[1])) {
                numbers.add(match.value.toInt())
            }
            return numbers.take(3)
        }

        assertThat(roleNumbers("App title (Home)")).containsExactly(40, 48, 700).inOrder()
        assertThat(roleNumbers("Screen title")).containsExactly(24, 32, 600).inOrder()
        assertThat(roleNumbers("Score value")).containsExactly(26, 32, 700).inOrder()
        assertThat(roleNumbers("Body")).containsExactly(16, 24, 400).inOrder()

        // The doc's digit rule and the app's function must be the same function.
        assertThat(TypeScale.tileNumeral(2).value).isWithin(0.01f).of(32f)
        assertThat(TypeScale.tileNumeral(128).value).isWithin(0.01f).of(26f)
        assertThat(TypeScale.tileNumeral(1024).value).isWithin(0.01f).of(21f)
        assertThat(TypeScale.tileNumeral(16384).value).isWithin(0.01f).of(18f)
        assertThat(TypeScale.TILE_NUMERAL_MAX_SCALE).isEqualTo(1.25f)
        assertThat(TypeScale.DIALOG_SCORE_MAX_SCALE).isEqualTo(1.25f)
        assertThat(TypeScale.IN_SCREEN_SCORE_MAX_SCALE).isEqualTo(1.5f)

        val spacing = section("### Spacing, shape, elevation, motion")
        assertThat(spacing).contains("${Dimens.ScreenPaddingHorizontal.value.toInt()}dp horizontal")
        assertThat(spacing).contains("${Dimens.ScreenPaddingVertical.value.toInt()}dp top/bottom")
        assertThat(spacing).contains("${Dimens.BoardPadding.value.toInt()}dp")
        assertThat(spacing).contains("tile gap ${Dimens.TileGap.value.toInt()}dp")
        assertThat(spacing).contains("${Dimens.MinTarget.value.toInt()}dp")
        assertThat(spacing).contains("${Dimens.DirectionPadKey.value.toInt()}dp")
        assertThat(spacing).contains("max width ${Dimens.DialogMaxWidth.value.toInt()}dp")
        assertThat(spacing).contains("${Corners.Board.value.toInt()}dp")
        assertThat(spacing).contains("tile corner ${Corners.Tile.value.toInt()}dp")
        assertThat(spacing).contains("score box corner ${Corners.ScoreBox.value.toInt()}dp")
    }
}
