package io.github.pabi277.tilefold.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.domain.Stats
import io.github.pabi277.tilefold.ui.components.InlineBanner
import io.github.pabi277.tilefold.ui.components.PrimaryButton
import io.github.pabi277.tilefold.ui.icons.TilefoldIcons
import io.github.pabi277.tilefold.ui.theme.Corners

/**
 * Stats screen — the six facts, two columns (one at 2.0×).
 *
 * Reference: `StatsScreen` in `design/reference/src/screens.jsx` — no charts,
 * empty state with CTA, storage-error banner shows \"—\" not zero.
 */
@Composable
internal fun StatsScreen(
    stats: Stats,
    isEmpty: Boolean = stats.isEmpty,
    storageError: Boolean = false,
    onNewGame: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val fontScale = LocalDensity.current.fontScale
    val isLargeFont = fontScale >= 2f

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.stats_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (storageError) {
            InlineBanner(
                message = stringResource(R.string.banner_stats_error),
                detail = stringResource(R.string.banner_stats_unknown),
                onRetry = onRetry,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        if (isEmpty) {
            Text(
                text = stringResource(R.string.stats_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.stats_empty_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            )
            if (onNewGame != null) {
                PrimaryButton(label = stringResource(R.string.game_new), onClick = onNewGame, icon = TilefoldIcons.Play)
            }
        }
        StatsGrid(stats = stats, storageError = storageError, singleColumn = isLargeFont)
    }
}

@Composable
private fun StatsGrid(
    stats: Stats,
    storageError: Boolean,
    singleColumn: Boolean,
) {
    val unknown = stringResource(R.string.stats_unknown)
    fun fmt(value: Int): String = if (storageError) unknown else value.toString()
    fun tileFmt(value: Int): String = if (storageError) unknown else if (value == 0) unknown else value.toString()

    val gamesLabel = stringResource(R.string.stats_games_played)
    val gamesValue = if (stats.gamesPlayed == 0 && !storageError) unknown else fmt(stats.gamesPlayed)
    val items =
        listOf(
            StatItem(gamesLabel, gamesValue),
            StatItem(stringResource(R.string.stats_wins), fmt(stats.wins)),
            StatItem(stringResource(R.string.stats_best_score), fmt(stats.bestScore)),
            StatItem(stringResource(R.string.stats_highest_tile), tileFmt(stats.highestTile)),
            StatItem(stringResource(R.string.stats_daily_streak), fmt(stats.dailyStreak)),
            StatItem(stringResource(R.string.stats_best_daily), fmt(stats.bestDailyScore)),
        )

    if (singleColumn) {
        Column(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items.forEach { item ->
                StatTile(label = item.label, value = item.value)
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            for (i in items.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatTile(label = items[i].label, value = items[i].value, modifier = Modifier.weight(1f))
                    if (i + 1 < items.size) {
                        StatTile(label = items[i + 1].label, value = items[i + 1].value, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private data class StatItem(
    val label: String,
    val value: String,
)

@Composable
private fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Corners.Card))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(Corners.Card))
                .padding(16.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
