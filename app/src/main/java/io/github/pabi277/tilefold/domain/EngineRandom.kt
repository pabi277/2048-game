@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * The engine's own pseudo-random source: **xorshift64\***, seeded and
 * serialisable.
 *
 * Why not `java.util.Random` or `kotlin.random.Random`?
 * - the whole engine must be reproducible from `(seed, moves)`, so a saved game
 *   stores the generator's *state*, not a re-seed (`docs/DECISIONS.md`, "Engine
 *   RNG" and "Undo rules");
 * - no wall clock and no platform class may leak into `domain/` (`rule.md` §5).
 *
 * The algorithm is public and tiny (Marsaglia's xorshift, with the multiply from
 * Vigna's xorshift64\*). [nextInt] uses the high 63 bits modulo the bound: the
 * bias is at most `bound / 2^63`, which is irrelevant here and is not corrected
 * for — this is a game's tile spawner, not a cryptographic source.
 *
 * The cursor is mutable for drawing convenience, but [state] is the complete
 * generator: rebuild it with [fromState] and the future is identical.
 */
class EngineRandom private constructor(
    seedState: Long,
) {
    private var cursor: Long = if (seedState == 0L) FALLBACK_STATE else seedState

    /** The generator's position — store this to make a future reproducible. */
    val state: Long
        get() = cursor

    /** Next 64 raw bits. Advances [state]. */
    fun nextLong(): Long {
        var x = cursor
        x = x xor (x ushr SHIFT_A)
        x = x xor (x shl SHIFT_B)
        x = x xor (x ushr SHIFT_C)
        cursor = x
        return x * MULTIPLIER
    }

    /** Uniform-enough value in `0 until bound`. */
    fun nextInt(bound: Int): Int {
        require(bound > 0) { "bound must be positive, got $bound" }
        val bits = nextLong() ushr 1
        return (bits % bound).toInt()
    }

    companion object {
        private const val FALLBACK_STATE = -0x61c8864680b583ebL
        private const val MULTIPLIER = 2685821657736338717L
        private const val SHIFT_A = 12
        private const val SHIFT_B = 25
        private const val SHIFT_C = 27

        /** Starts a generator at [seed]. Seed `0` is mapped to a fixed non-zero state. */
        fun fromSeed(seed: Long): EngineRandom = EngineRandom(seed)

        /** Restarts a generator at a previously saved [state]. */
        fun fromState(state: Long): EngineRandom = EngineRandom(state)
    }
}

/**
 * Places the tile that follows a valid move: a 2 nine times in ten, a 4 the rest,
 * in a uniformly chosen empty cell.
 *
 * Draw order is **value first, then cell** — fixed so that a saved generator
 * state always yields the same tile in the same place.
 */
object Spawner {
    const val FOUR_IN_TEN = 10
    const val FOUR_THRESHOLD = 9

    /**
     * @return the board with one new tile, or `null` when the board is full
     *         (which cannot happen after a valid move, but is handled anyway).
     */
    fun spawn(
        board: Board,
        random: EngineRandom,
    ): Board? {
        val empty = board.emptyCells()
        if (empty.isEmpty()) return null

        val value = if (random.nextInt(FOUR_IN_TEN) >= FOUR_THRESHOLD) 4 else 2
        val index = empty[random.nextInt(empty.size)]
        return board.withCell(index / Board.WIDTH, index % Board.WIDTH, value)
    }
}
