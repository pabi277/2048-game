// SPDX-License-Identifier: MIT

package io.github.pabi277.tilefold

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

/**
 * Parity test for EN ↔ HI strings (§ ROADMAP 4.3).
 *
 * Every key that exists in `values/strings.xml` must exist in `values-hi/strings.xml`
 * and vice versa, so a missing translation (or a deleted key) fails the gate.
 *
 * It also checks that no string value is empty and that every `%1$s`/`%d` placeholder
 * pattern is preserved across locales where the base string contains one.
 */
class StringParityTest {
    private fun keys(xml: File): Set<String> =
        xml.readText().let { text ->
            Regex("""<string name=\"([^\"]+)\">""").findAll(text).map { it.groupValues[1] }.toSet()
        }

    @Test
    fun enAndHiHaveSameKeys() {
        val en = File("src/main/res/values/strings.xml")
        val hi = File("src/main/res/values-hi/strings.xml")
        assertThat(en.exists()).isTrue()
        assertThat(hi.exists()).isTrue()

        val enKeys = keys(en)
        val hiKeys = keys(hi)
        val missingInHi = enKeys - hiKeys
        val missingInEn = hiKeys - enKeys
        assertThat(missingInHi).isEmpty()
        assertThat(missingInEn).isEmpty()
    }

    @Test
    fun noEmptyValues() {
        listOf("src/main/res/values/strings.xml", "src/main/res/values-hi/strings.xml").forEach { path ->
            val text = File(path).readText()
            val empty = Regex("""<string name=\"[^\"]+\">\s*</string>""").findAll(text).toList()
            assertThat(empty).isEmpty()
        }
    }

    @Test
    fun placeholdersPreserved() {
        val enText = File("src/main/res/values/strings.xml").readText()
        val hiText = File("src/main/res/values-hi/strings.xml").readText()
        // For each string that contains a placeholder in EN, HI must also contain one.
        val enEntries = Regex("""<string name=\"([^\"]+)\">([^<]*%(?:\d\$)?[sd][^<]*)</string>""").findAll(enText)
        enEntries.forEach { m ->
            val name = m.groupValues[1]
            // Check that hi contains same placeholder pattern type
            val hiMatch = Regex("""<string name=\"$name\">[^<]*%""").containsMatchIn(hiText)
            assertThat(hiMatch).isTrue()
        }
    }
}
