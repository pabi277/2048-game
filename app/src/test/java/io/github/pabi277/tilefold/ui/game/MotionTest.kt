@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import com.google.common.truth.Truth.assertThat
import io.github.pabi277.tilefold.ui.theme.Durations
import org.junit.Test
import java.util.Locale

/**
 * Motion and number formatting: the durations are the frozen tokens, and reduced
 * motion turns every one of them into a snap.
 */
class MotionTest {
    @Test
    fun `the durations are the frozen tokens`() {
        assertThat(Motion.slide(reduced = false)).isEqualTo(Durations.SLIDE_MS)
        assertThat(Motion.pop(reduced = false)).isEqualTo(Durations.POP_MS)
        assertThat(Motion.spawn(reduced = false)).isEqualTo(Durations.SPAWN_MS)
        assertThat(Motion.score(reduced = false)).isEqualTo(Durations.SCORE_MS)
        assertThat(listOf(Durations.SLIDE_MS, Durations.POP_MS, Durations.SPAWN_MS, Durations.SCORE_MS))
            .containsExactly(110, 140, 130, 220)
            .inOrder()
    }

    @Test
    fun `reduced motion snaps every transition`() {
        assertThat(Motion.slide(reduced = true)).isEqualTo(0)
        assertThat(Motion.pop(reduced = true)).isEqualTo(0)
        assertThat(Motion.spawn(reduced = true)).isEqualTo(0)
        assertThat(Motion.score(reduced = true)).isEqualTo(0)
    }

    @Test
    fun `scores are grouped exactly, never abbreviated`() {
        assertThat(ScoreFormat.of(0, Locale.US)).isEqualTo("0")
        assertThat(ScoreFormat.of(941, Locale.US)).isEqualTo("941")
        assertThat(ScoreFormat.of(8412, Locale.US)).isEqualTo("8,412")
        assertThat(ScoreFormat.of(12480, Locale.US)).isEqualTo("12,480")
        assertThat(ScoreFormat.of(1234567, Locale.US)).isEqualTo("1,234,567")
    }

    @Test
    fun `grouping follows the device locale`() {
        assertThat(ScoreFormat.of(8412, Locale.GERMANY)).isEqualTo("8.412")
    }
}
