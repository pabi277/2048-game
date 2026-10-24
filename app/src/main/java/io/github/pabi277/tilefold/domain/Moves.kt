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
 *
 * [plan] is the primitive: it returns the resulting board **and** the source →
 * destination geometry the board view animates. [apply] is the same computation
 * without the geometry.
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
        val plan = plan(board, direction) ?: return NoMove
        return Moved(
            board = plan.board,
            scoreDelta = plan.scoreDelta,
            mergedValues = plan.mergedValues,
        )
    }

    /** True when *no* direction changes the board. */
    fun hasAnyMove(board: Board): Boolean = Direction.entries.any { plan(board, it) != null }

    /**
     * @return the move's board and tile geometry, or `null` when the direction
     *         changes nothing (the caller must then never spawn or score).
     */
    fun plan(
        board: Board,
        direction: Direction,
    ): MovePlan? {
        val cells = MutableList(Board.CELLS) { 0 }
        val slides = mutableListOf<TileSlide>()
        val mergeDestinations = mutableSetOf<Int>()
        val mergedValues = mutableListOf<Int>()
        var gained = 0

        for (indices in lineIndices(direction)) {
            val line = slideLine(board, indices)
            indices.forEachIndexed { offset, cellIndex -> cells[cellIndex] = line.values[offset] }
            slides.addAll(line.slides)
            mergeDestinations.addAll(line.mergeDestinations)
            mergedValues.addAll(line.mergedValues)
            gained += line.gained
        }

        val result = Board.of(cells)
        if (result == board) return null

        return MovePlan(
            direction = direction,
            board = result,
            slides = slides,
            mergeDestinations = mergeDestinations,
            scoreDelta = gained,
            mergedValues = mergedValues,
        )
    }

    /** One line's worth of results, in movement order (leading edge first). */
    private class LineResult(
        val values: List<Int>,
        val slides: List<TileSlide>,
        val mergeDestinations: List<Int>,
        val mergedValues: List<Int>,
        val gained: Int,
    )

    /**
     * Reads the four lines as board-index lists in movement order: element 0 of
     * each list is the leading edge for that direction, so one scan below serves
     * all four directions.
     */
    private fun lineIndices(direction: Direction): List<List<Int>> =
        (0 until Board.WIDTH).map { line ->
            val alongRow = (0 until Board.WIDTH).map { step -> line * Board.WIDTH + step }
            val alongColumn = (0 until Board.WIDTH).map { step -> step * Board.WIDTH + line }
            when (direction) {
                Direction.LEFT -> alongRow
                Direction.RIGHT -> alongRow.reversed()
                Direction.UP -> alongColumn
                Direction.DOWN -> alongColumn.reversed()
            }
        }

    /** Compacts one line, then merges adjacent equal pairs once, from the leading edge. */
    private fun slideLine(
        board: Board,
        indices: List<Int>,
    ): LineResult {
        val occupied = indices.filter { board.values[it] != 0 }
        val values = MutableList(indices.size) { 0 }
        val slides = mutableListOf<TileSlide>()
        val mergeDestinations = mutableListOf<Int>()
        val mergedValues = mutableListOf<Int>()
        var gained = 0
        var destination = 0
        var cursor = 0

        while (cursor < occupied.size) {
            val from = occupied[cursor]
            val value = board.values[from]
            val partner = occupied.getOrNull(cursor + 1)
            val target = indices[destination]

            if (partner != null && board.values[partner] == value) {
                val mergedValue = value * 2
                slides.add(TileSlide(from, target))
                slides.add(TileSlide(partner, target))
                values[destination] = mergedValue
                mergeDestinations.add(target)
                mergedValues.add(mergedValue)
                gained += mergedValue
                cursor += 2
            } else {
                slides.add(TileSlide(from, target))
                values[destination] = value
                cursor += 1
            }
            destination += 1
        }

        return LineResult(values, slides, mergeDestinations, mergedValues, gained)
    }
}

/** Either a change or nothing — never "a move with zero effect". */
sealed interface MoveOutcome
