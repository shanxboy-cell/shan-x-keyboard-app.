package com.shanova.keyboard.keyboard.languages

import com.shanova.keyboard.keyboard.model.Key
import com.shanova.keyboard.keyboard.model.KeyRow
import com.shanova.keyboard.keyboard.model.KeyboardLayout
import com.shanova.keyboard.keyboard.model.backspaceKey
import com.shanova.keyboard.keyboard.model.bottomRow
import com.shanova.keyboard.keyboard.model.characterKeys
import com.shanova.keyboard.keyboard.model.shiftKey

/**
 * 🇮🇳 Tamil (தமிழ்).
 *
 * Consonants (மெய்யெழுத்து) on the normal layer;
 * vowels (உயிரெழுத்து), digits and special signs on the shift layer.
 */
object TamilLanguage : BaseLanguage() {

    override val id = "ta"
    override val displayName = "தமிழ்"
    override val flag = "🇮🇳"

    // Consonants + pulli and special consonants
    override val lettersNormal = KeyboardLayout(
        id = "ta_normal",
        rows = listOf(
            KeyRow(characterKeys("கஙசஞடணதநபம")),
            KeyRow(characterKeys("யரலவழளறனஂ்")),
            KeyRow(
                listOf(shiftKey()) +
                    characterKeys("ஜஷஸஹ") +
                    listOf(
                        Key(output = "க்ஷ", label = "க்ஷ"),
                        Key(output = "ஶ", label = "ஶ"),
                        Key(output = "ஃ", label = "ஃ")
                    ) +
                    listOf(backspaceKey())
            ),
            bottomRow(spaceLabel = displayName)
        )
    )

    // Vowels, Tamil digits and special symbols
    override val lettersShift = KeyboardLayout(
        id = "ta_shift",
        rows = listOf(
            KeyRow(characterKeys("அஆஇஈஉஊஎஏஐஒ")),
            KeyRow(characterKeys("ஓஔ௧௨௩௪௫௬௭௮")),
            KeyRow(
                listOf(shiftKey()) +
                    characterKeys("௯௰௱௲௹௺₹") +
                    listOf(backspaceKey())
            ),
            bottomRow(spaceLabel = displayName)
        )
    )
}
