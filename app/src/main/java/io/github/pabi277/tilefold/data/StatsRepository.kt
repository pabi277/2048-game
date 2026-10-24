package io.github.pabi277.tilefold.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.pabi277.tilefold.domain.Stats
import io.github.pabi277.tilefold.domain.StatsCodec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Persists [Stats] through [StatsCodec].
 *
 * Mirrors [GameSaveRepository]: corrupt payload → null + storage error.
 */
class StatsRepository(
    private val dataStore: DataStore<Preferences>,
) {
    var loadError: Boolean = false
        private set

    val statsFlow: Flow<Stats> =
        dataStore.data.map { prefs ->
            val payload = prefs[PreferencesKeys.Stats]
            if (payload.isNullOrBlank()) {
                loadError = false
                Stats()
            } else {
                val decoded = StatsCodec.decode(payload)
                if (decoded == null) {
                    loadError = true
                    Stats()
                } else {
                    loadError = false
                    decoded
                }
            }
        }

    suspend fun load(): Stats {
        val prefs = dataStore.data.first()
        val payload = prefs[PreferencesKeys.Stats]
        if (payload.isNullOrBlank()) {
            loadError = false
            return Stats()
        }
        val decoded = StatsCodec.decode(payload)
        if (decoded == null) {
            loadError = true
            return Stats()
        }
        loadError = false
        return decoded
    }

    suspend fun save(stats: Stats) {
        dataStore.edit { it[PreferencesKeys.Stats] = StatsCodec.encode(stats) }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(PreferencesKeys.Stats) }
    }
}
