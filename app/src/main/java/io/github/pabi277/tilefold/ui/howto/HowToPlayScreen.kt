// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.howto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.ui.components.AppBar
import io.github.pabi277.tilefold.ui.components.AppCard
import io.github.pabi277.tilefold.ui.components.RowGroup
import io.github.pabi277.tilefold.ui.components.SectionLabel
import io.github.pabi277.tilefold.ui.game.DirectionPad
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens
import io.github.pabi277.tilefold.ui.theme.TilefoldThemeDefaults

@Composable
internal fun HowToPlayScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.ScreenPaddingVertical),
    ) {
        AppBar(title = stringResource(R.string.how_to_play_title), onBack = onBack)

        val rules =
            listOf(
                stringResource(R.string.how_to_play_rule_1),
                stringResource(R.string.how_to_play_rule_2),
                stringResource(R.string.how_to_play_rule_3),
                stringResource(R.string.how_to_play_rule_4),
                stringResource(R.string.how_to_play_rule_5),
                stringResource(R.string.how_to_play_rule_6),
                stringResource(R.string.how_to_play_rule_7),
            )

        Column(modifier = Modifier.padding(top = 8.dp)) {
            rules.forEachIndexed { idx, rule ->
                NumberedRule(number = idx + 1, text = rule)
            }
        }

        SectionLabel(text = stringResource(R.string.how_to_play_scoring_title))
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ExampleRow()
                Text(
                    text = stringResource(R.string.how_to_play_scoring_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        SectionLabel(text = stringResource(R.string.how_to_play_undo_title))
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.how_to_play_undo_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }

        SectionLabel(text = stringResource(R.string.how_to_play_controls_title))
        AppCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.how_to_play_controls_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                DirectionPad(onMove = {})
            }
        }
    }
}

@Composable
private fun NumberedRule(number: Int, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier.size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ExampleRow() {
    val palette = TilefoldThemeDefaults.palette
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(8 to 8, 16 to 0).forEachIndexed { idx, (valueA, _) ->
            if (idx == 1) {
                Text(text = "=", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TileBox(value = 16, palette = palette)
                Text(
                    text = "+16",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp),
                )
                return@forEachIndexed
            }
            TileBox(value = 8, palette = palette)
            Text(text = "+", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TileBox(value = 8, palette = palette)
        }
    }
}

@Composable
private fun TileBox(value: Int, palette: io.github.pabi277.tilefold.ui.theme.TilefoldPalette) {
    val pair = palette.tile(value)
    Box(
        modifier =
            Modifier.size(48.dp)
                .clip(RoundedCornerShape(Corners.Tile))
                .background(pair.background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = pair.numeral,
        )
    }
}
