@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * JSON codec for [Stats] — mirrors [SaveCodec] but for the stats blob.
 *
 * Schema version 1, defensive decode: unknown keys ignored, missing fields
 * defaulted, impossible values refused (returns null).
 */
object StatsCodec {
    const val CURRENT_SCHEMA_VERSION = 1

    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = false
        }

    fun encode(stats: Stats): String = json.encodeToString(StatsDto.from(stats))

    fun decode(payload: String?): Stats? {
        if (payload.isNullOrBlank()) return null
        val dto = runCatching { json.decodeFromString<StatsDto>(payload) }.getOrNull() ?: return null
        return dto.toStats()
    }

    @Serializable
    private data class StatsDto(
        val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
        val bestScore: Int = 0,
        val gamesPlayed: Int = 0,
        val wins: Int = 0,
        val highestTile: Int = 0,
        val dailyStreak: Int = 0,
        val lastDailyEpochDay: Long? = null,
        val bestDailyScore: Int = 0,
    ) {
        fun toStats(): Stats? {
            if (schemaVersion < 1 || schemaVersion > CURRENT_SCHEMA_VERSION) return null
            if (isImpossible()) return null
            return Stats(
                bestScore = bestScore,
                gamesPlayed = gamesPlayed,
                wins = wins,
                highestTile = highestTile,
                dailyStreak = dailyStreak,
                lastDailyEpochDay = lastDailyEpochDay,
                bestDailyScore = bestDailyScore,
            )
        }

        private fun isImpossible(): Boolean =
            bestScore < 0 ||
                gamesPlayed < 0 ||
                wins < 0 ||
                highestTile < 0 ||
                dailyStreak < 0 ||
                bestDailyScore < 0 ||
                (highestTile != 0 && !Board.isTileValue(highestTile))

        companion object {
            fun from(stats: Stats): StatsDto =
                StatsDto(
                    schemaVersion = CURRENT_SCHEMA_VERSION,
                    bestScore = stats.bestScore,
                    gamesPlayed = stats.gamesPlayed,
                    wins = stats.wins,
                    highestTile = stats.highestTile,
                    dailyStreak = stats.dailyStreak,
                    lastDailyEpochDay = stats.lastDailyEpochDay,
                    bestDailyScore = stats.bestDailyScore,
                )
        }
    }
}
