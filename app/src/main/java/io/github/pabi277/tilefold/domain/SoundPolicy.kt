// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold.domain

/**
 * Pure policy for sound & haptics — host-testable, no Android imports.
 *
 * Mirrors `AdPolicy` (§ docs/DECISIONS.md) — the decision \"should play?\"
 * is pure; the Android wrapper actually plays.
 */
object SoundPolicy {
    /**
     * Whether a sound should play given the user's toggle and the device's
     * ringer mode.
     *
     * Rule (§ ROADMAP 4.4): gated by settings, silenced when the device is
     * on vibrate/silent. `ringerIsNormal` maps to AudioManager.RINGER_MODE_NORMAL.
     */
    fun shouldPlaySound(soundEnabled: Boolean, ringerIsNormal: Boolean): Boolean =
        soundEnabled && ringerIsNormal

    fun shouldVibrate(hapticsEnabled: Boolean, ringerIsNormal: Boolean): Boolean =
        hapticsEnabled && ringerIsNormal
}
