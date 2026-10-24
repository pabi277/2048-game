// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.settings

import io.github.pabi277.tilefold.domain.Language
import io.github.pabi277.tilefold.domain.ThemeChoice

/**
 * State for Settings screen.
 *
 * Gold unlock is derived from stats (highestTile >= 2048), not persisted here.
 */
internal data class SettingsState(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val theme: ThemeChoice = ThemeChoice.SYSTEM,
    val language: Language = Language.EN,
    val dpadVisible: Boolean = false,
    val goldUnlocked: Boolean = false,
    val isStoreUnavailable: Boolean = false,
    val isAdFree: Boolean = false,
    val versionName: String = "0.4.0",
)
