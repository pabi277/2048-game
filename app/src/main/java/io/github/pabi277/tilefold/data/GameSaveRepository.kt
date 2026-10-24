package io.github.pabi277.tilefold.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.pabi277.tilefold.domain.Game
import io.github.pabi277.tilefold.domain.SaveCodec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Persists the current [Game] through [SaveCodec].
 *
 * - save on every valid move, debounced ≤250 ms ([SAVE_DEBOUNCE_MILLIS]);
 * - save on stop immediately (no debounce);
 * - resume on launch through the codec with the exact board/score/keep-playing
 *   flag/RNG position;
 * - corrupt-save fallback + storage banner (decode → null while a non-empty
 *   payload existed means \"storage error\").
 */
class GameSaveRepository(
    private val dataStore: DataStore<Preferences>,
) {
    private var debounceJob: Job? = null

    /**
     * Whether the last load failed to decode a non-empty payload.
     *
     * The caller shows the storage-error banner when this is true and still
     * starts a new game (a board is never lost, but a corrupt one is never
     * restored).
     */
    var loadError: Boolean = false
        private set

    /** Flow of the restored game, or null when none / corrupt. */
    val savedGame: Flow<Game?> =
        dataStore.data.map { prefs ->
            val payload = prefs[PreferencesKeys.SaveGame]
            if (payload.isNullOrBlank()) {
                loadError = false
                null
            } else {
                val decoded = SaveCodec.decode(payload)
                loadError = decoded == null
                decoded
            }
        }

    suspend fun load(): Game? {
        val prefs = dataStore.data.first()
        val payload = prefs[PreferencesKeys.SaveGame]
        if (payload.isNullOrBlank()) {
            loadError = false
            return null
        }
        val decoded = SaveCodec.decode(payload)
        loadError = decoded == null
        return decoded
    }

    /** Immediate, non-debounced write — for `ON_STOP`. */
    suspend fun saveNow(game: Game) {
        debounceJob?.cancel()
        dataStore.edit { it[PreferencesKeys.SaveGame] = SaveCodec.encode(game) }
    }

    /** Debounced write — for every valid move, at most [SAVE_DEBOUNCE_MILLIS]. */
    fun saveDebounced(
        scope: CoroutineScope,
        game: Game,
    ) {
        debounceJob?.cancel()
        debounceJob =
            scope.launch {
                delay(SAVE_DEBOUNCE_MILLIS)
                dataStore.edit { it[PreferencesKeys.SaveGame] = SaveCodec.encode(game) }
            }
    }

    suspend fun clear() {
        debounceJob?.cancel()
        dataStore.edit { it.remove(PreferencesKeys.SaveGame) }
    }

    companion object {
        const val SAVE_DEBOUNCE_MILLIS = 250L
    }
}
