package com.shanova.keyboard.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Local-only preference store for SHAN-X-NOVA Keyboard.
 * Everything stays on the device — nothing is uploaded, nothing is logged.
 */
class Prefs(context: Context) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    var keyboardHeightScale: Float
        get() = sp.getFloat(KEY_HEIGHT, 1.0f)
        set(v) = sp.edit().putFloat(KEY_HEIGHT, v.coerceIn(0.85f, 1.35f)).apply()

    var keySoundEnabled: Boolean
        get() = sp.getBoolean(KEY_SOUND, false)
        set(v) = sp.edit().putBoolean(KEY_SOUND, v).apply()

    var hapticEnabled: Boolean
        get() = sp.getBoolean(KEY_HAPTIC, true)
        set(v) = sp.edit().putBoolean(KEY_HAPTIC, v).apply()

    var keyAnimationEnabled: Boolean
        get() = sp.getBoolean(KEY_ANIMATION, true)
        set(v) = sp.edit().putBoolean(KEY_ANIMATION, v).apply()

    var popupPreviewEnabled: Boolean
        get() = sp.getBoolean(KEY_POPUP_PREVIEW, false)
        set(v) = sp.edit().putBoolean(KEY_POPUP_PREVIEW, v).apply()

    var reducedMotion: Boolean
        get() = sp.getBoolean(KEY_REDUCED_MOTION, false)
        set(v) = sp.edit().putBoolean(KEY_REDUCED_MOTION, v).apply()

    var largeKeysEnabled: Boolean
        get() = sp.getBoolean(KEY_LARGE_KEYS, false)
        set(v) = sp.edit().putBoolean(KEY_LARGE_KEYS, v).apply()

    var highContrastEnabled: Boolean
        get() = sp.getBoolean(KEY_HIGH_CONTRAST, false)
        set(v) = sp.edit().putBoolean(KEY_HIGH_CONTRAST, v).apply()

    var toolbarEnabled: Boolean
        get() = sp.getBoolean(KEY_TOOLBAR, true)
        set(v) = sp.edit().putBoolean(KEY_TOOLBAR, v).apply()

    var clipboardHistoryEnabled: Boolean
        get() = sp.getBoolean(KEY_CLIPBOARD_HISTORY, true)
        set(v) = sp.edit().putBoolean(KEY_CLIPBOARD_HISTORY, v).apply()

    var personalizedLearningEnabled: Boolean
        get() = sp.getBoolean(KEY_LEARNING, false)
        set(v) = sp.edit().putBoolean(KEY_LEARNING, v).apply()

    var strictOfflineMode: Boolean
        get() = sp.getBoolean(KEY_OFFLINE, true)
        set(v) = sp.edit().putBoolean(KEY_OFFLINE, v).apply()

    var profileId: String
        get() = sp.getString(KEY_PROFILE, "default") ?: "default"
        set(v) = sp.edit().putString(KEY_PROFILE, v).apply()

    var themeId: String
        get() = sp.getString(KEY_THEME, "shan_x_nova") ?: "shan_x_nova"
        set(v) = sp.edit().putString(KEY_THEME, v).apply()

    var customAccent: String
        get() = sp.getString(KEY_CUSTOM_ACCENT, "#36D28B") ?: "#36D28B"
        set(v) = sp.edit().putString(KEY_CUSTOM_ACCENT, v).apply()

    var customBackground: String
        get() = sp.getString(KEY_CUSTOM_BACKGROUND, "#070909") ?: "#070909"
        set(v) = sp.edit().putString(KEY_CUSTOM_BACKGROUND, v).apply()

    var customKey: String
        get() = sp.getString(KEY_CUSTOM_KEY, "#18211E") ?: "#18211E"
        set(v) = sp.edit().putString(KEY_CUSTOM_KEY, v).apply()

    var currentLanguageId: String
        get() = sp.getString(KEY_LANGUAGE, "si") ?: "si"
        set(v) = sp.edit().putString(KEY_LANGUAGE, v).apply()

    var recentEmojis: List<String>
        get() = (sp.getString(KEY_RECENT_EMOJI, "") ?: "")
            .split(DELIM)
            .filter { it.isNotBlank() }
        set(v) = sp.edit()
            .putString(KEY_RECENT_EMOJI, v.take(MAX_RECENT).joinToString(DELIM))
            .apply()

    fun addRecentEmoji(emoji: String) {
        val list = recentEmojis.toMutableList()
        list.remove(emoji)
        list.add(0, emoji)
        recentEmojis = list
    }

    var clipboardItems: List<String>
        get() = (sp.getString(KEY_CLIPBOARD, "") ?: "")
            .split(DELIM)
            .filter { it.isNotBlank() }
        private set(v) = sp.edit()
            .putString(KEY_CLIPBOARD, v.take(MAX_CLIPBOARD).joinToString(DELIM))
            .apply()

    fun addClipboardItem(value: String) {
        if (!clipboardHistoryEnabled || value.isBlank()) return
        val list = clipboardItems.toMutableList()
        list.remove(value)
        list.add(0, value.take(MAX_CLIPBOARD_TEXT))
        clipboardItems = list
    }

    fun clearClipboardItems() {
        clipboardItems = emptyList()
    }

    var shortcuts: Map<String, String>
        get() = (sp.getString(KEY_SHORTCUTS, "") ?: "").lineSequence()
            .mapNotNull { line ->
                val split = line.indexOf('=')
                if (split <= 0) null else line.substring(0, split) to line.substring(split + 1)
            }.toMap()
        private set(value) = sp.edit().putString(
            KEY_SHORTCUTS,
            value.entries.joinToString("\n") { "${it.key.replace("=", "")}=${it.value.replace("\n", " ")}" }
        ).apply()

    fun saveShortcut(shortcut: String, expansion: String) {
        val key = shortcut.trim()
        if (key.isBlank() || expansion.isBlank()) return
        shortcuts = shortcuts + (key to expansion)
    }

    fun deleteShortcut(shortcut: String) {
        shortcuts = shortcuts - shortcut
    }

    companion object {
        private const val FILE = "shan_x_nova_prefs"
        private const val KEY_HEIGHT = "keyboard_height_scale"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_ANIMATION = "key_animation"
        private const val KEY_POPUP_PREVIEW = "popup_key_preview"
        private const val KEY_REDUCED_MOTION = "reduced_motion"
        private const val KEY_LARGE_KEYS = "large_keys"
        private const val KEY_HIGH_CONTRAST = "high_contrast"
        private const val KEY_TOOLBAR = "toolbar_enabled"
        private const val KEY_CLIPBOARD_HISTORY = "clipboard_history_enabled"
        private const val KEY_LEARNING = "personalized_learning_enabled"
        private const val KEY_OFFLINE = "strict_offline_mode"
        private const val KEY_PROFILE = "active_profile"
        private const val KEY_SHORTCUTS = "shortcuts"
        private const val KEY_THEME = "theme_id"
        private const val KEY_CUSTOM_ACCENT = "custom_accent"
        private const val KEY_CUSTOM_BACKGROUND = "custom_background"
        private const val KEY_CUSTOM_KEY = "custom_key"
        private const val KEY_LANGUAGE = "current_language"
        private const val KEY_RECENT_EMOJI = "recent_emojis"
        private const val DELIM = "|"
        private const val MAX_RECENT = 36
        private const val MAX_CLIPBOARD = 24
        private const val MAX_CLIPBOARD_TEXT = 500
        private const val KEY_CLIPBOARD = "clipboard_items"
    }
}
