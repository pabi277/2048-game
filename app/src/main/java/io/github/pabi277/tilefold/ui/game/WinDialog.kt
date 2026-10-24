package io.github.pabi277.tilefold.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.ui.components.PrimaryButton
import io.github.pabi277.tilefold.ui.components.SecondaryButton
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.TextCaps
import io.github.pabi277.tilefold.ui.theme.TilefoldThemeDefaults
import io.github.pabi277.tilefold.ui.theme.TypeScale

internal const val WIN_DIALOG_TAG = "win-dialog"

/**
 * Win dialog — \"You made 2048\" with Keep playing / New game.
 *
 * Layout is the reference's `WinScreen` dialog: title, body, two score boxes
 * (capped at 1.25×), then the two actions. The dialog is scrollable at 2.0×
 * via the Dialog's own scrolling.
 */
@Composable
internal fun WinDialog(
    score: Int,
    best: Int,
    onKeepPlaying: () -> Unit,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onKeepPlaying) {
        Surface(
            shape = RoundedCornerShape(Corners.Dialog),
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.testTag(WIN_DIALOG_TAG),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.win_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.win_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PrimaryButton(label = stringResource(R.string.win_keep_playing), onClick = onKeepPlaying)
                    SecondaryButton(label = stringResource(R.string.game_new), onClick = onNewGame)
                }
            }
        }
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
