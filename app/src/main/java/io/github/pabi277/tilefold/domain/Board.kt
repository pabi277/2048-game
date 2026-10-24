@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * An immutable 4×4 board. `0` means "empty cell"; every other value is a tile
 * (a power of two, 2 or higher).
 *
 * This is the engine's only data structure and it is deliberately dumb: it
 * validates nothing about the *rules* (those live in [Moves], [Game]) and it
 * never mutates. `domain/` has no Android imports and no wall clock — a board is
 * always reproducible from `(seed, moves)` (`rule.md` §5).
 */
class Board private constructor(
    private val cells: List<Int>,
) {
    init {
        require(cells.size == CELLS) { "A board needs exactly $CELLS cells, got ${cells.size}" }
    }

    /** Row-major copy of the cells; index = `row * WIDTH + col`. */
    val values: List<Int>
        get() = cells

    fun valueAt(
        row: Int,
        col: Int,
    ): Int = cells[row * WIDTH + col]

    fun isEmpty(
        row: Int,
        col: Int,
    ): Boolean = valueAt(row, col) == 0

    /** A copy with one cell replaced. Used by the spawner and by tests. */
    fun withCell(
        row: Int,
        col: Int,
        value: Int,
    ): Board {
        require(value == 0 || isTileValue(value)) { "Not a tile value: $value" }
        val copy = cells.toMutableList()
        copy[row * WIDTH + col] = value
        return Board(copy)
    }

    /** Indices (row-major) of the empty cells — the spawner picks from these. */
    fun emptyCells(): List<Int> = cells.indices.filter { cells[it] == 0 }

    val isFull: Boolean
        get() = cells.none { it == 0 }

    val highestTile: Int
        get() = cells.maxOrNull() ?: 0

    fun row(row: Int): List<Int> = (0 until WIDTH).map { valueAt(row, it) }

    fun column(col: Int): List<Int> = (0 until WIDTH).map { valueAt(it, col) }

    /**
     * Stable single-line form used by golden tests and failure messages:
     * rows joined with `/`, cells with `,` (e.g. `2,4,8,16/0,0,0,0/…`).
     */
    override fun toString(): String = (0 until WIDTH).joinToString("/") { row(it).joinToString(",") }

    override fun equals(other: Any?): Boolean = other is Board && other.cells == cells

    override fun hashCode(): Int = cells.hashCode()

    companion object {
        const val WIDTH = 4
        const val CELLS = WIDTH * WIDTH

        /** A tile value is a power of two ≥ 2. */
        fun isTileValue(value: Int): Boolean = value >= 2 && value and (value - 1) == 0

        /** The empty board: every cell is `0`. */
        val EMPTY: Board = Board(List(CELLS) { 0 })

        /** Builds a board from a row-major list of 16 cells, validating each value. */
        fun of(values: List<Int>): Board {
            require(values.size == CELLS) { "A board needs $CELLS values, got ${values.size}" }
            values.forEach { require(it == 0 || isTileValue(it)) { "Not a tile value: $it" } }
            return Board(values.toList())
        }

        /** Builds a board from four rows of four cells — the form tests read best. */
        fun rows(vararg rows: List<Int>): Board {
            require(rows.size == WIDTH) { "A board needs $WIDTH rows, got ${rows.size}" }
            rows.forEach { require(it.size == WIDTH) { "Each row needs $WIDTH cells, got ${it.size}" } }
            return of(rows.flatMap { it })
        }
    }
}

/** The four directions a swipe can take. */
enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    ;

    val isVertical: Boolean
        get() = this == UP || this == DOWN
}
