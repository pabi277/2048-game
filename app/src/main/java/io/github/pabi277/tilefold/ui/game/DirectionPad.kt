@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.ui.icons.TilefoldIcons
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens

internal const val DIRECTION_PAD_TAG = "direction-pad"

/**
 * The optional direction pad: four 56dp keys, up above left/down/right, 4dp
 * apart, 2dp elevation — exactly the frozen reference's `DirectionPad`.
 *
 * It exists for the players who do not swipe (and for tablets with a keyboard),
 * never instead of swiping: the board is always the primary input.
 */
@Composable
internal fun DirectionPad(
    onMove: (Direction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.testTag(DIRECTION_PAD_TAG),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KEY_GAP),
    ) {
        PadKey(
            icon = TilefoldIcons.ArrowUp,
            label = R.string.direction_up,
            direction = Direction.UP,
            onMove = onMove,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(KEY_GAP)) {
            PadKey(
                icon = TilefoldIcons.ArrowLeft,
                label = R.string.direction_left,
                direction = Direction.LEFT,
                onMove = onMove,
            )
            PadKey(
                icon = TilefoldIcons.ArrowDown,
                label = R.string.direction_down,
                direction = Direction.DOWN,
                onMove = onMove,
            )
            PadKey(
                icon = TilefoldIcons.ArrowRight,
                label = R.string.direction_right,
                direction = Direction.RIGHT,
                onMove = onMove,
            )
        }
    }
}

@Composable
private fun PadKey(
    icon: ImageVector,
    label: Int,
    direction: Direction,
    onMove: (Direction) -> Unit,
) {
    val shape = RoundedCornerShape(Corners.Button)
    Box(
        modifier =
            Modifier
                .size(Dimens.DirectionPadKey)
                .shadow(Dimens.DirectionPadElevation, shape)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                .clickable { onMove(direction) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(label),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
    }
}

private val KEY_GAP = 4.dp
