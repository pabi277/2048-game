package io.github.pabi277.tilefold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.pabi277.tilefold.data.AppContainer
import io.github.pabi277.tilefold.domain.DailyChallenge
import io.github.pabi277.tilefold.ui.game.GameRoute
import io.github.pabi277.tilefold.ui.stats.StatsScreen
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme

/**
 * Single activity for the whole app (single-activity + Compose per
 * `knowledge/app-engineering.md` §1).
 *
 * Phase 3 shows the game with win/game-over dialogs, undo allowance, persistence
 * and the stats screen. The APK opens on Classic; tabs expose Daily and Stats
 * until Home navigation arrives in phase 4 (deliberate temporary chrome).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = AppContainer(applicationContext)
        setContent {
            TilefoldTheme {
                var selected by remember { mutableIntStateOf(0) }
                Column(Modifier.fillMaxSize()) {
                    TabRow(selectedTabIndex = selected) {
                        Tab(selected = selected == 0, onClick = { selected = 0 }, text = { Text("Classic") })
                        Tab(
                            selected = selected == 1,
                            onClick = { selected = 1 },
                            text = { Text(stringResource(io.github.pabi277.tilefold.R.string.daily_title)) },
                        )
                        Tab(
                            selected = selected == 2,
                            onClick = { selected = 2 },
                            text = { Text(stringResource(io.github.pabi277.tilefold.R.string.stats_title)) },
                        )
                    }
                    when (selected) {
                        0 -> GameRoute(container = container, modifier = Modifier.weight(1f))
                        1 -> {
                            val today = DailyChallenge.epochDay(System.currentTimeMillis())
                            GameRoute(
                                container = container,
                                seed = DailyChallenge.seedFor(today),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        else -> StatsScreen(
                            stats = io.github.pabi277.tilefold.domain.Stats(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
