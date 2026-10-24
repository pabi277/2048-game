// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.ui.theme.Corners

/**
 * Card surface — the rounded card the reference renders with a 1dp outlineVariant
 * border. Used for ResumeCard and DailyCard on Home and for the How to play examples.
 */
@Composable
internal fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(Corners.Card),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier,
        content = content,
    )
}
