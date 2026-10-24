// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.pabi277.tilefold.domain.Language
import io.github.pabi277.tilefold.domain.Settings
import io.github.pabi277.tilefold.domain.SettingsCodec
import io.github.pabi277.tilefold.domain.ThemeChoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Persists [Settings] through [SettingsCodec].
 *
 * Mirrors [StatsRepository]: corrupt payload → defaults + storageError.
 */
class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
) {
    var loadError: Boolean = false
        private set

    val settingsFlow: Flow<Settings> =
        dataStore.data.map { prefs ->
            val payload = prefs[PreferencesKeys.Settings]
            if (payload.isNullOrBlank()) {
                loadError = false
                Settings()
            } else {
                val decoded = SettingsCodec.decode(payload)
                if (decoded == null) {
                    loadError = true
                    Settings()
                } else {
                    loadError = false
                    decoded
                }
            }
        }

    suspend fun load(): Settings {
        val prefs = dataStore.data.first()
        val payload = prefs[PreferencesKeys.Settings]
        if (payload.isNullOrBlank()) {
            loadError = false
            return Settings()
        }
        val decoded = SettingsCodec.decode(payload)
        if (decoded == null) {
            loadError = true
            return Settings()
        }
        loadError = false
        return decoded
    }

    suspend fun save(settings: Settings) {
        dataStore.edit { it[PreferencesKeys.Settings] = SettingsCodec.encode(settings) }
    }

    suspend fun update(transform: (Settings) -> Settings) {
        val current = load()
        save(transform(current))
    }

    suspend fun setSoundEnabled(enabled: Boolean) = update { it.copy(soundEnabled = enabled) }

    suspend fun setHapticsEnabled(enabled: Boolean) = update { it.copy(hapticsEnabled = enabled) }

    suspend fun setTheme(theme: ThemeChoice) = update { it.copy(theme = theme) }

    suspend fun setLanguage(language: Language) = update { it.copy(language = language) }

    suspend fun setDpadVisible(visible: Boolean) = update { it.copy(dpadVisible = visible) }

    suspend fun clear() {
        dataStore.edit { it.remove(PreferencesKeys.Settings) }
    }
}
