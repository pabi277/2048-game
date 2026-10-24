package io.github.pabi277.tilefold.ui.theme

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * The numeral growth caps from the frozen reference (`docs/UI-REFERENCE.md`
 * § Type scale): tile numerals stop at 1.25×, in-screen score values at 1.5×,
 * dialog scores at 1.25×, so a 2.0× system font scale can never overflow a
 * square cell or a dialog.
 *
 * Android has no per-text font-scale cap, so the size is *divided* by the
 * current font scale and multiplied by the cap: the final pixels still scale
 * with the system setting, just no further than the cap.
 */
internal object TextCaps {
    /**
     * @param base the token size in sp at 1.0× scale
     * @param cap the maximum growth factor (1.25 / 1.5)
     * @param fontScale the system font scale, `LocalDensity.current.fontScale`
     */
    fun capped(
        base: TextUnit,
        cap: Float,
        fontScale: Float,
    ): TextUnit {
        if (fontScale <= cap || fontScale <= 0f) return base
        return (base.value * (cap / fontScale)).sp
    }
}
