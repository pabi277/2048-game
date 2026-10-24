// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SettingsCodecTest {
    @Test
    fun roundTrip() {
        val original = Settings(
            soundEnabled = false,
            hapticsEnabled = true,
            theme = ThemeChoice.DARK,
            language = Language.HI,
            dpadVisible = true,
        )
        val encoded = SettingsCodec.encode(original)
        val decoded = SettingsCodec.decode(encoded)
        assertThat(decoded).isEqualTo(original)
    }

    @Test
    fun defaultsOnEmpty() {
        val decoded = SettingsCodec.decode(null)
        assertThat(decoded).isNull()
    }

    @Test
    fun corruptReturnsNull() {
        assertThat(SettingsCodec.decode("not-json")).isNull()
        assertThat(SettingsCodec.decode("""{"schemaVersion": 99, "soundEnabled": true}""")).isNull()
    }

    @Test
    fun missingFieldsDefaulted() {
        val decoded = SettingsCodec.decode("""{"schemaVersion":1}""")
        assertThat(decoded).isNotNull()
        assertThat(decoded!!.soundEnabled).isTrue()
    }

    @Test
    fun soundPolicyGate() {
        assertThat(SoundPolicy.shouldPlaySound(true, true)).isTrue()
        assertThat(SoundPolicy.shouldPlaySound(false, true)).isFalse()
        assertThat(SoundPolicy.shouldPlaySound(true, false)).isFalse()
        assertThat(SoundPolicy.shouldVibrate(true, true)).isTrue()
        assertThat(SoundPolicy.shouldVibrate(true, false)).isFalse()
    }
}
