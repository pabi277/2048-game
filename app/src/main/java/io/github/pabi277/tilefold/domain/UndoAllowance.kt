@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * How many undos the current game still allows.
 *
 * Rules (`docs/DECISIONS.md` — Undo rules):
 * - 1 free undo per game;
 * - undo is allowed after game-over (the dialog still shows the button);
 * - a rewarded ad gives +1, up to 3 total.
 *
 * The limit is a domain rule, not a UI whim, so it lives here and the UI only
 * asks [canUndo]. The engine's one-snapshot [Game.canUndo] is still the ground
 * truth — this allowance only caps *how many times* that snapshot may be used.
 */
data class UndoAllowance(
    val totalAllowed: Int = 1,
    val used: Int = 0,
) {
    init {
        require(totalAllowed in 1..MAX_TOTAL) { "totalAllowed must be 1..$MAX_TOTAL, got $totalAllowed" }
        require(used in 0..totalAllowed) { "used must be 0..$totalAllowed, got $used" }
    }

    val canUndo: Boolean
        get() = used < totalAllowed

    val canEarnMore: Boolean
        get() = totalAllowed < MAX_TOTAL

    fun onUndo(): UndoAllowance {
        require(canUndo) { "no undo remaining" }
        return copy(used = used + 1)
    }

    fun onRewarded(): UndoAllowance {
        require(canEarnMore) { "already at max" }
        return copy(totalAllowed = totalAllowed + 1)
    }

    fun reset(): UndoAllowance = UndoAllowance()

    companion object {
        const val MAX_TOTAL = 3
        const val FREE = 1
    }
}
