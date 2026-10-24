@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * The seeded generator and the spawner.
 *
 * xorshift64\* is a public algorithm, so these tests pin the *sequence* — if a
 * refactor changes the numbers, saved games stop replaying and the failure says
 * so immediately.
 */
class EngineRandomTest {
    @Test
    fun `the same seed always yields the same sequence`() {
        val first = EngineRandom.fromSeed(1L)
        val second = EngineRandom.fromSeed(1L)

        val a = List(50) { first.nextLong() }
        val b = List(50) { second.nextLong() }

        assertThat(a).isEqualTo(b)
    }

    @Test
    fun `the sequence is pinned for seed 1`() {
        val random = EngineRandom.fromSeed(1L)

        assertThat(
            listOf(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong()),
        ).containsExactly(
            5180492295206395165L,
            -6066446928794000099L,
            -5057245994778681513L,
            5599127315341312413L,
        ).inOrder()
    }

    @Test
    fun `a state restores the exact future`() {
        val original = EngineRandom.fromSeed(20261024L)
        repeat(7) { original.nextLong() }
        val saved = original.state

        val restored = EngineRandom.fromState(saved)

        assertThat(restored.state).isEqualTo(saved)
        assertThat(List(10) { restored.nextLong() }).isEqualTo(List(10) { original.nextLong() })
    }

    @Test
    fun `seed zero is mapped to a fixed non-zero state`() {
        val fromZero = EngineRandom.fromSeed(0L)
        val againFromZero = EngineRandom.fromSeed(0L)

        assertThat(fromZero.state).isNotEqualTo(0L)
        assertThat(fromZero.nextLong()).isEqualTo(againFromZero.nextLong())
        assertThat(fromZero.nextLong()).isNotEqualTo(0L)
    }

    @Test
    fun `nextInt stays inside its bound`() {
        val random = EngineRandom.fromSeed(3L)

        repeat(500) {
            assertThat(random.nextInt(16)).isAtLeast(0)
            assertThat(random.nextInt(16)).isLessThan(16)
        }
    }

    @Test
    fun `nextInt rejects a non-positive bound`() {
        val random = EngineRandom.fromSeed(3L)

        assertThat(runCatching { random.nextInt(0) }.isFailure).isTrue()
        assertThat(runCatching { random.nextInt(-1) }.isFailure).isTrue()
    }

    @Test
    fun `the spawner fills an empty cell and never overwrites a tile`() {
        val random = EngineRandom.fromSeed(11L)
        var board = Board.EMPTY

        repeat(Board.CELLS) { placed ->
            board = Spawner.spawn(board, random) ?: error("spawn returned null with a free cell")
            assertThat(board.emptyCells()).hasSize(Board.CELLS - placed - 1)
            assertThat(board.values.count { it != 0 }).isEqualTo(placed + 1)
        }
    }

    @Test
    fun `the spawner refuses a full board instead of looping`() {
        val full = Board.of(List(Board.CELLS) { 2 })

        assertThat(Spawner.spawn(full, EngineRandom.fromSeed(1L))).isNull()
    }

    @Test
    fun `the spawn values are exactly two or four`() {
        val random = EngineRandom.fromSeed(5L)

        repeat(200) {
            val spawned = Spawner.spawn(Board.EMPTY, random)!!
            assertThat(spawned.values.filter { it != 0 }.single()).isAnyOf(2, 4)
        }
    }

    @Test
    fun `a four appears about one time in ten`() {
        val random = EngineRandom.fromSeed(20261024L)
        val draws = 20000
        var fours = 0
        var board = Board.EMPTY

        repeat(draws) {
            board = Spawner.spawn(Board.EMPTY, random)!!
            if (board.highestTile == 4) fours++
        }

        val ratio = fours.toDouble() / draws
        // 10% expected; the tolerance is wide enough not to be flaky, tight
        // enough to catch a swapped 90/10 or an off-by-one threshold.
        assertThat(ratio).isAtLeast(0.085)
        assertThat(ratio).isAtMost(0.115)
    }

    @Test
    fun `the value is drawn before the cell`() {
        val board = Board.rows(listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0))
        val spawner = EngineRandom.fromSeed(1L)
        val probe = EngineRandom.fromSeed(1L)
        val valueDecidedByFirstDraw = if (probe.nextInt(10) >= 9) 4 else 2

        val spawned = Spawner.spawn(board, spawner)!!

        assertThat(spawned.values.single { it != 0 }).isEqualTo(valueDecidedByFirstDraw)
    }

    @Test
    fun `a spawn replayed from the saved state is the same spawn`() {
        val board = Board.rows(listOf(2, 2, 0, 0), listOf(0, 0, 4, 0), listOf(0, 0, 0, 0), listOf(8, 0, 0, 0))
        val first = EngineRandom.fromSeed(99L)
        val start = first.state

        val a = Spawner.spawn(board, first)!!
        val b = Spawner.spawn(board, EngineRandom.fromState(start))!!

        assertThat(b).isEqualTo(a)
    }
}
