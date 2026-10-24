// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.home

/**
 * State for the Home screen — mirrors the 6 variants the reference renders:
 * `#home-no-save`, `#home-in-progress`, `#home-daily-done`, `#home-empty`,
 * `#home-storage-error`, `#home-loading`.
 */
internal data class HomeState(
    val hasSavedGame: Boolean = false,
    val savedScore: Int = 0,
    val savedHighestTile: Int = 0,
    val isDailyMode: Boolean = false,
    val dailyCompleted: Boolean = false,
    val dailyStreak: Int = 0,
    val todaysScore: Int = 0,
    val bestScore: Int = 0,
    val isEmpty: Boolean = false,
    val storageError: Boolean = false,
    val isLoading: Boolean = false,
    val dailyLabelDate: String = "24 Oct",
)

internal fun HomeState.isInProgress(): Boolean = hasSavedGame && !isLoading

internal fun HomeState.showBoardPreview(): Boolean = !isInProgress() && !dailyCompleted
