@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * Persisted statistics — the six facts the Stats screen shows.
 *
 * Every field has a default so an older payload still decodes, and
 * [StatsCodec] refuses an internally impossible payload rather than
 * repairing it (a board is never lost, but a stat is never invented).
 */
data class Stats(
    val bestScore: Int = 0,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val highestTile: Int = 0,
    val dailyStreak: Int = 0,
    val lastDailyEpochDay: Long? = null,
    val bestDailyScore: Int = 0,
) {
    init {
        require(bestScore >= 0) { "bestScore cannot be negative: $bestScore" }
        require(gamesPlayed >= 0) { "gamesPlayed cannot be negative: $gamesPlayed" }
        require(wins >= 0) { "wins cannot be negative: $wins" }
        require(highestTile >= 0) { "highestTile cannot be negative: $highestTile" }
        require(dailyStreak >= 0) { "dailyStreak cannot be negative: $dailyStreak" }
        require(bestDailyScore >= 0) { "bestDailyScore cannot be negative: $bestDailyScore" }
    }

    /** True when no game has ever been recorded — the empty state. */
    val isEmpty: Boolean
        get() = gamesPlayed == 0 && bestScore == 0 && highestTile == 0
}
