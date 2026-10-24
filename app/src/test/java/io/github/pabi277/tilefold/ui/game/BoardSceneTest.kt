@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import com.google.common.truth.Truth.assertThat
import io.github.pabi277.tilefold.domain.Board
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.domain.EngineRandom
import io.github.pabi277.tilefold.domain.Game
import io.github.pabi277.tilefold.domain.GameSnapshot
import io.github.pabi277.tilefold.domain.MovePlan
import io.github.pabi277.tilefold.domain.Moves
import org.junit.Test

/**
 * The bridge between the engine and the animated board.
 *
 * The scene must describe the board the engine produced — same values, same
 * cells — while keeping tile identities so Compose can slide them. If this
 * drifts, the picture and the rules disagree, which is the one thing phase 2 is
 * not allowed to do.
 */
class BoardSceneTest {
    private fun board(vararg values: Int): Board = Board.of(values.toList())

    private fun valuesOf(scene: BoardScene): Map<Int, Int> = scene.tiles.associate { it.index to it.value }

    private fun plan(
        start: Board,
        direction: Direction,
    ): MovePlan = requireNotNull(Moves.plan(start, direction))

    @Test
    fun `a still scene mirrors the board`() {
        val scene = BoardScene.still(board(2, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

        assertThat(valuesOf(scene)).containsExactly(0, 2, 3, 4)
        assertThat(scene.tiles.map { it.key }.distinct()).hasSize(2)
        assertThat(scene.leaving).isEmpty()
    }

    @Test
    fun `a slide keeps every tile's identity`() {
        val start = board(0, 0, 2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val scene = BoardScene.still(start)
        val beforeKeys = scene.tiles.map { it.key }

        val next =
            BoardScene.after(
                scene = scene,
                plan = plan(start, Direction.LEFT),
                nextBoard = board(2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            )

        assertThat(next.tiles.map { it.key }).containsExactlyElementsIn(beforeKeys)
        assertThat(valuesOf(next)).containsExactly(0, 2, 1, 4)
        assertThat(next.tiles.first { it.index == 0 }.originIndex).isEqualTo(2)
        assertThat(next.tiles.none { it.spawned }).isTrue()
    }

    @Test
    fun `a merge leaves one survivor and one fading tile`() {
        val start = board(2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val scene = BoardScene.still(start)

        val next =
            BoardScene.after(
                scene = scene,
                plan = plan(start, Direction.LEFT),
                nextBoard = board(4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            )

        assertThat(next.tiles).hasSize(1)
        val survivor = next.tiles.single()
        assertThat(survivor.value).isEqualTo(4)
        assertThat(survivor.index).isEqualTo(0)
        assertThat(survivor.merged).isTrue()
        assertThat(next.leaving).hasSize(1)
        assertThat(next.leaving.single().leaving).isTrue()
        assertThat(next.leaving.single().index).isEqualTo(0)
    }

    @Test
    fun `the spawned tile is the one cell that was empty and is not`() {
        val start = board(0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val scene = BoardScene.still(start)
        val plan = plan(start, Direction.LEFT)

        val next = BoardScene.after(scene, plan, plan.board.withCell(3, 3, 4))

        val spawned = next.tiles.single { it.spawned }
        assertThat(spawned.index).isEqualTo(15)
        assertThat(spawned.value).isEqualTo(4)
        assertThat(next.tiles.single { it.index == 0 }.spawned).isFalse()
    }

    @Test
    fun `a real game's scene always matches its board`() {
        val random = EngineRandom.fromSeed(20261024L)
        val tiles = listOf(0, 0, 2, 2, 4, 8)
        val firstBoard = Board.of(List(Board.CELLS) { tiles[random.nextInt(tiles.size)] })
        var scene = BoardScene.still(firstBoard)
        var game = Game.restore(GameSnapshot(firstBoard, 0, 7L, false, false, 0))
        var moves = 0

        repeat(60) {
            val direction = Direction.entries[random.nextInt(Direction.entries.size)]
            val plan = Moves.plan(game.board, direction) ?: return@repeat
            val next = game.move(direction)
            if (next === game) return@repeat
            scene = BoardScene.after(scene, plan, next.board)
            game = next
            moves += 1

            val expected =
                next.board.values
                    .mapIndexedNotNull { index, value ->
                        if (value == 0) null else index to value
                    }.toMap()
            assertThat(valuesOf(scene)).isEqualTo(expected)
            // Leaving tiles are always a copy of something that merged.
            assertThat(scene.leaving).hasSize(plan.mergeDestinations.size)
            assertThat(scene.tiles.count { it.merged }).isEqualTo(plan.mergeDestinations.size)
        }

        assertThat(moves).isAtLeast(10)
    }

    @Test
    fun `absorbed tiles can be dropped without touching the survivors`() {
        val start = board(2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val scene =
            BoardScene.after(
                scene = BoardScene.still(start),
                plan = plan(start, Direction.LEFT),
                nextBoard = board(4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            )

        val cleared = scene.copy(leaving = emptyList())

        assertThat(cleared.tiles).isEqualTo(scene.tiles)
        assertThat(cleared.leaving).isEmpty()
    }
}
