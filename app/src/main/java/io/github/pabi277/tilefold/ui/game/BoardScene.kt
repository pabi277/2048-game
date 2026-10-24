package io.github.pabi277.tilefold.ui.game

import io.github.pabi277.tilefold.domain.Board
import io.github.pabi277.tilefold.domain.MovePlan

/**
 * One tile as the board draws it.
 *
 * [key] is a stable identity — Compose keeps a tile's animation state while the
 * key survives, which is what makes a slide look like the same tile moving
 * instead of two tiles blinking. [originIndex] is where the tile starts this
 * frame (equal to [index] unless it is a tile that is being absorbed into a
 * merge).
 */
internal data class TileView(
    val key: Long,
    val value: Int,
    val index: Int,
    val originIndex: Int = index,
    val spawned: Boolean = false,
    val merged: Boolean = false,
    val leaving: Boolean = false,
)

/**
 * What the board is showing right now: the tiles at their destinations plus the
 * tiles that are currently sliding into a merge and fading out.
 */
internal data class BoardScene(
    val tiles: List<TileView>,
    val leaving: List<TileView> = emptyList(),
    val nextKey: Long,
) {
    companion object {
        /** A still board — no animation, used for a fresh game, undo and tests. */
        fun still(board: Board): BoardScene {
            var key = 0L
            val tiles =
                board.values.mapIndexedNotNull { index, value ->
                    if (value == 0) {
                        null
                    } else {
                        TileView(key = key++, value = value, index = index)
                    }
                }
            return BoardScene(tiles = tiles, nextKey = key)
        }

        /**
         * Applies one move from the engine: every tile that moved keeps its key,
         * a merge leaves one survivor (carrying the doubled value) and one
         * fading tile, and the follow-up tile appears with a fresh key.
         *
         * The rules are **not** re-implemented here — everything comes from
         * [plan] and [nextBoard], which the engine produced.
         */
        fun after(
            scene: BoardScene,
            plan: MovePlan,
            nextBoard: Board,
        ): BoardScene {
            var key = scene.nextKey
            val tiles = mutableListOf<TileView>()
            val leaving = mutableListOf<TileView>()

            plan.slides.groupBy { it.to }.forEach { (destination, arrivals) ->
                val survivor = scene.tiles.firstOrNull { it.index == arrivals.first().from }
                if (survivor == null) return@forEach
                tiles.add(
                    survivor.copy(
                        index = destination,
                        originIndex = arrivals.first().from,
                        value = plan.board.values[destination],
                        merged = destination in plan.mergeDestinations,
                        spawned = false,
                        leaving = false,
                    ),
                )
                val absorbed =
                    arrivals.getOrNull(1)?.let { slide -> scene.tiles.firstOrNull { it.index == slide.from } }
                if (absorbed != null) {
                    leaving.add(
                        absorbed.copy(
                            key = key++,
                            index = destination,
                            originIndex = absorbed.index,
                            leaving = true,
                        ),
                    )
                }
            }

            val spawnedIndex = spawnIndex(plan, nextBoard)
            if (spawnedIndex != null) {
                tiles.add(
                    TileView(
                        key = key++,
                        value = nextBoard.values[spawnedIndex],
                        index = spawnedIndex,
                        spawned = true,
                    ),
                )
            }

            return BoardScene(tiles = tiles, leaving = leaving, nextKey = key)
        }

        /**
         * The cell the follow-up tile landed in: empty after the slide, occupied
         * after the spawn. There is exactly one, because the engine spawns once.
         */
        private fun spawnIndex(
            plan: MovePlan,
            nextBoard: Board,
        ): Int? =
            nextBoard.values.indices.firstOrNull { index ->
                plan.board.values[index] == 0 && nextBoard.values[index] != 0
            }
    }
}
