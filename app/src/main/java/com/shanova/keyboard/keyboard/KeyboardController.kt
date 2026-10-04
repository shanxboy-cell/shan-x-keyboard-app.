package com.shanova.keyboard.keyboard

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.shanova.keyboard.emoji.EmojiPanel
import com.shanova.keyboard.keyboard.languages.Language
import com.shanova.keyboard.keyboard.languages.LanguageRegistry
import com.shanova.keyboard.keyboard.model.Key
import com.shanova.keyboard.keyboard.model.KeyType
import com.shanova.keyboard.settings.Prefs
import com.shanova.keyboard.theme.KeyboardTheme

/**
 * Owns all keyboard state (language, layer, shift) and routes key
 * events to the active [InputConnection].
 *
 * UI rendering lives in [KeyboardView] / [EmojiPanel]; layouts live in
 * the language definitions; this class only coordinates them.
 */
class KeyboardController(private val context: Context) {

    enum class Layer { LETTERS, NUMBERS, SYMBOLS, EMOJI }

    private val prefs = Prefs(context)

    private lateinit var root: LinearLayout
    private lateinit var keyboardView: KeyboardView
    private lateinit var toolbar: KeyboardToolbar
    private var emojiPanel: EmojiPanel? = null

    private var language: Language = LanguageRegistry.byId(prefs.currentLanguageId)
    private var layer = Layer.LETTERS
    private var shiftActive = false
    private var theme: KeyboardTheme = loadTheme()

    /** Connection to the app currently receiving text. */
    var inputConnection: InputConnection? = null
    var editorInfo: EditorInfo? = null

    /* -------------------- lifecycle -------------------- */

    fun createRootView(): View {
        theme = loadTheme()
        root = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        keyboardView = KeyboardView(context)
        toolbar = KeyboardToolbar(
            context = context,
            theme = theme,
            onEmoji = { showEmoji() },
            onClipboard = { showClipboard() },
            onThemes = { openSettings() },
            onLanguage = { switchLanguage() },
            onSettings = { openSettings() }
        )
        applyTheme()
        wireKeyboard()
        addKeyboardView()
        showLetters()
        return root
    }

    /** Called when the keyboard (re)opens: reload prefs and rebuild. */
    fun refresh() {
        val newTheme = loadTheme()
        if (newTheme.id != theme.id) {
            theme = newTheme
            applyTheme()
        }
        language = LanguageRegistry.byId(prefs.currentLanguageId)
        keyboardView.apply {
            keySoundEnabled = prefs.keySoundEnabled
            hapticEnabled = prefs.hapticEnabled
            keyAnimationEnabled = prefs.keyAnimationEnabled
            reducedMotion = prefs.reducedMotion
            largeKeysEnabled = prefs.largeKeysEnabled
            highContrastEnabled = prefs.highContrastEnabled
            heightScale = prefs.keyboardHeightScale
        }
        when (layer) {
            Layer.EMOJI -> showEmoji()
            Layer.NUMBERS -> showLayout(Layer.NUMBERS)
            Layer.SYMBOLS -> showLayout(Layer.SYMBOLS)
            Layer.LETTERS -> showLetters()
        }
    }

