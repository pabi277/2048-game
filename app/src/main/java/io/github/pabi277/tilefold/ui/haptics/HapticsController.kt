// SPDX-License-Identifier: MIT
@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.media.AudioManager
import io.github.pabi277.tilefold.domain.Settings
import io.github.pabi277.tilefold.domain.SoundPolicy

/**
 * Haptics controller — one short tap on merge, gated by [Settings.hapticsEnabled]
 * and silenced when the device is on vibrate/silent is false? Actually haptics
 * should still run when ringer is normal only, per ROADMAP: \"gated by settings,
 * silenced when device is on vibrate\". Here \"silenced when on vibrate\" for
 * haptics means: when the ringer is *silent*, don't vibrate? But spec says
 * sound/haptics silenced when device on vibrate — i.e. both respect ringer.
 * We apply same rule as sound: only vibrate when ringer is NORMAL.
 *
 * Uses platform Vibrator; no custom assets needed.
 */
class HapticsController(
    private val context: Context,
) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val audioManager: AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private fun isRingerNormal(): Boolean =
        audioManager?.ringerMode == AudioManager.RINGER_MODE_NORMAL

    fun buzzMerge(settings: Settings) {
        if (!SoundPolicy.shouldVibrate(settings.hapticsEnabled, isRingerNormal())) return
        vibrate(20)
    }

    fun buzzWin(settings: Settings) {
        if (!SoundPolicy.shouldVibrate(settings.hapticsEnabled, isRingerNormal())) return
        vibrate(40)
    }

    fun buzzTick(settings: Settings) {
        if (!SoundPolicy.shouldVibrate(settings.hapticsEnabled, isRingerNormal())) return
        vibrate(12)
    }

    private fun vibrate(durationMs: Long) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vib.vibrate(durationMs)
        }
    }
}
