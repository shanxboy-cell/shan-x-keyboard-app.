package com.shanova.keyboard.settings

import com.shanova.keyboard.theme.KeyboardTheme

data class KeyboardProfile(
    val id: String,
    val name: String,
    val themeId: String,
    val languageId: String? = null,
    val heightScale: Float? = null,
    val animations: Boolean? = null,
    val largeKeys: Boolean? = null,
    val toolbar: Boolean? = null
) {
    fun apply(prefs: Prefs) {
        prefs.profileId = id
        prefs.themeId = themeId
        languageId?.let { prefs.currentLanguageId = it }
        heightScale?.let { prefs.keyboardHeightScale = it }
        animations?.let { prefs.keyAnimationEnabled = it }
        largeKeys?.let { prefs.largeKeysEnabled = it }
        toolbar?.let { prefs.toolbarEnabled = it }
    }

    companion object {
        val ALL = listOf(
            KeyboardProfile("default", "SHAN-X-NOVA Default", KeyboardTheme.SHAN_X_NOVA.id, toolbar = true),
            KeyboardProfile("minimal", "Minimal", KeyboardTheme.LIGHT_PREMIUM.id, animations = false, toolbar = false),
            KeyboardProfile("amoled", "AMOLED", KeyboardTheme.AMOLED_BLACK.id),
            KeyboardProfile("gaming", "Gaming", KeyboardTheme.NEON.id, animations = true, toolbar = false),
            KeyboardProfile("coding", "Coding", KeyboardTheme.CARBON.id, toolbar = true),
            KeyboardProfile("sinhala", "Sinhala", KeyboardTheme.MIDNIGHT_GREEN.id, languageId = "si"),
            KeyboardProfile("english", "English", KeyboardTheme.SHAN_X_NOVA.id, languageId = "en"),
            KeyboardProfile("tamil", "Tamil", KeyboardTheme.AURORA.id, languageId = "ta"),
            KeyboardProfile("writing", "Writing", KeyboardTheme.LIGHT_PREMIUM.id, heightScale = 1.2f, largeKeys = true),
            KeyboardProfile("custom", "Custom", KeyboardTheme.SHAN_X_NOVA.id)
        )

        fun byId(id: String?): KeyboardProfile = ALL.firstOrNull { it.id == id } ?: ALL.first()
    }
}