    private fun applyTheme() {
        root.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(theme.backgroundColor, theme.backgroundGradientEnd)
        )
        keyboardView.theme = theme
        if (::toolbar.isInitialized) toolbar.applyTheme(theme)
    }

    private fun loadTheme(): KeyboardTheme = if (prefs.themeId == "custom") {
        KeyboardTheme.custom(prefs.customAccent, prefs.customBackground, prefs.customKey)
    } else {
        KeyboardTheme.byId(prefs.themeId)
    }

    private fun wireKeyboard() {
        keyboardView.apply {
            keySoundEnabled = prefs.keySoundEnabled
            hapticEnabled = prefs.hapticEnabled
            keyAnimationEnabled = prefs.keyAnimationEnabled
            reducedMotion = prefs.reducedMotion
            largeKeysEnabled = prefs.largeKeysEnabled
            highContrastEnabled = prefs.highContrastEnabled
            heightScale = prefs.keyboardHeightScale
            onKeyListener = object : KeyboardView.OnKeyListener {
                override fun onKey(key: Key) = handleKey(key)
            }
        }
    }

    /* -------------------- layers -------------------- */

    private fun showLetters() {
        layer = Layer.LETTERS
        swapToKeyboard()
        showLayout(Layer.LETTERS)
    }

    private fun showLayout(target: Layer) {
        layer = target
        swapToKeyboard()
        val layout = when (target) {
            Layer.LETTERS -> if (shiftActive) language.lettersShift else language.lettersNormal
            Layer.NUMBERS -> language.numbers
            Layer.SYMBOLS -> language.symbols
            Layer.EMOJI -> language.lettersNormal // not used
        }
        keyboardView.showLayout(
            layout,
            shiftActive = shiftActive,
            spaceLabel = language.displayName
        )
    }

    private fun swapToKeyboard() {
        if (emojiPanel != null || root.childCount == 0 || root.getChildAt(root.childCount - 1) !== keyboardView) {
            root.removeAllViews()
            addKeyboardView()
            emojiPanel = null
        }
    }

    private fun addKeyboardView() {
        if (prefs.toolbarEnabled) {
            root.addView(toolbar, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(38)
            ))
        }
        root.addView(keyboardView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))
    }

    private fun showEmoji() {
        layer = Layer.EMOJI
        val panel = EmojiPanel(
            context = context,
            theme = theme,
            recents = { prefs.recentEmojis },
            onEmojiPicked = { emoji ->
                commit(emoji)
                prefs.addRecentEmoji(emoji)
            },
            onBackspace = { backspace() },
            onEnter = { enter() },
            onClose = { showLetters() }
        )
        emojiPanel = panel
        root.removeAllViews()
        if (prefs.toolbarEnabled) root.addView(toolbar, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(38)
        ))
        root.addView(panel, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))
    }

    private fun showClipboard() {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        manager.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString()?.let { prefs.addClipboardItem(it) }
        val panel = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(theme.backgroundColor)
        }
        panel.addView(TextView(context).apply {
            text = "📋  Clipboard"
            textSize = 16f
            setTextColor(theme.primaryTextColor)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        })
        prefs.clipboardItems.forEach { item ->
            panel.addView(TextView(context).apply {
                text = item
                textSize = 15f
                maxLines = 2
                setTextColor(theme.primaryTextColor)
                setPadding(dp(12), dp(10), dp(12), dp(10))
                setOnClickListener { commit(item); showLetters() }
            })
        }
        panel.addView(TextView(context).apply {
            text = "ABC"
            gravity = android.view.Gravity.CENTER
            setTextColor(theme.primaryTextColor)
            setOnClickListener { showLetters() }
        })
        root.removeAllViews()
        if (prefs.toolbarEnabled) root.addView(toolbar, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(38)
        ))
        root.addView(panel, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))
        emojiPanel = null
    }

    private fun openSettings() {
        context.startActivity(Intent(context, com.shanova.keyboard.settings.SettingsActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /* -------------------- key handling -------------------- */

    private fun handleKey(key: Key) {
        when (key.type) {
            KeyType.CHARACTER -> {
                commit(key.output)
                if (shiftActive) {
                    shiftActive = false
                    if (layer == Layer.LETTERS) showLayout(Layer.LETTERS)
                }
            }
            KeyType.SHIFT -> {
                shiftActive = !shiftActive
                if (layer != Layer.LETTERS) layer = Layer.LETTERS
                showLayout(Layer.LETTERS)
            }
            KeyType.BACKSPACE -> backspace()
            KeyType.ENTER -> enter()
            KeyType.SPACE -> commit(" ")
            KeyType.LANGUAGE -> switchLanguage()
            KeyType.NUMBERS -> showLayout(Layer.NUMBERS)
            KeyType.SYMBOLS -> showLayout(Layer.SYMBOLS)
            KeyType.ABC -> {
                shiftActive = false
                showLetters()
            }
            KeyType.EMOJI -> showEmoji()
            KeyType.EMOJI_BACK -> showLetters()
        }
    }

    private fun switchLanguage() {
        language = LanguageRegistry.nextAfter(language)
        prefs.currentLanguageId = language.id
        shiftActive = false
        if (layer == Layer.EMOJI || layer == Layer.LETTERS) {
            showLetters()
        } else {
            showLayout(layer)
        }
    }

    /* -------------------- text ops -------------------- */

    private fun commit(text: String) {
        val ic = inputConnection ?: return
        ic.beginBatchEdit()
        ic.commitText(text, 1)
        ic.endBatchEdit()
    }

    private fun backspace() {
        val ic = inputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    private fun enter() {
        val ic = inputConnection ?: return
        val info = editorInfo
        val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
        if (action != null && action != EditorInfo.IME_ACTION_NONE &&
            info.imeOptions.and(EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0
        ) {
            ic.performEditorAction(action)
        } else {
            ic.commitText("\n", 1)
        }
    }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
