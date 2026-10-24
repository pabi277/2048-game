package io.github.pabi277.tilefold.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.pabi277.tilefold.ui.theme.Corners

internal const val CONFIRM_DIALOG_TAG = "confirm-dialog"

/**
 * Destructive confirm — restart game / reset stats.
 *
 * Mirrors the reference's `Dialog` with two actions (primary destructive,
 * secondary cancel) and a scrollable body at 2.0×.
 */
@Composable
internal fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Corners.Dialog),
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.testTag(CONFIRM_DIALOG_TAG),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column(
                    modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PrimaryButton(label = confirmLabel, onClick = onConfirm)
                    SecondaryButton(label = dismissLabel, onClick = onDismiss)
                }
            }
        }
    }
}
