@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/**
 * The slide/merge rules, as pure functions over a [Board].
 *
 * Semantics (from `docs/UI-REFERENCE.md` "How to play" and `rule.md` §5, studied
 * clean-room from the MIT-licensed game's *behaviour* — no code copied):
 *
 * - every tile slides as far as it can towards the leading edge;
 * - a tile merges **at most once per move**;
 * - merges resolve from the leading edge inward, so `2 2 2 2` moved left becomes
 *   `4 4 0 0` (not `8 0 0 0`);
 * - a move that changes nothing is [MoveOutcome.NoMove] — the caller must not
 *   spawn a tile for it.
 */
object Moves {
    /** Result of a valid move: the new board, the score it earns and what merged. */
    data class Moved(
        val board: Board,
        val scoreDelta: Int,
        val mergedValues: List<Int>,
    ) : MoveOutcome

    /** A direction that cannot change the board. */
    data object NoMove : MoveOutcome

    fun apply(
        board: Board,
        direction: Direction,
    ): MoveOutcome {
        val lines = readLines(board, direction)
        val merged = lines.map { mergeLine(it) }

        val result = buildBoard(merged, direction)
        if (result == board) return NoMove

        return Moved(
            board = result,
            scoreDelta = merged.sumOf { it.gained },
            mergedValues = merged.flatMap { it.mergedValues },
        )
    }

    /** True when *no* direction changes the board. */
    fun hasAnyMove(board: Board): Boolean = Direction.entries.any { apply(board, it) != NoMove }

    private data class LineOutcome(
        val cells: List<Int>,
        val gained: Int,
        val mergedValues: List<Int>,
    )

    /**
     * Reads the four lines in movement order: the first element of each line is
     * the leading edge for that direction, so the merge scan below is written
     * once and used by all four directions.
     */
    private fun readLines(
        board: Board,
        direction: Direction,
    ): List<List<Int>> =
        (0 until Board.WIDTH).map { index ->
            val line =
                when (direction) {
                    Direction.LEFT -> board.row(index)
                    Direction.RIGHT -> board.row(index).reversed()
                    Direction.UP -> board.column(index)
                    Direction.DOWN -> board.column(index).reversed()
                }
            line
        }

    /** Puts the merged lines back into a board, undoing the read order above. */
    private fun buildBoard(
        merged: List<LineOutcome>,
        direction: Direction,
    ): Board {
        val cells = MutableList(Board.CELLS) { 0 }
        merged.forEachIndexed { index, line ->
            val flipped = direction == Direction.RIGHT || direction == Direction.DOWN
            val ordered = if (flipped) line.cells.reversed() else line.cells
            ordered.forEachIndexed { offset, value ->
                when (direction) {
                    Direction.LEFT, Direction.RIGHT -> cells[index * Board.WIDTH + offset] = value
                    Direction.UP, Direction.DOWN -> cells[offset * Board.WIDTH + index] = value
                }
            }
        }
        return Board.of(cells)
    }

    /** Compact, then merge adjacent equal pairs once, scanning from the leading edge. */
    private fun mergeLine(line: List<Int>): LineOutcome {
        val compact = line.filter { it != 0 }
        val out = mutableListOf<Int>()
        val mergedValues = mutableListOf<Int>()
        var gained = 0
        var index = 0
        while (index < compact.size) {
            val value = compact[index]
            val partner = compact.getOrNull(index + 1)
            if (partner == value) {
                val mergedValue = value * 2
                out.add(mergedValue)
                mergedValues.add(mergedValue)
                gained += mergedValue
                index += 2
            } else {
                out.add(value)
                index += 1
            }
        }
        while (out.size < Board.WIDTH) {
            out.add(0)
        }
        return LineOutcome(out.toList(), gained, mergedValues.toList())
    }
}

/** Either a change or nothing — never "a move with zero effect". */
sealed interface MoveOutcome
