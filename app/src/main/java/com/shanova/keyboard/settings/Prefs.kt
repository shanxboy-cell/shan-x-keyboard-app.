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

    var themeId: String
        get() = sp.getString(KEY_THEME, "emerald") ?: "emerald"
        set(v) = sp.edit().putString(KEY_THEME, v).apply()

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

    companion object {
        private const val FILE = "shanova_prefs"
        private const val KEY_HEIGHT = "keyboard_height_scale"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_THEME = "theme_id"
        private const val KEY_LANGUAGE = "current_language"
        private const val KEY_RECENT_EMOJI = "recent_emojis"
        private const val DELIM = "|"
        private const val MAX_RECENT = 36
    }
}
