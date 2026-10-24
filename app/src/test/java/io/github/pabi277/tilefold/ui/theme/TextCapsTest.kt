@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.theme

import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * The numeral caps: a 2.0× font scale must never break a square tile or a score
 * box, but text below the cap must scale normally (`docs/UI-REFERENCE.md`
 * § Type scale — "deliberate, so 2.0× never breaks a layout").
 */
class TextCapsTest {
    @Test
    fun `nothing changes below the cap`() {
        assertThat(TextCaps.capped(32.sp, 1.25f, 1.0f)).isEqualTo(32.sp)
        assertThat(TextCaps.capped(32.sp, 1.25f, 1.25f)).isEqualTo(32.sp)
        assertThat(TextCaps.capped(26.sp, 1.5f, 1.3f)).isEqualTo(26.sp)
    }

    @Test
    fun `a tile numeral stops at 1_25 times its size`() {
        val capped = TextCaps.capped(32.sp, 1.25f, 2.0f)

        // 32sp rendered at a 2.0x scale would be 64; capped it is 40.
        assertThat(capped.value).isWithin(0.01f).of(20f)
        assertThat(capped.value * 2.0f).isWithin(0.01f).of(40f)
    }

    @Test
    fun `an in-screen score stops at 1_5 times its size`() {
        val capped = TextCaps.capped(26.sp, 1.5f, 2.0f)

        assertThat(capped.value * 2.0f).isWithin(0.01f).of(39f)
    }

    @Test
    fun `the caps are the frozen token values`() {
        assertThat(TypeScale.TILE_NUMERAL_MAX_SCALE).isEqualTo(1.25f)
        assertThat(TypeScale.IN_SCREEN_SCORE_MAX_SCALE).isEqualTo(1.5f)
        assertThat(TypeScale.DIALOG_SCORE_MAX_SCALE).isEqualTo(1.25f)
    }

    @Test
    fun `a nonsense font scale falls back to the token size`() {
        assertThat(TextCaps.capped(20.sp, 1.25f, 0f)).isEqualTo(20.sp)
        assertThat(TextCaps.capped(20.sp, 1.25f, -1f)).isEqualTo(20.sp)
    }
}
