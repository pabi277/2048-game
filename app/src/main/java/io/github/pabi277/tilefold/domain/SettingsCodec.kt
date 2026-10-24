// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * JSON codec for [Settings] — mirrors [StatsCodec] and [SaveCodec].
 *
 * Same contract:
 * - schemaVersion 1, unknown keys ignored, missing fields defaulted;
 * - an impossible payload (null decode) makes the storage-error path fall
 *   back to defaults (§ RESEARCH.md — defensive decode).
 */
object SettingsCodec {
    const val CURRENT_SCHEMA_VERSION = 1

    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = false
        }

    fun encode(settings: Settings): String = json.encodeToString(SettingsDto.from(settings))

    fun decode(payload: String?): Settings? {
        if (payload.isNullOrBlank()) return null
        val dto = runCatching { json.decodeFromString<SettingsDto>(payload) }.getOrNull() ?: return null
        return dto.toSettings()
    }

    @Serializable
    private data class SettingsDto(
        val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
        val soundEnabled: Boolean = true,
        val hapticsEnabled: Boolean = true,
        val theme: String = ThemeChoice.SYSTEM.name,
        val language: String = Language.EN.name,
        val dpadVisible: Boolean = false,
    ) {
        fun toSettings(): Settings? {
            if (schemaVersion < 1 || schemaVersion > CURRENT_SCHEMA_VERSION) return null
            val themeChoice = runCatching { ThemeChoice.valueOf(theme) }.getOrElse { ThemeChoice.SYSTEM }
            val languageChoice = runCatching { Language.valueOf(language) }.getOrElse { Language.EN }
            return Settings(
                soundEnabled = soundEnabled,
                hapticsEnabled = hapticsEnabled,
                theme = themeChoice,
                language = languageChoice,
                dpadVisible = dpadVisible,
            )
        }

        companion object {
            fun from(settings: Settings): SettingsDto =
                SettingsDto(
                    schemaVersion = CURRENT_SCHEMA_VERSION,
                    soundEnabled = settings.soundEnabled,
                    hapticsEnabled = settings.hapticsEnabled,
                    theme = settings.theme.name,
                    language = settings.language.name,
                    dpadVisible = settings.dpadVisible,
                )
        }
    }
}
