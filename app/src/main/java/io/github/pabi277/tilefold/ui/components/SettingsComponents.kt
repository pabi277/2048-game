// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens

@Composable
internal fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    val labelStyle = MaterialTheme.typography.labelSmall
    Text(
        text = text,
        style = labelStyle.copy(letterSpacing = labelStyle.letterSpacing),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
internal fun RowGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(Corners.Card),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier,
    ) {
        Column { content() }
    }
}

@Composable
internal fun SettingsRow(
    title: String,
    onClick: (() -> Unit)? = null,
    subtitle: String? = null,
    trail: @Composable (() -> Unit)? = null,
    destructive: Boolean = false,
    enabled: Boolean = true,
    first: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val titleColor = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    Column(modifier = modifier) {
        if (!first) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
            )
        }
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(min = Dimens.MinTarget)
                    .clip(RoundedCornerShape(0.dp))
                    .clickable(
                        enabled = enabled && onClick != null,
                        onClick = { onClick?.invoke() },
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = if (enabled) titleColor else titleColor.copy(alpha = 0.55f),
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) subtitleColor else subtitleColor.copy(alpha = 0.55f),
                    )
                }
            }
            if (trail != null) trail()
        }
    }
}

@Composable
internal fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    subtitle: String? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    SettingsRow(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        onClick = { if (enabled) onCheckedChange(!checked) },
        trail = {
            Switch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled,
                colors =
                    SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                        uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                    ),
            )
        },
        modifier = modifier,
    )
}

@Composable
internal fun SegmentedChoice(
    options: List<SegmentedOption>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Corners.Button))
                .padding(0.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        options.forEach { opt ->
            val isSelected = opt.value == selected
            val enabled = opt.enabled
            Surface(
                shape = RoundedCornerShape(Corners.Button),
                color =
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier =
                    Modifier.weight(1f)
                        .heightIn(min = Dimens.MinTarget - 8.dp)
                        .clip(RoundedCornerShape(Corners.Button))
                        .clickable(enabled = enabled, onClick = { onSelected(opt.value) })
                        .padding(horizontal = 4.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val labelColor = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        enabled -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                    Text(
                        text = opt.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = labelColor,
                    )
                }
            }
        }
    }
}

internal data class SegmentedOption(
    val value: String,
    val label: String,
    val enabled: Boolean = true,
)
