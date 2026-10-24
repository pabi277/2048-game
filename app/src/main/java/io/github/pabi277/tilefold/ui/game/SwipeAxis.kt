@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import io.github.pabi277.tilefold.domain.Direction
import kotlin.math.abs

/**
 * Turns a drag into a direction — the only rule the gesture layer owns, kept
 * pure so it can be tested without a touchscreen.
 *
 * Semantics (a deliberate, documented choice):
 * - a drag shorter than the threshold is **not** a swipe (`null`), so a tap or
 *   an accidental brush never costs a move and never spawns a tile;
 * - the dominant axis wins;
 * - an exact tie is read as horizontal, because a phone is taller than it is
 *   wide and vertical drags are the ones people start slightly off-axis.
 */
internal object SwipeAxis {
    /** 24dp: below the 48dp touch target, above normal finger jitter. */
    const val MIN_DRAG_DP = 24f

    /**
     * @param dx horizontal drag in px, end minus start
     * @param dy vertical drag in px, end minus start (positive = downwards)
     * @param threshold the minimum drag in px ([MIN_DRAG_DP] converted by the caller)
     */
    fun direction(
        dx: Float,
        dy: Float,
        threshold: Float = MIN_DRAG_DP,
    ): Direction? {
        val horizontal = abs(dx)
        val vertical = abs(dy)
        if (horizontal < threshold && vertical < threshold) return null

        return when {
            horizontal >= vertical && dx > 0f -> Direction.RIGHT
            horizontal >= vertical -> Direction.LEFT
            dy > 0f -> Direction.DOWN
            else -> Direction.UP
        }
    }
}
