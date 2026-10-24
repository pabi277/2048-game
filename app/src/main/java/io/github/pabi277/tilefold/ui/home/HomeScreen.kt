// SPDX-License-Identifier: MIT
@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.ui.components.AppCard
import io.github.pabi277.tilefold.ui.components.AppChip
import io.github.pabi277.tilefold.ui.components.ChipTone
import io.github.pabi277.tilefold.ui.components.InlineBanner
import io.github.pabi277.tilefold.ui.components.PrimaryButton
import io.github.pabi277.tilefold.ui.components.SecondaryButton
import io.github.pabi277.tilefold.ui.components.TextAction
import io.github.pabi277.tilefold.ui.icons.TilefoldIcons
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens
import io.github.pabi277.tilefold.ui.theme.TilefoldThemeDefaults

internal const val HOME_CONTINUE_TAG = "home-continue"
internal const val HOME_NEW_GAME_TAG = "home-new-game"
internal const val HOME_DAILY_TAG = "home-daily"
internal const val HOME_HOW_TO_PLAY_TAG = "home-how-to-play"
internal const val HOME_STATS_TAG = "home-stats"
internal const val HOME_SETTINGS_TAG = "home-settings"
internal const val HOME_BEST_TAG = "home-best"

@Suppress("LongMethod")
@Composable
internal fun HomeScreen(
    state: HomeState,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onDaily: () -> Unit,
    onHowToPlay: () -> Unit,
    onStats: () -> Unit,
    onSettings: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        LoadingContent(modifier)
        return
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.ScreenPaddingVertical),
    ) {
        if (state.storageError) {
            InlineBanner(
                message = stringResource(R.string.banner_save_read_error),
                detail = stringResource(R.string.banner_save_read_detail),
                onRetry = onRetry,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        // Hero
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = if (state.storageError) 8.dp else 24.dp),
        )
        Text(
            text = stringResource(R.string.home_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )

        // Board preview - only when no in-progress card
        if (state.showBoardPreview()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                BoardPreview()
            }
        }

        Spacer(Modifier.height(16.dp))

        // Cards
        when {
            state.isInProgress() -> {
                ResumeCard(
                    score = state.savedScore,
                    highestTile = state.savedHighestTile,
                    isDaily = state.isDailyMode,
                )
            }

            state.dailyCompleted -> {
                DailyCard(
                    score = state.todaysScore,
                    streak = state.dailyStreak,
                )
            }
        }

        // Primary / secondary actions
        Column(
            modifier = Modifier.padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.isInProgress()) {
                PrimaryButton(
                    label = stringResource(R.string.home_continue),
                    onClick = onContinue,
                    icon = TilefoldIcons.Play,
                    modifier = Modifier.testTag(HOME_CONTINUE_TAG),
                )
                SecondaryButton(
                    label = if (state.dailyCompleted) {
                        stringResource(R.string.daily_done)
                    } else {
                        stringResource(R.string.home_daily_challenge)
                    },
                    onClick = onDaily,
                    enabled = !state.dailyCompleted,
                    modifier = Modifier.testTag(HOME_DAILY_TAG),
                )
                // Demoted new game as text action with confirm dialog - just text here
                TextAction(
                    label = stringResource(R.string.home_new_game),
                    onClick = onNewGame,
                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag(HOME_NEW_GAME_TAG),
                )
            } else {
                PrimaryButton(
                    label = stringResource(R.string.home_new_game),
                    onClick = onNewGame,
                    icon = TilefoldIcons.Play,
                    modifier = Modifier.testTag(HOME_NEW_GAME_TAG),
                )
                SecondaryButton(
                    label = if (state.dailyCompleted) {
                        stringResource(R.string.daily_done)
                    } else {
                        stringResource(R.string.home_daily_challenge)
                    },
                    onClick = onDaily,
                    enabled = !state.dailyCompleted,
                    modifier = Modifier.testTag(HOME_DAILY_TAG),
                )
            }
        }

        // Text link row: How to play · Stats · Settings
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextAction(
                label = stringResource(R.string.home_how_to_play),
                onClick = onHowToPlay,
                modifier = Modifier.testTag(HOME_HOW_TO_PLAY_TAG),
            )
            Text(
                text = "·",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            TextAction(
                label = stringResource(R.string.home_stats),
                onClick = onStats,
                modifier = Modifier.testTag(HOME_STATS_TAG),
            )
            Text(
                text = "·",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            TextAction(
                label = stringResource(R.string.home_settings),
                onClick = onSettings,
                modifier = Modifier.testTag(HOME_SETTINGS_TAG),
            )
        }

        Spacer(Modifier.weight(1f, fill = false))
        Spacer(Modifier.height(16.dp))

        // Best line
        val bestText = if (state.isEmpty) {
            stringResource(R.string.home_no_best)
        } else {
            stringResource(R.string.home_best_line, state.bestScore.toString())
        }
        Text(
            text = bestText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally).testTag(HOME_BEST_TAG),
        )
        if (state.isEmpty) {
            Text(
                text = stringResource(R.string.home_start_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp, bottom = 8.dp),
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ResumeCard(
    score: Int,
    highestTile: Int,
    isDaily: Boolean,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val labelStyle = MaterialTheme.typography.labelSmall
            Text(
                text = stringResource(R.string.home_game_in_progress),
                style = labelStyle.copy(letterSpacing = labelStyle.letterSpacing),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (isDaily) {
                AppChip(label = stringResource(R.string.daily_title), tone = ChipTone.Outline)
            }
        }
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Text(
            text = stringResource(
                R.string.home_resume_meta,
                score.toString(),
                highestTile.toString(),
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        Text(
            text = stringResource(R.string.home_continue_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun DailyCard(
    score: Int,
    streak: Int,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_daily_label, "24 Oct"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (streak > 0) {
                AppChip(label = stringResource(R.string.streak_days, streak), tone = ChipTone.Accent)
            }
        }
        Text(
            text = stringResource(R.string.home_todays_score),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
    }
}

@Composable
internal fun BoardPreview(
    modifier: Modifier = Modifier,
) {
    val palette = TilefoldThemeDefaults.palette
    val grid = rememberBoardPreviewGrid()
    // Reference uses 196px; we use 196dp equivalent
    val px = 196.dp
    val gap = 6.dp
    val padding = 8.dp
    BoxWithConstraints(
        modifier =
            modifier
                .clip(RoundedCornerShape(Corners.Board))
                .background(palette.boardFrame)
                .padding(padding),
    ) {
        val maxWidthPx = px
        // Use fixed size box
        Column(
            modifier = Modifier.width(maxWidthPx),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            grid.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                ) {
                    row.forEach { value ->
                        val pair = if (value != 0) palette.tile(value) else null
                        Box(
                            modifier =
                                Modifier.weight(1f)
                                    .clip(RoundedCornerShape(Corners.Tile))
                                    .background(pair?.background ?: palette.emptyCell)
                                    .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (value != 0) {
                                Text(
                                    text = value.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = pair!!.numeral,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun rememberBoardPreviewGrid(): List<List<Int>> =
    listOf(
        listOf(2, 4, 8, 16),
        listOf(32, 64, 128, 256),
        listOf(0, 2, 4, 0),
        listOf(0, 0, 0, 8),
    )

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    val palette = TilefoldThemeDefaults.palette
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.ScreenPaddingVertical),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(2, 4, 8, 16).forEach { v ->
                val pair = palette.tile(v)
                Box(
                    modifier =
                        Modifier.size(44.dp)
                            .clip(RoundedCornerShape(Corners.Tile))
                            .background(pair.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = v.toString(), color = pair.numeral, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(R.string.home_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(Corners.Board))
                    .background(palette.boardFrame)
                    .padding(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(4) { idx ->
                            Box(
                                modifier =
                                    Modifier.weight(1f)
                                        .clip(RoundedCornerShape(Corners.Tile))
                                        .background(palette.skeleton.copy(alpha = if (idx % 3 == 0) 0.6f else 1f))
                                        .height(56.dp),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
