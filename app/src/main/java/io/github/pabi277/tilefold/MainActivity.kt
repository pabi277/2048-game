package io.github.pabi277.tilefold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate
import io.github.pabi277.tilefold.data.AppContainer
import io.github.pabi277.tilefold.domain.Language
import io.github.pabi277.tilefold.domain.Stats
import io.github.pabi277.tilefold.ui.nav.TilefoldNavHost
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme
import kotlinx.coroutines.launch

/**
 * Single activity for the whole app (single-activity + Compose per
 * `knowledge/app-engineering.md` §1).
 *
 * Phase 4 owns Home navigation, How to play, Settings with theme/language
 * switches, and sound/haptics. The game route still preserves the exact
 * board/score/RNG persistence proved in phase 3.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = AppContainer(applicationContext)
        setContent {
            val settings by container.settingsRepository.settingsFlow.collectAsState(
                initial = io.github.pabi277.tilefold.domain.Settings(),
            )
            val stats by container.statsRepository.statsFlow.collectAsState(initial = Stats())
            val savedGame by container.gameSaveRepository.savedGame.collectAsState(initial = null)
            val scope = rememberCoroutineScope()

            // Theme: system/light/dark/gold
            TilefoldTheme(themeChoice = settings.theme) {
                val hasSaved = savedGame != null
                val savedScore = savedGame?.score ?: 0
                val savedHighest = savedGame?.board?.highestTile ?: 0
                val isDaily = savedGame?.let { false } ?: false // GameMode not stored in SaveCodec yet; treat as classic
                val storageError = container.gameSaveRepository.loadError

                val versionName =
                    try {
                        packageManager.getPackageInfo(packageName, 0).versionName ?: "0.4.0"
                    } catch (_: Exception) {
                        "0.4.0"
                    }

                TilefoldNavHost(
                    container = container,
                    settings = settings,
                    stats = stats,
                    savedHasGame = hasSaved,
                    savedScore = savedScore,
                    savedHighestTile = savedHighest,
                    isDailyMode = isDaily,
                    storageError = storageError,
                    versionName = versionName,
                    onThemeChanged = { choice ->
                        scope.launch { container.settingsRepository.setTheme(choice) }
                    },
                    onLanguageChanged = { lang ->
                        scope.launch {
                            container.settingsRepository.setLanguage(lang)
                            applyLanguage(lang)
                        }
                    },
                    onSoundChanged = { enabled ->
                        scope.launch { container.settingsRepository.setSoundEnabled(enabled) }
                    },
                    onHapticsChanged = { enabled ->
                        scope.launch { container.settingsRepository.setHapticsEnabled(enabled) }
                    },
                    onDpadChanged = { visible ->
                        scope.launch { container.settingsRepository.setDpadVisible(visible) }
                    },
                )
            }
        }
    }

    private fun applyLanguage(language: Language) {
        val tag = when (language) {
            Language.EN -> "en"
            Language.HI -> "hi"
        }
        val localeList = LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
