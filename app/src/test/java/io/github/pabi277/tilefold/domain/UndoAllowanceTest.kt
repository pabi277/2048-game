@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UndoAllowanceTest {
    @Test
    fun `fresh game allows one undo`() {
        val allowance = UndoAllowance()

        assertThat(allowance.canUndo).isTrue()
        assertThat(allowance.canEarnMore).isTrue()
        assertThat(allowance.totalAllowed).isEqualTo(1)
    }

    @Test
    fun `using the free undo exhausts allowance`() {
        val after = UndoAllowance().onUndo()

        assertThat(after.canUndo).isFalse()
        assertThat(after.used).isEqualTo(1)
        assertThat(after.totalAllowed).isEqualTo(1)
    }

    @Test
    fun `rewarded undo adds one up to max three`() {
        val one = UndoAllowance().onRewarded()
        val two = one.onRewarded()

        assertThat(one.totalAllowed).isEqualTo(2)
        assertThat(two.totalAllowed).isEqualTo(3)
        assertThat(two.canEarnMore).isFalse()
    }

    @Test
    fun `max three undos can be used`() {
        var allowance = UndoAllowance()
        allowance = allowance.onRewarded()
        allowance = allowance.onRewarded()
        allowance = allowance.onUndo()
        allowance = allowance.onUndo()
        allowance = allowance.onUndo()

        assertThat(allowance.canUndo).isFalse()
        assertThat(allowance.used).isEqualTo(3)
    }

    @Test
    fun `reset returns to one free`() {
        val used = UndoAllowance().onUndo()
        val reset = used.reset()

        assertThat(reset.totalAllowed).isEqualTo(1)
        assertThat(reset.used).isEqualTo(0)
    }
}
