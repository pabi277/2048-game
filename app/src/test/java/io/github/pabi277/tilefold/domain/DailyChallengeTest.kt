@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DailyChallengeTest {
    @Test
    fun `same epoch day gives same board`() {
        val epochDay = 20376L // 2025-10-24
        val a = DailyChallenge.newDailyGame(epochDay)
        val b = DailyChallenge.newDailyGame(epochDay)

        assertThat(a.board).isEqualTo(b.board)
    }

    @Test
    fun `different days give different boards`() {
        val a = DailyChallenge.newDailyGame(100L)
        val b = DailyChallenge.newDailyGame(101L)

        assertThat(a.board).isNotEqualTo(b.board)
    }

    @Test
    fun `epoch day is millis divided by day`() {
        assertThat(DailyChallenge.epochDay(0L)).isEqualTo(0L)
        assertThat(DailyChallenge.epochDay(86_400_000L)).isEqualTo(1L)
        assertThat(DailyChallenge.epochDay(86_399_999L)).isEqualTo(0L)
    }

    @Test
    fun `streak increments when yesterday was last`() {
        val stats = Stats(dailyStreak = 2, lastDailyEpochDay = 10L)

        assertThat(DailyChallenge.streakAfter(stats, 11L)).isEqualTo(3)
    }

    @Test
    fun `streak resets after gap`() {
        val stats = Stats(dailyStreak = 5, lastDailyEpochDay = 10L)

        assertThat(DailyChallenge.streakAfter(stats, 12L)).isEqualTo(1)
    }

    @Test
    fun `streak unchanged when already completed today`() {
        val stats = Stats(dailyStreak = 3, lastDailyEpochDay = 10L)

        assertThat(DailyChallenge.streakAfter(stats, 10L)).isEqualTo(3)
    }

    @Test
    fun `daily board reproducible from fixed date seed`() {
        // Pin: epochDay 20261 (2025-07-01) always opens same two tiles.
        val game = DailyChallenge.newDailyGame(20261L)

        assertThat(game.board.values.count { it != 0 }).isEqualTo(2)
        assertThat(DailyChallenge.newDailyGame(20261L).board).isEqualTo(game.board)
    }

    @Test
    fun `update stats on daily complete tracks streak and best`() {
        val stats = Stats(dailyStreak = 1, lastDailyEpochDay = 10L, bestDailyScore = 100)
        val updated = DailyChallenge.updateStatsOnDailyComplete(stats, 11L, 200)

        assertThat(updated.dailyStreak).isEqualTo(2)
        assertThat(updated.lastDailyEpochDay).isEqualTo(11L)
        assertThat(updated.bestDailyScore).isEqualTo(200)
    }

    @Test
    fun `isCompletedToday true when same epoch day`() {
        val stats = Stats(lastDailyEpochDay = 10L)

        assertThat(DailyChallenge.isCompletedToday(stats, 10L)).isTrue()
        assertThat(DailyChallenge.isCompletedToday(stats, 11L)).isFalse()
    }
}
