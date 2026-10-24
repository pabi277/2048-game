@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * "Remove animations" on this device (Accessibility settings on Android 12+,
 * the animator duration scale on older versions).
 *
 * The board reads this once and snaps every transition when it is true, so a
 * player who has asked for no motion still gets the game — the tiles move, they
 * just do not animate. Phase 4 adds an in-app switch that feeds [Motion] the same
 * way (`docs/UI-REFERENCE.md` § Spacing, shape, elevation, motion).
 */
@Composable
internal fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) { systemRequestsReducedMotion(context) }
}

internal fun systemRequestsReducedMotion(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return runCatching { !ValueAnimator.areAnimatorsEnabled() }.getOrDefault(false)
    }
    val scale =
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE)
        }.getOrDefault(1f)
    return scale == 0f
}
