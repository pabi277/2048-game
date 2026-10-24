package io.github.pabi277.tilefold.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tilefold")

/**
 * Manual DI graph — `docs/DECISIONS.md` § DI.
 *
 * Three nodes: DataStore → repositories → ViewModels.
 * No Hilt/Koin — the graph is small and explicit.
 */
class AppContainer(
    context: Context,
) {
    val dataStore: DataStore<Preferences> = context.dataStore

    val gameSaveRepository = GameSaveRepository(dataStore)
    val statsRepository = StatsRepository(dataStore)
    val settingsRepository = SettingsRepository(dataStore)

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
}
