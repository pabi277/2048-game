@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens

/**
 * The two button shapes of the frozen reference (`PrimaryButton` and
 * `SecondaryButton` in `docs/UI-REFERENCE.md` § Component inventory):
 * 48dp minimum height, 12dp corners, 16dp inline padding, 8dp icon gap.
 *
 * Both are stateless and read only theme tokens, so Home, dialogs and the pause
 * sheet in later phases share one implementation.
 */
@Composable
internal fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Corners.Button),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = Dimens.MinTarget)
                .alpha(if (enabled) 1f else 0.45f),
    ) {
        ButtonLabel(label = label, icon = icon, tint = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
internal fun SecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val content = MaterialTheme.colorScheme.onSecondaryContainer
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Corners.Button),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = content,
            ),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = Dimens.MinTarget)
                .alpha(if (enabled) 1f else 0.45f),
    ) {
        ButtonLabel(label = label, icon = icon, tint = content)
    }
}

@Composable
private fun ButtonLabel(
    label: String,
    icon: ImageVector?,
    tint: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = tint)
    }
}

/**
 * A 48dp icon-only action — the home and restart buttons above the board and the
 * direction-pad toggle below it. The icon itself is 22dp, as the reference draws
 * it, while the touch target stays the full 48dp (`rule.md` §5).
 */
@Composable
internal fun IconAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(Dimens.IconButton)
                .clip(RoundedCornerShape(Corners.Button))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
    }
}

/**
 * A quiet text action with an optional leading icon — "Undo" on the game screen.
 * It keeps the 48dp target even though it reads as text.
 */
@Composable
internal fun TextAction(
    label: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tint = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier =
            modifier
                .heightIn(min = Dimens.MinTarget)
                .clip(RoundedCornerShape(Corners.Button))
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 8.dp)
                .alpha(if (enabled) 1f else 0.45f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = tint)
    }
}

/**
 * The outline chip the reference uses for the "Swipe to merge" hint and the
 * game-over note: fully rounded, 1dp divider border, no fill.
 */
@Composable
internal fun OutlineChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier =
            modifier
                .clip(shape)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
