@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * The movement plan the board view animates from.
 *
 * `plan.board` must always equal `apply(...)`, and the slides must describe every
 * occupied cell exactly once — a UI that draws from this cannot invent a rule,
 * and a plan that lies would show a tile moving where the engine never put it.
 */
class MovePlanTest {
    private fun board(vararg values: Int): Board = Board.of(values.toList())

    @Test
    fun `a slide maps every occupied cell to its destination`() {
        val start = board(2, 0, 0, 4, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val plan = Moves.plan(start, Direction.LEFT)!!

        assertThat(plan.slides).containsExactly(TileSlide(0, 0), TileSlide(3, 1), TileSlide(5, 4))
        assertThat(plan.mergeDestinations).isEmpty()
        assertThat(plan.board.toString()).isEqualTo("2,4,0,0/8,0,0,0/0,0,0,0/0,0,0,0")
    }

    @Test
    fun `both halves of a merge slide onto the same destination`() {
        val start = board(2, 2, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val plan = Moves.plan(start, Direction.LEFT)!!

        assertThat(plan.slides)
            .containsExactly(TileSlide(0, 0), TileSlide(1, 0), TileSlide(2, 1), TileSlide(3, 1))
        assertThat(plan.mergeDestinations).containsExactly(0, 1)
        assertThat(plan.mergedValues).containsExactly(4, 16).inOrder()
        assertThat(plan.board.toString()).isEqualTo("4,16,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
    }

    @Test
    fun `the first slide onto a merge destination is the survivor`() {
        val start = board(0, 0, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val plan = Moves.plan(start, Direction.RIGHT)!!

        val ontoThree = plan.slides.filter { it.to == 3 }
        assertThat(ontoThree.map { it.from }).containsExactly(3, 2).inOrder()
    }

    @Test
    fun `a four fold line resolves into two merges from the leading edge`() {
        val start = board(2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val plan = Moves.plan(start, Direction.LEFT)!!

        assertThat(plan.slides)
            .containsExactly(
                TileSlide(0, 0),
                TileSlide(1, 0),
                TileSlide(2, 1),
                TileSlide(3, 1),
            )
        assertThat(plan.board.toString()).isEqualTo("4,4,0,0/0,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(plan.scoreDelta).isEqualTo(8)
    }

    @Test
    fun `vertical plans use column destinations`() {
        val start = board(2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0)

        val up = Moves.plan(start, Direction.UP)!!
        val down = Moves.plan(start, Direction.DOWN)!!

        assertThat(up.slides).containsExactly(TileSlide(0, 0), TileSlide(4, 0), TileSlide(12, 4))
        assertThat(up.board.toString()).isEqualTo("4,0,0,0/4,0,0,0/0,0,0,0/0,0,0,0")
        assertThat(down.slides).containsExactly(TileSlide(12, 12), TileSlide(4, 8), TileSlide(0, 8))
        assertThat(down.board.toString()).isEqualTo("0,0,0,0/0,0,0,0/4,0,0,0/4,0,0,0")
    }

    @Test
    fun `a direction that changes nothing has no plan`() {
        val packed = board(2, 4, 8, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        assertThat(Moves.plan(packed, Direction.LEFT)).isNull()
    }

    @Test
    fun `the plan and the move always agree`() {
        val random = EngineRandom.fromSeed(20261024L)
        val tiles = listOf(0, 0, 2, 2, 4, 8, 16, 32, 64)
        val boards = List(200) { Board.of(List(Board.CELLS) { tiles[random.nextInt(tiles.size)] }) }

        boards.forEach { start ->
            Direction.entries.forEach { direction ->
                val plan = Moves.plan(start, direction)
                val moved = Moves.apply(start, direction)
                if (plan == null) {
                    assertThat(moved).isEqualTo(Moves.NoMove)
                } else {
                    assertThat(moved).isInstanceOf(Moves.Moved::class.java)
                    assertThat((moved as Moves.Moved).board).isEqualTo(plan.board)
                    assertThat(moved.scoreDelta).isEqualTo(plan.scoreDelta)
                    assertThat(moved.mergedValues).isEqualTo(plan.mergedValues)
                    // Every occupied cell is described exactly once.
                    assertThat(plan.slides.map { it.from }.distinct()).hasSize(plan.slides.size)
                }
            }
        }
    }

    @Test
    fun `a merged destination is worth two sources`() {
        val random = EngineRandom.fromSeed(77L)
        val tiles = listOf(0, 2, 2, 4, 4, 8)
        repeat(100) {
            val start = Board.of(List(Board.CELLS) { tiles[random.nextInt(tiles.size)] })
            Direction.entries.forEach { direction ->
                val plan = Moves.plan(start, direction) ?: return@forEach
                plan.mergeDestinations.forEach { destination ->
                    assertThat(plan.slides.count { it.to == destination }).isEqualTo(2)
                }
                assertThat(plan.mergeDestinations).hasSize(plan.mergedValues.size)
            }
        }
    }
}
