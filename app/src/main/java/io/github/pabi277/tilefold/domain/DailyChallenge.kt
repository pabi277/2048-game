@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * Date-seeded Daily Challenge — same board for everyone, local streak.
 *
 * The seed is the **UTC epoch-day** (`docs/DECISIONS.md` — Daily seed convention)
 * so \"same board for everyone\" holds across time zones. The local streak and
 * best daily score live inside [Stats]; this object only computes the pure
 * parts so tests can pin one date.
 */
object DailyChallenge {
    const val MILLIS_PER_DAY = 86_400_000L

    /** UTC epoch-day for [millis] — the number of days since 1970-01-01 UTC. */
    fun epochDay(millis: Long): Long = millis / MILLIS_PER_DAY

    /** The generator seed for the daily board on [epochDay]. */
    fun seedFor(epochDay: Long): Long = epochDay

    /**
     * Starts the daily board for [epochDay] — deterministic, two tiles, same
     * for every device that asks on the same UTC day.
     */
    fun newDailyGame(epochDay: Long): Game = Game.newGame(seedFor(epochDay))

    /**
     * Whether the daily on [todayEpochDay] has already been completed,
     * given [stats.lastDailyEpochDay].
     */
    fun isCompletedToday(
        stats: Stats,
        todayEpochDay: Long,
    ): Boolean = stats.lastDailyEpochDay == todayEpochDay

    /**
     * The streak after finishing the daily on [todayEpochDay].
     *
     * - first daily ever → 1
     * - yesterday was also completed → increment
     * - already completed today → unchanged (caller should not bump twice)
     * - gap >1 → reset to 1
     */
    fun streakAfter(
        stats: Stats,
        todayEpochDay: Long,
    ): Int {
        val last = stats.lastDailyEpochDay
        if (last == todayEpochDay) return stats.dailyStreak
        if (last == null) return 1
        return if (todayEpochDay - last == 1L) stats.dailyStreak + 1 else 1
    }

    /**
     * Stats update after a daily game finishes with [score].
     */
    fun updateStatsOnDailyComplete(
        stats: Stats,
        todayEpochDay: Long,
        score: Int,
    ): Stats {
        if (stats.lastDailyEpochDay == todayEpochDay) return stats
        val newStreak = streakAfter(stats, todayEpochDay)
        return stats.copy(
            dailyStreak = newStreak,
            lastDailyEpochDay = todayEpochDay,
            bestDailyScore = maxOf(stats.bestDailyScore, score),
        )
    }

    /** Stats update after a classic game finishes (best, wins, etc.). */
    fun updateStatsOnClassicEnd(
        stats: Stats,
        game: Game,
        won: Boolean,
    ): Stats {
        val best = maxOf(stats.bestScore, game.score)
        val highest = maxOf(stats.highestTile, game.board.highestTile)
        return stats.copy(
            bestScore = best,
            gamesPlayed = stats.gamesPlayed + 1,
            wins = if (won) stats.wins + 1 else stats.wins,
            highestTile = highest,
        )
    }
}
