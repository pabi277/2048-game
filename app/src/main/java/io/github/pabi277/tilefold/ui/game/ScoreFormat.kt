package io.github.pabi277.tilefold.ui.game

import java.text.NumberFormat
import java.util.Locale

/**
 * Score numerals as the player reads them: grouped by the device locale
 * (`8,412` in English, `8.412` where that is the convention) and never
 * abbreviated, because the reference shows the exact number.
 */
internal object ScoreFormat {
    fun of(
        value: Int,
        locale: Locale = Locale.getDefault(),
    ): String = NumberFormat.getIntegerInstance(locale).format(value.toLong())
}
