package com.shanova.keyboard.keyboard.languages

import com.shanova.keyboard.keyboard.model.KeyRow
import com.shanova.keyboard.keyboard.model.KeyboardLayout
import com.shanova.keyboard.keyboard.model.abcKey
import com.shanova.keyboard.keyboard.model.backspaceKey
import com.shanova.keyboard.keyboard.model.bottomRow
import com.shanova.keyboard.keyboard.model.characterKeys
import com.shanova.keyboard.keyboard.model.enterKey
import com.shanova.keyboard.keyboard.model.languageKey
import com.shanova.keyboard.keyboard.model.emojiKey
import com.shanova.keyboard.keyboard.model.shiftKey
import com.shanova.keyboard.keyboard.model.spaceKey
import com.shanova.keyboard.keyboard.model.symbolsKey

/**
 * A keyboard language.
 *
 * To add a new language, implement this interface (or extend [BaseLanguage])
 * and register it in [LanguageRegistry]. No other part of the keyboard
 * needs to change.
 */
interface Language {
    val id: String
    val displayName: String   // native name shown on the space bar
    val flag: String

    val lettersNormal: KeyboardLayout
    val lettersShift: KeyboardLayout
    val numbers: KeyboardLayout
    val symbols: KeyboardLayout
}

/** Shared number / symbol layers so languages only define letter layers. */
object CommonLayers {

    val NUMBERS = KeyboardLayout(
        id = "numbers",
        rows = listOf(
            KeyRow(characterKeys("1234567890")),
            KeyRow(characterKeys("-/:;()$&@\"")),
            KeyRow(
                listOf(symbolsKey(1.4f)) +
                    characterKeys(".,?!'") +
                    listOf(backspaceKey(1.6f))
            ),
            KeyRow(
                listOf(
                    abcKey(),
                    languageKey(),
                    emojiKey(),
                    spaceKey(" ", 5f),
                    enterKey()
                )
            )
        )
    )

    val SYMBOLS = KeyboardLayout(
        id = "symbols",
        rows = listOf(
            KeyRow(characterKeys("[]{}#%^*+=")),
            KeyRow(characterKeys("_\\|~<>€£¥")),
            KeyRow(
                listOf(abcKey(1.4f)) +
                    characterKeys(".,?!'") +
                    listOf(backspaceKey(1.6f))
            ),
            KeyRow(
                listOf(
                    abcKey(),
                    languageKey(),
                    emojiKey(),
                    spaceKey(" ", 5f),
                    enterKey()
                )
            )
        )
    )
}

/** Convenience base: plug in two letter layers, get the rest for free. */
abstract class BaseLanguage : Language {
    override val numbers: KeyboardLayout get() = CommonLayers.NUMBERS
    override val symbols: KeyboardLayout get() = CommonLayers.SYMBOLS
}

/**
 * Registry of installed languages, in switch order.
 * New languages are added here — one line per language.
 */
object LanguageRegistry {

    val languages: List<Language> = listOf(
        SinhalaLanguage,
        EnglishLanguage,
        TamilLanguage
    )

    val default: Language = languages.first()

    fun byId(id: String?): Language =
        languages.firstOrNull { it.id == id } ?: default

    fun nextAfter(current: Language): Language {
        val idx = languages.indexOfFirst { it.id == current.id }
        return languages[(idx + 1).floorMod(languages.size)]
    }

    private fun Int.floorMod(m: Int): Int = ((this % m) + m) % m
}
