// SPDX-License-Identifier: MIT
@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import io.github.pabi277.tilefold.domain.Settings
import io.github.pabi277.tilefold.domain.SoundPolicy
import kotlin.math.PI
import kotlin.math.sin

/**
 * Sound controller for phase 4 — one merge cue, one win cue, one button tick.
 *
 * All tones are *generated* from sine waves into PCM buffers, so the
 * APK ships no binary audio assets and the requirement \"all original/generated
 * assets or none\" (§ ROADMAP 4.4) is met by construction. Each cue is gated by
 * [Settings.soundEnabled] and silenced when the device ringer is on vibrate/silent.
 *
 * Host-testable gate lives in [SoundPolicy]; this class only wraps Android's
 * AudioTrack + AudioManager. Playback is fire-and-forget on a short-lived
 * AudioTrack so no SoundPool assets need pre-loading.
 */
class SoundController(
    private val context: Context,
) {
    private val audioManager: AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private fun isRingerNormal(): Boolean =
        audioManager?.ringerMode == AudioManager.RINGER_MODE_NORMAL

    fun playMerge(settings: Settings) {
        if (!SoundPolicy.shouldPlaySound(settings.soundEnabled, isRingerNormal())) return
        playPcm(GeneratedTone.pcm(440, 80))
    }

    fun playWin(settings: Settings) {
        if (!SoundPolicy.shouldPlaySound(settings.soundEnabled, isRingerNormal())) return
        playPcm(GeneratedTone.pcmChirp(880, 1320, 220))
    }

    fun playTick(settings: Settings) {
        if (!SoundPolicy.shouldPlaySound(settings.soundEnabled, isRingerNormal())) return
        playPcm(GeneratedTone.pcm(1000, 40), volume = 0.6f)
    }

    fun release() {
        // No persistent pool to release; AudioTracks are one-shot.
    }

    private fun playPcm(pcm: ByteArray, volume: Float = 1f) {
        try {
            val track =
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build(),
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(GeneratedTone.SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    )
                    .setBufferSizeInBytes(pcm.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
            track.setVolume(volume)
            track.write(pcm, 0, pcm.size)
            track.play()
            // Release after playback finishes (duration + a little headroom)
            val durationMs = (pcm.size / 2L * 1000L) / GeneratedTone.SAMPLE_RATE + 30L
            track.setNotificationMarkerPosition(pcm.size / 2)
            // Simple delayed release without needing a handler: post to a thread
            Thread {
                try {
                    Thread.sleep(durationMs)
                } catch (_: InterruptedException) {
                    // ignore
                }
                try {
                    track.stop()
                } catch (_: Exception) {
                }
                track.release()
            }.start()
        } catch (_: Exception) {
            // Audio not available on this device/CI — silently ignore.
        }
    }
}

/**
 * Generates short PCM tones in memory for AudioTrack.
 * Uses 16-bit mono 44100Hz.
 */
internal object GeneratedTone {
    const val SAMPLE_RATE = 44100
    private const val AMPLITUDE = 28000

    fun pcm(freqHz: Int, durationMs: Int): ByteArray =
        sine(freqHz.toDouble(), durationMs)

    fun pcmChirp(fromHz: Int, toHz: Int, durationMs: Int): ByteArray {
        val samples = durationMs * SAMPLE_RATE / 1000
        val out = ByteArray(samples * 2)
        for (i in 0 until samples) {
            val t = i.toDouble() / samples
            val freq = fromHz + (toHz - fromHz) * t
            // Apply fade in/out to avoid click
            val envelope = when {
                i < samples * 0.05 -> i / (samples * 0.05)
                i > samples * 0.9 -> (samples - i) / (samples * 0.1)
                else -> 1.0
            }
            val sample = (sin(2 * PI * freq * i / SAMPLE_RATE) * AMPLITUDE * envelope).toInt().coerceIn(-32767, 32767)
            out[i * 2] = (sample and 0xFF).toByte()
            out[i * 2 + 1] = ((sample shr 8) and 0xFF).toByte()
        }
        return out
    }

    private fun sine(freqHz: Double, durationMs: Int): ByteArray {
        val samples = durationMs * SAMPLE_RATE / 1000
        val out = ByteArray(samples * 2)
        for (i in 0 until samples) {
            val envelope = when {
                i < samples * 0.04 -> i / (samples * 0.04)
                i > samples * 0.85 -> (samples - i) / (samples * 0.15)
                else -> 1.0
            }
            val sample = (sin(2 * PI * freqHz * i / SAMPLE_RATE) * AMPLITUDE * envelope).toInt().coerceIn(-32767, 32767)
            out[i * 2] = (sample and 0xFF).toByte()
            out[i * 2 + 1] = ((sample shr 8) and 0xFF).toByte()
        }
        return out
    }
}
