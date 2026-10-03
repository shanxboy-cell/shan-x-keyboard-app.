package com.shanova.keyboard.keyboard.languages

import com.shanova.keyboard.keyboard.model.KeyRow
import com.shanova.keyboard.keyboard.model.KeyboardLayout
import com.shanova.keyboard.keyboard.model.backspaceKey
import com.shanova.keyboard.keyboard.model.bottomRow
import com.shanova.keyboard.keyboard.model.characterKeys
import com.shanova.keyboard.keyboard.model.shiftKey

/** 🇬🇧 English — standard QWERTY. */
object EnglishLanguage : BaseLanguage() {

    override val id = "en"
    override val displayName = "English"
    override val flag = "🇬🇧"

    override val lettersNormal = KeyboardLayout(
        id = "en_normal",
        rows = listOf(
            KeyRow(characterKeys("qwertyuiop")),
            KeyRow(characterKeys("asdfghjkl"), marginWeight = 0.5f),
            KeyRow(listOf(shiftKey()) + characterKeys("zxcvbnm") + listOf(backspaceKey())),
            bottomRow(spaceLabel = displayName)
        )
    )

    override val lettersShift = KeyboardLayout(
        id = "en_shift",
        rows = listOf(
            KeyRow(characterKeys("QWERTYUIOP")),
            KeyRow(characterKeys("ASDFGHJKL"), marginWeight = 0.5f),
            KeyRow(listOf(shiftKey()) + characterKeys("ZXCVBNM") + listOf(backspaceKey())),
            bottomRow(spaceLabel = displayName)
        )
    )
}
