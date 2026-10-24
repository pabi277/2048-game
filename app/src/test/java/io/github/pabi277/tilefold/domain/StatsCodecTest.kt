@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StatsCodecTest {
    @Test
    fun `round trip preserves all fields`() {
        val stats =
            Stats(
                bestScore = 12480,
                gamesPlayed = 128,
                wins = 14,
                highestTile = 1024,
                dailyStreak = 4,
                lastDailyEpochDay = 20376L,
                bestDailyScore = 3240,
            )

        val restored = StatsCodec.decode(StatsCodec.encode(stats))!!

        assertThat(restored).isEqualTo(stats)
    }

    @Test
    fun `empty stats round trips`() {
        val restored = StatsCodec.decode(StatsCodec.encode(Stats()))!!

        assertThat(restored).isEqualTo(Stats())
    }

    @Test
    fun `null and blank decode to null`() {
        assertThat(StatsCodec.decode(null)).isNull()
        assertThat(StatsCodec.decode("")).isNull()
        assertThat(StatsCodec.decode("   ")).isNull()
    }

    @Test
    fun `corrupt json refused`() {
        assertThat(StatsCodec.decode("not json")).isNull()
        assertThat(StatsCodec.decode("[]")).isNull()
        assertThat(StatsCodec.decode("{\"schemaVersion\":2,\"bestScore\":10}")).isNull()
    }

    @Test
    fun `impossible values refused`() {
        assertThat(StatsCodec.decode("{\"schemaVersion\":1,\"bestScore\":-1}")).isNull()
        assertThat(StatsCodec.decode("{\"schemaVersion\":1,\"highestTile\":3}")).isNull()
    }

    @Test
    fun `older payload with missing fields still decodes`() {
        val minimal = "{\"schemaVersion\":1,\"bestScore\":100}"

        val restored = StatsCodec.decode(minimal)!!

        assertThat(restored.bestScore).isEqualTo(100)
        assertThat(restored.gamesPlayed).isEqualTo(0)
    }

    @Test
    fun `unknown keys ignored`() {
        val withExtra = "{\"schemaVersion\":1,\"bestScore\":10,\"newField\":true}"

        assertThat(StatsCodec.decode(withExtra)).isNotNull()
    }
}
