package io.github.pabi277.tilefold.ui.game

import io.github.pabi277.tilefold.ui.theme.Durations

/**
 * The one place that decides whether the board animates.
 *
 * Durations come from the frozen tokens ([Durations]); this object only applies
 * the accessibility rule: when the system asks for no animations, every
 * duration becomes 0 and the board renders its final state immediately.
 * Phase 4 adds an in-app "reduce motion" setting that feeds the same flag.
 */
internal object Motion {
    fun slide(reduced: Boolean): Int = if (reduced) 0 else Durations.SLIDE_MS

    fun pop(reduced: Boolean): Int = if (reduced) 0 else Durations.POP_MS

    fun spawn(reduced: Boolean): Int = if (reduced) 0 else Durations.SPAWN_MS

    fun score(reduced: Boolean): Int = if (reduced) 0 else Durations.SCORE_MS
}
