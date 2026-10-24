// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.domain

/**
 * Persisted app settings — phase 4 (§ Data schema sketch in RESEARCH.md).
 *
 * Fields here directly map to the switches/segmented controls the reference
 * renders in `SettingsScreen` (§ ui.jsx): sound, haptics, theme, language and
 * the direction-pad visibility. Theme contains GOLD even though the switcher
 * disables it until the milestone is reached; keeping the enum closed makes
 * decoding total.
 */
data class Settings(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val theme: ThemeChoice = ThemeChoice.SYSTEM,
    val language: Language = Language.EN,
    val dpadVisible: Boolean = false,
)

/**
 * Theme choice exactly as the reference shows it: System / Light / Dark + Gold
 * (locked until the player reaches 2048).
 */
enum class ThemeChoice {
    SYSTEM,
    LIGHT,
    DARK,
    GOLD,
}

/** In-app language — the APK ships EN + HI and the switch takes effect without restart. */
enum class Language {
    EN,
    HI,
}
