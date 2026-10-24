@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * Pure policy for ads — no SDK import, no `Context`, no side effect.
 *
 * The UI asks the policy first; only when it answers `true` does the UI touch
 * the SDK. That is what makes \"policy says no ⇒ no SDK call\" provable in a
 * host test with a fake [AdService].
 *
 * Rules (`docs/DECISIONS.md`):
 * - rewarded undo: only when [UndoAllowance.canEarnMore] and the caller has
 *   consent and is not ad-free; the host decides whether to show the ad after
 *   this check;
 * - interstitial: only after the game-over dialog is dismissed, at most 1 per
 *   3 game-overs and ≥120 s apart, never for ad-free users, never before
 *   consent, never on win/keep-playing.
 */
object AdPolicy {
    const val INTERSTITIAL_GAME_OVER_INTERVAL = 3
    const val INTERSTITIAL_MIN_GAP_MILLIS = 120_000L

    data class InterstitialState(
        val gameOverCount: Int = 0,
        val lastShownAtMillis: Long? = null,
    )

    /**
     * Whether a rewarded-undo ad may be requested.
     *
     * @param allowance current undo allowance of this game
     * @param isAdFree purchase entitlement — when true, rewarded is unnecessary
     * @param hasConsent GDPR / consent gate — rewarded still needs consent
     */
    fun canShowRewardedUndo(
        allowance: UndoAllowance,
        isAdFree: Boolean,
        hasConsent: Boolean,
    ): Boolean {
        if (isAdFree) return false
        if (!hasConsent) return false
        return allowance.canEarnMore
    }

    /**
     * Whether an interstitial may be shown after a game-over dismissal.
     *
     * @param status the game state that just ended — must be [GameStatus.GAME_OVER]
     * @param state how many game-overs have happened and when the last interstitial ran
     * @param nowMillis wall clock in millis (UTC) — supplied by the caller so tests can fix it
     * @param isAdFree when true, never
     * @param hasConsent when false, never
     */
    @Suppress("ReturnCount", "ktlint:standard:function-signature")
    fun canShowInterstitial(
        status: GameStatus,
        state: InterstitialState,
        nowMillis: Long,
        isAdFree: Boolean,
        hasConsent: Boolean,
    ): Boolean {
        if (isAdFree) return false
        if (!hasConsent) return false
        if (status != GameStatus.GAME_OVER) return false
        val nextCount = state.gameOverCount + 1
        if (nextCount % INTERSTITIAL_GAME_OVER_INTERVAL != 0) return false
        val last = state.lastShownAtMillis
        if (last != null && nowMillis - last < INTERSTITIAL_MIN_GAP_MILLIS) return false
        return true
    }

    fun onGameOver(state: InterstitialState): InterstitialState =
        state.copy(gameOverCount = state.gameOverCount + 1)

    fun onInterstitialShown(
        state: InterstitialState,
        nowMillis: Long,
    ): InterstitialState = state.copy(lastShownAtMillis = nowMillis)
}

/**
 * The SDK boundary — only touched after [AdPolicy] says yes.
 *
 * The production implementation calls Google Mobile Ads with test ad units;
 * tests supply a fake that records whether it was called.
 */
interface AdService {
    suspend fun showRewardedUndo(): Boolean
    suspend fun showInterstitial(): Boolean
}
