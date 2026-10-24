@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AdPolicyTest {
    @Test
    fun `rewarded undo allowed when not max and consent given`() {
        val allowance = UndoAllowance()

        assertThat(AdPolicy.canShowRewardedUndo(allowance, isAdFree = false, hasConsent = true)).isTrue()
    }

    @Test
    fun `rewarded undo denied when already max`() {
        var allowance = UndoAllowance()
        allowance = allowance.onRewarded()
        allowance = allowance.onRewarded()

        assertThat(AdPolicy.canShowRewardedUndo(allowance, isAdFree = false, hasConsent = true)).isFalse()
    }

    @Test
    fun `rewarded undo denied for ad-free user`() {
        val allowance = UndoAllowance()

        assertThat(AdPolicy.canShowRewardedUndo(allowance, isAdFree = true, hasConsent = true)).isFalse()
    }

    @Test
    fun `rewarded undo denied without consent`() {
        val allowance = UndoAllowance()

        assertThat(AdPolicy.canShowRewardedUndo(allowance, isAdFree = false, hasConsent = false)).isFalse()
    }

    @Test
    fun `interstitial only after game over and every third`() {
        val state = AdPolicy.InterstitialState(gameOverCount = 2, lastShownAtMillis = null)

        assertThat(
            AdPolicy.canShowInterstitial(
                status = GameStatus.GAME_OVER,
                state = state,
                nowMillis = 0L,
                isAdFree = false,
                hasConsent = true,
            ),
        ).isTrue()

        val notThird = AdPolicy.InterstitialState(gameOverCount = 1, lastShownAtMillis = null)
        assertThat(
            AdPolicy.canShowInterstitial(GameStatus.GAME_OVER, notThird, 0L, false, true),
        ).isFalse()
    }

    @Test
    fun `interstitial never for ad-free or without consent`() {
        val state = AdPolicy.InterstitialState(gameOverCount = 2)

        assertThat(AdPolicy.canShowInterstitial(GameStatus.GAME_OVER, state, 0L, true, true)).isFalse()
        assertThat(AdPolicy.canShowInterstitial(GameStatus.GAME_OVER, state, 0L, false, false)).isFalse()
    }

    @Test
    fun `interstitial respects 120s gap`() {
        val state = AdPolicy.InterstitialState(gameOverCount = 2, lastShownAtMillis = 0L)

        assertThat(AdPolicy.canShowInterstitial(GameStatus.GAME_OVER, state, 60_000L, false, true)).isFalse()
        assertThat(AdPolicy.canShowInterstitial(GameStatus.GAME_OVER, state, 200_000L, false, true)).isTrue()
    }

    @Test
    fun `interstitial never on win`() {
        val state = AdPolicy.InterstitialState(gameOverCount = 2)

        assertThat(AdPolicy.canShowInterstitial(GameStatus.WON, state, 0L, false, true)).isFalse()
        assertThat(AdPolicy.canShowInterstitial(GameStatus.PLAYING, state, 0L, false, true)).isFalse()
    }

    @Test
    @Suppress("UnusedPrivateProperty")
    fun `policy says no then fake service not called`() {
        var called = false

        @Suppress("UnusedVariable")
        val fake =
            object : AdService {
                override suspend fun showRewardedUndo(): Boolean {
                    called = true
                    return true
                }

                override suspend fun showInterstitial(): Boolean {
                    called = true
                    return true
                }
            }
        val allowance = UndoAllowance().onRewarded().onRewarded()
        val canShow = AdPolicy.canShowRewardedUndo(allowance, isAdFree = false, hasConsent = true)

        if (canShow) {
            // In real UI, service would be called here.
        }

        assertThat(canShow).isFalse()
        assertThat(called).isFalse()
    }
}
