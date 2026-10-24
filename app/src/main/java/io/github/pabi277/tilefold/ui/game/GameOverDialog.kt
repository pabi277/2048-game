package io.github.pabi277.tilefold.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.ui.components.PrimaryButton
import io.github.pabi277.tilefold.ui.components.SecondaryButton
import io.github.pabi277.tilefold.ui.components.TextAction
import androidx.compose.ui.text.font.FontWeight
import io.github.pabi277.tilefold.ui.icons.TilefoldIcons
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.TextCaps
import io.github.pabi277.tilefold.ui.theme.TilefoldThemeDefaults
import io.github.pabi277.tilefold.ui.theme.TypeScale

internal const val GAME_OVER_DIALOG_TAG = "game-over-dialog"
internal const val NEW_BEST_TAG = "new-best-chip"

/**
 * Game-over dialog — final score, best, New best chip, undo + new game + home.
 *
 * Reference: `GameOverScreen` — the chip is success tone, the undo caption
 * reads \"1 free undo\" and the primary action carries the undo icon.
 */
@Composable
internal fun GameOverDialog(
    score: Int,
    best: Int,
    isNewBest: Boolean,
    canUndo: Boolean,
    canEarnUndo: Boolean,
    onUndo: () -> Unit,
    onRewardedUndo: () -> Unit,
    onNewGame: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onNewGame) {
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(Corners.Dialog),
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.testTag(GAME_OVER_DIALOG_TAG),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.game_over_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (isNewBest) {
                        NewBestChip()
                    }
                }
                Text(
                    text = stringResource(R.string.game_over_final_score),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DialogScoreBox(
                        label = stringResource(R.string.game_score),
                        value = score,
                        modifier = Modifier.weight(1f),
                    )
                    DialogScoreBox(
                        label = stringResource(R.string.game_best),
                        value = best,
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    text = stringResource(R.string.game_free_undo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PrimaryButton(
                        label = stringResource(R.string.game_undo_last),
                        onClick = onUndo,
                        icon = TilefoldIcons.Undo2,
                        enabled = canUndo,
                    )
                    if (canEarnUndo && !canUndo) {
                        SecondaryButton(
                            label = stringResource(R.string.game_rewarded_undo),
                            onClick = onRewardedUndo,
                        )
                    }
                    SecondaryButton(label = stringResource(R.string.game_new), onClick = onNewGame)
                    TextAction(
                        label = stringResource(R.string.game_home),
                        onClick = onHome,
                        icon = TilefoldIcons.House,
                    )
                }
            }
        }
    }
}

@Composable
internal fun NewBestChip(modifier: Modifier = Modifier) {
    val palette = TilefoldThemeDefaults.palette
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.success)
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .testTag(NEW_BEST_TAG),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.game_new_best),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = palette.onSuccess,
        )
    }
}

@Composable
private fun DialogScoreBox(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
) {
    val palette = TilefoldThemeDefaults.palette
    val fontScale = LocalDensity.current.fontScale
    val valueStyle =
        MaterialTheme.typography.headlineMedium.copy(
            fontSize = TextCaps.capped(TypeScale.ScoreValue, TypeScale.DIALOG_SCORE_MAX_SCALE, fontScale),
            lineHeight = TextCaps.capped(TypeScale.ScoreValueLine, TypeScale.DIALOG_SCORE_MAX_SCALE, fontScale),
            fontFeatureSettings = "tnum",
        )
    Column(
        modifier =
            modifier
                .background(palette.scoreBox, RoundedCornerShape(Corners.ScoreBox))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = palette.scoreLabel,
            maxLines = 1,
        )
        Text(
            text = ScoreFormat.of(value),
            style = valueStyle,
            color = palette.scoreValue,
            maxLines = 1,
        )
    }
}
