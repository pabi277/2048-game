@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.ui.components.PrimaryButton
import io.github.pabi277.tilefold.ui.components.SecondaryButton
import io.github.pabi277.tilefold.ui.icons.TilefoldIcons
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.TypeScale

internal const val PAUSE_SHEET_TAG = "pause-sheet"

/**
 * The pause sheet from `docs/UI-REFERENCE.md` row 9: the board dims under a
 * scrim and a bottom sheet offers Resume and Restart, with the current score and
 * highest tile as quiet context.
 *
 * The reference also lists a "Home" row; there is no Home screen until phase 4,
 * so that row is deliberately absent rather than dead (recorded in
 * `docs/phases/02-board-ui.md`).
 */
@Composable
internal fun PauseSheet(
    score: Int,
    highestTile: Int,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape =
        RoundedCornerShape(
            topStart = Corners.SheetTop,
            topEnd = Corners.SheetTop,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim)
                .testTag(PAUSE_SHEET_TAG),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = shape,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp)) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 40.dp, height = 4.dp)
                            .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(2.dp)),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pause_title),
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontSize = TypeScale.DialogTitle,
                            lineHeight = TypeScale.DialogTitleLine,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(16.dp))
                PrimaryButton(
                    label = stringResource(R.string.pause_resume),
                    onClick = onResume,
                    icon = TilefoldIcons.Play,
                )
                Spacer(Modifier.height(8.dp))
                SecondaryButton(
                    label = stringResource(R.string.game_restart),
                    onClick = onRestart,
                    icon = TilefoldIcons.RotateCcw,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text =
                        stringResource(
                            R.string.pause_meta,
                            ScoreFormat.of(score),
                            highestTile.toString(),
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
