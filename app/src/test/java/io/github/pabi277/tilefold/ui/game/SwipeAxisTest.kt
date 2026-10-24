@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import com.google.common.truth.Truth.assertThat
import io.github.pabi277.tilefold.domain.Direction
import org.junit.Test

/**
 * The swipe → direction rule, tested without a touchscreen.
 *
 * The number that matters most is the threshold: a drag below it must return
 * `null`, because a `null` is a move that never happens and therefore never
 * spawns a tile (`rule.md` §5 — a refused input is free).
 */
class SwipeAxisTest {
    private val threshold = SwipeAxis.MIN_DRAG_DP

    @Test
    fun `each direction is read from the dominant axis`() {
        assertThat(SwipeAxis.direction(120f, 10f, threshold)).isEqualTo(Direction.RIGHT)
        assertThat(SwipeAxis.direction(-120f, 10f, threshold)).isEqualTo(Direction.LEFT)
        assertThat(SwipeAxis.direction(10f, 120f, threshold)).isEqualTo(Direction.DOWN)
        assertThat(SwipeAxis.direction(10f, -120f, threshold)).isEqualTo(Direction.UP)
    }

    @Test
    fun `a short drag is not a swipe`() {
        assertThat(SwipeAxis.direction(10f, 5f, threshold)).isNull()
        assertThat(SwipeAxis.direction(-10f, -5f, threshold)).isNull()
        assertThat(SwipeAxis.direction(0f, 0f, threshold)).isNull()
        assertThat(SwipeAxis.direction(threshold - 1f, 0f, threshold)).isNull()
    }

    @Test
    fun `a drag exactly at the threshold counts`() {
        assertThat(SwipeAxis.direction(threshold, 0f, threshold)).isEqualTo(Direction.RIGHT)
        assertThat(SwipeAxis.direction(0f, -threshold, threshold)).isEqualTo(Direction.UP)
    }

    @Test
    fun `the larger axis wins even when the drag is diagonal`() {
        assertThat(SwipeAxis.direction(80f, 60f, threshold)).isEqualTo(Direction.RIGHT)
        assertThat(SwipeAxis.direction(60f, 80f, threshold)).isEqualTo(Direction.DOWN)
    }

    @Test
    fun `an exact tie reads as horizontal`() {
        assertThat(SwipeAxis.direction(90f, 90f, threshold)).isEqualTo(Direction.RIGHT)
        assertThat(SwipeAxis.direction(-90f, -90f, threshold)).isEqualTo(Direction.LEFT)
    }

    @Test
    fun `only arrow keys map to a direction`() {
        assertThat(directionFor(Key.DirectionUp)).isEqualTo(Direction.UP)
        assertThat(directionFor(Key.DirectionDown)).isEqualTo(Direction.DOWN)
        assertThat(directionFor(Key.DirectionLeft)).isEqualTo(Direction.LEFT)
        assertThat(directionFor(Key.DirectionRight)).isEqualTo(Direction.RIGHT)
        assertThat(directionFor(Key.Spacebar)).isNull()
        assertThat(directionFor(Key.Enter)).isNull()
    }

    @Test
    fun `a key press plays once and a release does nothing`() {
        val moves = mutableListOf<Direction>()

        val handled = handleKey(KeyEventType.KeyDown, Key.DirectionLeft, moves::add)
        val ignored = handleKey(KeyEventType.KeyUp, Key.DirectionLeft, moves::add)
        val unknown = handleKey(KeyEventType.KeyDown, Key.Backspace, moves::add)

        assertThat(handled).isTrue()
        assertThat(ignored).isFalse()
        assertThat(unknown).isFalse()
        assertThat(moves).containsExactly(Direction.LEFT)
    }

    @Test
    fun `the default threshold is the documented 24dp`() {
        assertThat(SwipeAxis.MIN_DRAG_DP).isEqualTo(24f)
        assertThat(SwipeAxis.direction(23f, 0f)).isNull()
        assertThat(SwipeAxis.direction(25f, 0f)).isEqualTo(Direction.RIGHT)
    }
}
