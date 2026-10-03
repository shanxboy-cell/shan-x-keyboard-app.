package com.shanova.keyboard.keyboard.languages

import com.shanova.keyboard.keyboard.model.KeyRow
import com.shanova.keyboard.keyboard.model.KeyboardLayout
import com.shanova.keyboard.keyboard.model.backspaceKey
import com.shanova.keyboard.keyboard.model.bottomRow
import com.shanova.keyboard.keyboard.model.characterKeys
import com.shanova.keyboard.keyboard.model.shiftKey

/**
 * 🇱🇰 Sinhala (සිංහල).
 *
 * Consonants live on the normal layer; vowels, vowel signs and
 * diacritics live on the shift layer (Wijesekara-style grouping).
 */
object SinhalaLanguage : BaseLanguage() {

    override val id = "si"
    override val displayName = "සිංහල"
    override val flag = "🇱🇰"

    // Consonants
    override val lettersNormal = KeyboardLayout(
        id = "si_normal",
        rows = listOf(
            KeyRow(characterKeys("කඛගඝ඙චඡජඣඤ")),
            KeyRow(characterKeys("ටඨඩඪණතථදධන")),
            KeyRow(
                listOf(shiftKey()) +
                    characterKeys("පඵබභමයරල") +
                    listOf(backspaceKey())
            ),
            bottomRow(spaceLabel = displayName)
        )
    )

    // Vowels, vowel signs and diacritics
    override val lettersShift = KeyboardLayout(
        id = "si_shift",
        rows = listOf(
            KeyRow(characterKeys("අආඇඈඉඊඋඌඑඒ")),
            KeyRow(characterKeys("ඔඕඖංඃ්ාැෑි")),
            KeyRow(
                listOf(shiftKey()) +
                    characterKeys("ීුූෘෙේොෝ") +
                    listOf(backspaceKey())
            ),
            bottomRow(spaceLabel = displayName)
        )
    )
}
