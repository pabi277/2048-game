@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * Where every occupied cell goes during one slide.
 *
 * `from` and `to` are row-major cell indices. On a merge **both** source cells
 * slide to the same destination: the first entry is the tile that survives (it
 * becomes the doubled tile), the second is the tile it absorbed.
 */
data class TileSlide(
    val from: Int,
    val to: Int,
)

/**
 * The complete description of one valid move: the resulting board plus enough
 * geometry for a UI to animate the slide without re-deriving the rules.
 *
 * The engine computes this; the board layer only draws it. That is the phase-2
 * contract from `docs/ROADMAP.md` ("the UI never re-implements rules").
 */
data class MovePlan(
    val direction: Direction,
    /** The board after the slide, **before** the follow-up tile spawns. */
    val board: Board,
    /** One entry per occupied source cell, in reading order. */
    val slides: List<TileSlide>,
    /** Destination indices that received a merge. */
    val mergeDestinations: Set<Int>,
    val scoreDelta: Int,
    val mergedValues: List<Int>,
)
