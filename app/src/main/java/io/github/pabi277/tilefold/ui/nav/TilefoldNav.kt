// SPDX-License-Identifier: MIT
@file:Suppress("MatchingDeclarationName")

package io.github.pabi277.tilefold.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pabi277.tilefold.data.AppContainer
import io.github.pabi277.tilefold.domain.DailyChallenge
import io.github.pabi277.tilefold.domain.Language
import io.github.pabi277.tilefold.domain.Settings
import io.github.pabi277.tilefold.domain.Stats
import io.github.pabi277.tilefold.domain.ThemeChoice
import io.github.pabi277.tilefold.ui.game.GameRoute
import io.github.pabi277.tilefold.ui.home.HomeScreen
import io.github.pabi277.tilefold.ui.home.HomeState
import io.github.pabi277.tilefold.ui.howto.HowToPlayScreen
import io.github.pabi277.tilefold.ui.settings.SettingsScreen
import io.github.pabi277.tilefold.ui.settings.SettingsState
import io.github.pabi277.tilefold.ui.stats.StatsScreen
import kotlinx.coroutines.launch

@Suppress("MatchingDeclarationName")
object TilefoldRoutes {
    const val Home = "home"
    const val GameClassic = "game_classic"
    const val GameDaily = "game_daily"
    const val HowToPlay = "how_to_play"
    const val Stats = "stats"
    const val Settings = "settings"
}

@Suppress("LongParameterList")
@Composable
internal fun TilefoldNavHost(
    container: AppContainer,
    settings: Settings,
    stats: Stats,
    savedHasGame: Boolean,
    savedScore: Int,
    savedHighestTile: Int,
    isDailyMode: Boolean,
    storageError: Boolean,
    versionName: String,
    onThemeChanged: (ThemeChoice) -> Unit,
    onLanguageChanged: (Language) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onDpadChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = TilefoldRoutes.Home,
        modifier = modifier,
    ) {
        composable(TilefoldRoutes.Home) {
            val today = DailyChallenge.epochDay(System.currentTimeMillis())
            val dailyCompleted = DailyChallenge.isCompletedToday(stats, today)
            val homeState =
                HomeState(
                    hasSavedGame = savedHasGame,
                    savedScore = savedScore,
                    savedHighestTile = savedHighestTile,
                    isDailyMode = isDailyMode,
                    dailyCompleted = dailyCompleted,
                    dailyStreak = stats.dailyStreak,
                    todaysScore = stats.bestDailyScore,
                    bestScore = stats.bestScore,
                    isEmpty = stats.isEmpty,
                    storageError = storageError,
                    dailyLabelDate = "24 Oct",
                )
            HomeScreen(
                state = homeState,
                onNewGame = { navController.navigate(TilefoldRoutes.GameClassic) },
                onContinue = { navController.navigate(TilefoldRoutes.GameClassic) },
                onDaily = { navController.navigate(TilefoldRoutes.GameDaily) },
                onHowToPlay = { navController.navigate(TilefoldRoutes.HowToPlay) },
                onStats = { navController.navigate(TilefoldRoutes.Stats) },
                onSettings = { navController.navigate(TilefoldRoutes.Settings) },
                onRetry = {},
            )
        }

        composable(TilefoldRoutes.GameClassic) {
            GameRoute(
                container = container,
                onHome = { navController.popBackStack(TilefoldRoutes.Home, inclusive = false) },
            )
        }

        composable(TilefoldRoutes.GameDaily) {
            val today = DailyChallenge.epochDay(System.currentTimeMillis())
            GameRoute(
                container = container,
                seed = DailyChallenge.seedFor(today),
                onHome = { navController.popBackStack(TilefoldRoutes.Home, inclusive = false) },
            )
        }

        composable(TilefoldRoutes.HowToPlay) {
            HowToPlayScreen(onBack = { navController.popBackStack() })
        }

        composable(TilefoldRoutes.Stats) {
            StatsScreen(
                stats = stats,
                storageError = storageError,
                onNewGame = { navController.navigate(TilefoldRoutes.GameClassic) },
            )
        }

        composable(TilefoldRoutes.Settings) {
            val goldUnlocked = stats.highestTile >= 2048 || stats.wins > 0 || stats.bestScore >= 2048
            val settingsState =
                SettingsState(
                    soundEnabled = settings.soundEnabled,
                    hapticsEnabled = settings.hapticsEnabled,
                    theme = settings.theme,
                    language = settings.language,
                    dpadVisible = settings.dpadVisible,
                    goldUnlocked = goldUnlocked,
                    versionName = versionName,
                )
            SettingsScreen(
                state = settingsState,
                onSoundChanged = onSoundChanged,
                onHapticsChanged = onHapticsChanged,
                onThemeChanged = onThemeChanged,
                onLanguageChanged = onLanguageChanged,
                onDpadChanged = onDpadChanged,
                onResetStats = {
                    scope.launch { container.statsRepository.clear() }
                },
                onLicences = {},
                onPrivacy = {},
                onBack = { navController.popBackStack() },
            )
        }
    }
}
