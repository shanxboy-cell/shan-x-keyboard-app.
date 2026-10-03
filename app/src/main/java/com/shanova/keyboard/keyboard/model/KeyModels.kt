package com.shanova.keyboard.keyboard.model

/** Functional category of a key. */
enum class KeyType {
    CHARACTER,      // commits [output] text
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    LANGUAGE,       // cycle to next language
    SYMBOLS,        // switch to symbol layer
    NUMBERS,        // switch to number layer
    ABC,            // back to letters layer
    EMOJI,          // open emoji panel
    EMOJI_BACK      // close emoji panel
}

/**
 * One key on the keyboard.
 *
 * @param output text committed for CHARACTER keys
 * @param label  rendered label (defaults to [output])
 * @param widthWeight relative width inside its row (1f = standard letter key)
 */
data class Key(
    val type: KeyType = KeyType.CHARACTER,
    val output: String = "",
    val label: String = output,
    val widthWeight: Float = 1f
)

/** One horizontal row of keys, with optional side margins. */
data class KeyRow(
    val keys: List<Key>,
    val marginWeight: Float = 0f
)

/**
 * A full keyboard layer (e.g. letters, shifted letters, numbers, symbols).
 */
data class KeyboardLayout(
    val id: String,
    val rows: List<KeyRow>
)

/* ------------------------- Builder helpers ------------------------- */

fun characterKeys(chars: String): List<Key> =
    chars.map { Key(output = it.toString()) }

fun shiftKey(weight: Float = 1.4f) =
    Key(KeyType.SHIFT, label = "⇧", widthWeight = weight)

fun backspaceKey(weight: Float = 1.4f) =
    Key(KeyType.BACKSPACE, label = "⌫", widthWeight = weight)

fun enterKey(weight: Float = 1.6f) =
    Key(KeyType.ENTER, label = "⏎", widthWeight = weight)

fun languageKey(weight: Float = 1.2f) =
    Key(KeyType.LANGUAGE, label = "🌐", widthWeight = weight)

fun symbolsKey(weight: Float = 1.4f) =
    Key(KeyType.SYMBOLS, label = "#+=", widthWeight = weight)

fun numbersKey(weight: Float = 1.4f) =
    Key(KeyType.NUMBERS, label = "123", widthWeight = weight)

fun abcKey(weight: Float = 1.4f) =
    Key(KeyType.ABC, label = "ABC", widthWeight = weight)

fun emojiKey(weight: Float = 1.2f) =
    Key(KeyType.EMOJI, label = "😊", widthWeight = weight)

fun spaceKey(label: String, weight: Float = 5f) =
    Key(KeyType.SPACE, output = " ", label = label, widthWeight = weight)

fun emojiBackKey(weight: Float = 1.6f) =
    Key(KeyType.EMOJI_BACK, label = "ABC", widthWeight = weight)

/**
 * Builds the standard bottom row used by every letter layer:
 * [symbols/numbers] [language] [emoji] [SPACE] [enter]
 */
fun bottomRow(
    spaceLabel: String,
    leftKey: Key = numbersKey()
): KeyRow = KeyRow(
    keys = listOf(
        leftKey,
        languageKey(),
        emojiKey(),
        spaceKey(spaceLabel),
        enterKey()
    )
)
