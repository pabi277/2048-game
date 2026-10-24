// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Chip — small rounded label used for streak, daily, success/new-best.
 *
 * Tones:
 * - accent: coloured fill (daily streak)
 * - success: green fill (new best)
 * - outline: transparent with border (resume hint)
 * - neutral: muted fill
 */
@Composable
internal fun AppChip(
    label: String,
    modifier: Modifier = Modifier,
    tone: ChipTone = ChipTone.Neutral,
) {
    val (container, content) =
        when (tone) {
            ChipTone.Accent -> {
                MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
            }

            ChipTone.Success -> {
                MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
            }

            ChipTone.Outline -> {
                MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurfaceVariant
            }

            ChipTone.Neutral -> {
                MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
            }
        }
    Surface(
        shape = RoundedCornerShape(50),
        color = container,
        border =
            if (tone == ChipTone.Outline) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = content,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

internal enum class ChipTone {
    Accent,
    Success,
    Outline,
    Neutral,
}
