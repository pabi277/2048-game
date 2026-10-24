// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.ui.settings

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.domain.Language
import io.github.pabi277.tilefold.domain.ThemeChoice
import io.github.pabi277.tilefold.ui.components.AppBar
import io.github.pabi277.tilefold.ui.components.ConfirmDialog
import io.github.pabi277.tilefold.ui.components.RowGroup
import io.github.pabi277.tilefold.ui.components.SectionLabel
import io.github.pabi277.tilefold.ui.components.SegmentedChoice
import io.github.pabi277.tilefold.ui.components.SegmentedOption
import io.github.pabi277.tilefold.ui.components.SettingsRow
import io.github.pabi277.tilefold.ui.components.ToggleRow
import io.github.pabi277.tilefold.ui.icons.TilefoldIcons
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens

internal const val SETTINGS_SOUND_TAG = "settings-sound"
internal const val SETTINGS_HAPTICS_TAG = "settings-haptics"
internal const val SETTINGS_DPAD_TAG = "settings-dpad"

@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onSoundChanged: (Boolean) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onThemeChanged: (ThemeChoice) -> Unit,
    onLanguageChanged: (Language) -> Unit,
    onDpadChanged: (Boolean) -> Unit,
    onResetStats: () -> Unit,
    onLicences: () -> Unit,
    onPrivacy: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.ScreenPaddingVertical),
    ) {
        AppBar(title = stringResource(R.string.settings_title), onBack = onBack)

        SectionLabel(text = stringResource(R.string.settings_sec_play))
        RowGroup {
            ToggleRow(
                title = stringResource(R.string.settings_sound),
                subtitle =
                    if (state.soundEnabled) stringResource(R.string.settings_sound_on)
                    else stringResource(R.string.settings_sound_off),
                checked = state.soundEnabled,
                onCheckedChange = onSoundChanged,
                modifier = Modifier.testTag(SETTINGS_SOUND_TAG),
            )
            ToggleRow(
                title = stringResource(R.string.settings_haptics),
                subtitle =
                    if (state.hapticsEnabled) stringResource(R.string.settings_haptics_on)
                    else stringResource(R.string.settings_haptics_off),
                checked = state.hapticsEnabled,
                onCheckedChange = onHapticsChanged,
                modifier = Modifier.testTag(SETTINGS_HAPTICS_TAG),
            )
            ToggleRow(
                title = stringResource(R.string.settings_show_direction_pad),
                checked = state.dpadVisible,
                onCheckedChange = onDpadChanged,
                modifier = Modifier.testTag(SETTINGS_DPAD_TAG),
            )
        }

        SectionLabel(text = stringResource(R.string.settings_sec_look))
        SegmentedChoice(
            options =
                listOf(
                    SegmentedOption(ThemeChoice.SYSTEM.name, stringResource(R.string.settings_theme_system)),
                    SegmentedOption(ThemeChoice.LIGHT.name, stringResource(R.string.settings_theme_light)),
                    SegmentedOption(ThemeChoice.DARK.name, stringResource(R.string.settings_theme_dark)),
                ),
            selected = if (state.theme == ThemeChoice.GOLD) ThemeChoice.SYSTEM.name else state.theme.name,
            onSelected = { v -> onThemeChanged(ThemeChoice.valueOf(v)) },
        )
        RowGroup(modifier = Modifier.padding(top = 8.dp)) {
            SettingsRow(
                title = stringResource(R.string.settings_theme_gold),
                subtitle =
                    if (state.goldUnlocked) null else stringResource(R.string.settings_gold_locked),
                enabled = state.goldUnlocked,
                first = true,
                onClick = if (state.goldUnlocked) ({ onThemeChanged(ThemeChoice.GOLD) }) else null,
                trail = {
                    if (!state.goldUnlocked) {
                        Icon(
                            imageVector = TilefoldIcons.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                    } else if (state.theme == ThemeChoice.GOLD) {
                        Text(text = "✓", color = MaterialTheme.colorScheme.primary)
                    }
                },
            )
        }
        // Language segmented
        Box(modifier = Modifier.padding(top = 8.dp)) {
            SegmentedChoice(
                options =
                    listOf(
                        SegmentedOption(Language.EN.name, "English"),
                        SegmentedOption(Language.HI.name, "हिन्दी"),
                    ),
                selected = state.language.name,
                onSelected = { v -> onLanguageChanged(Language.valueOf(v)) },
            )
        }

        SectionLabel(text = stringResource(R.string.settings_sec_progress))
        RowGroup {
            SettingsRow(
                title = stringResource(R.string.settings_reset_stats),
                subtitle = stringResource(R.string.settings_reset_stats_sub),
                destructive = true,
                first = true,
                onClick = { showResetConfirm = true },
                trail = {
                    Icon(
                        imageVector = TilefoldIcons.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        }

        SectionLabel(text = stringResource(R.string.settings_sec_ads))
        RowGroup {
            when {
                state.isAdFree -> {
                    SettingsRow(
                        title = stringResource(R.string.settings_ad_free_active),
                        subtitle = stringResource(R.string.settings_ad_free_sub),
                        enabled = false,
                        first = true,
                        trail = { Text(text = "✓", color = MaterialTheme.colorScheme.tertiary) },
                    )
                }

                state.isStoreUnavailable -> {
                    SettingsRow(
                        title = stringResource(R.string.settings_remove_ads),
                        subtitle = stringResource(R.string.settings_store_unavailable),
                        enabled = false,
                        first = true,
                    )
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            text = stringResource(R.string.settings_rewarded_hidden),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    SettingsRow(
                        title = stringResource(R.string.settings_remove_ads),
                        subtitle = stringResource(R.string.settings_remove_ads_sub),
                        first = true,
                        onClick = {},
                        trail = {
                            Icon(
                                imageVector = TilefoldIcons.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                }
            }
        }

        SectionLabel(text = stringResource(R.string.settings_sec_about))
        RowGroup(modifier = Modifier.padding(bottom = 16.dp)) {
            SettingsRow(
                title = stringResource(R.string.settings_version, state.versionName),
                enabled = false,
                first = true,
            )
            SettingsRow(
                title = stringResource(R.string.settings_licences),
                onClick = onLicences,
                trail = {
                    Icon(
                        imageVector = TilefoldIcons.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
            SettingsRow(
                title = stringResource(R.string.settings_privacy),
                onClick = onPrivacy,
                trail = {
                    Icon(
                        imageVector = TilefoldIcons.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        }
    }

    if (showResetConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.settings_reset_stats_title),
            body = stringResource(R.string.settings_reset_stats_body),
            confirmLabel = stringResource(R.string.settings_reset),
            dismissLabel = stringResource(R.string.cancel),
            onConfirm = {
                showResetConfirm = false
                onResetStats()
            },
            onDismiss = { showResetConfirm = false },
        )
    }
}
