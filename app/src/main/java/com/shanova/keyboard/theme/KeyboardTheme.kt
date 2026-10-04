package com.shanova.keyboard.theme

import android.graphics.Color

/**
 * Local-only SHAN-X-NOVA visual theme definition.
 * Colors and presentation values are applied directly by the keyboard view;
 * no theme asset or user data leaves the device.
 */
data class KeyboardTheme(
    val id: String,
    val displayName: String,
    val backgroundColor: Int,
    val backgroundGradientEnd: Int,
    val keyColor: Int,
    val keyPressedColor: Int,
    val specialKeyColor: Int,
    val accentColor: Int,
    val accentDarkColor: Int,
    val primaryTextColor: Int,
    val secondaryTextColor: Int,
    val panelColor: Int,
    val borderColor: Int = 0x22FFFFFF,
    val glowColor: Int = accentColor,
    val transparency: Float = 1f,
    val cornerRadiusDp: Float = 9f,
    val glowIntensity: Float = 0.18f,
    val fontScale: Float = 1f
) {
    companion object {
        private fun theme(
            id: String,
            name: String,
            background: String,
            gradient: String,
            key: String,
            pressed: String,
            special: String,
            accent: String,
            accentDark: String,
            text: String,
            secondary: String,
            panel: String,
            border: String = "#22FFFFFF",
            transparency: Float = 1f,
            radius: Float = 9f,
            glow: Float = 0.18f,
            fontScale: Float = 1f
        ) = KeyboardTheme(
            id = id,
            displayName = name,
            backgroundColor = Color.parseColor(background),
            backgroundGradientEnd = Color.parseColor(gradient),
            keyColor = Color.parseColor(key),
            keyPressedColor = Color.parseColor(pressed),
            specialKeyColor = Color.parseColor(special),
            accentColor = Color.parseColor(accent),
            accentDarkColor = Color.parseColor(accentDark),
            primaryTextColor = Color.parseColor(text),
            secondaryTextColor = Color.parseColor(secondary),
            panelColor = Color.parseColor(panel),
            borderColor = Color.parseColor(border),
            glowColor = Color.parseColor(accent),
            transparency = transparency,
            cornerRadiusDp = radius,
            glowIntensity = glow,
            fontScale = fontScale
        )

        val SHAN_X_NOVA = theme(
            "shan_x_nova", "SHAN-X-NOVA Black", "#070909", "#10231B", "#18211E",
            "#36D28B", "#101816", "#36D28B", "#124D35", "#F3FFF9", "#91B4A6", "#0C1512"
        )
        val MIDNIGHT_GREEN = theme(
            "midnight_green", "Midnight Green", "#06100D", "#0D261E", "#15352A",
            "#51E6A1", "#10251D", "#51E6A1", "#16583B", "#ECFFF5", "#91B8A7", "#0B1C16",
            radius = 11f, glow = 0.24f
        )
        val AMOLED_BLACK = theme(
            "amoled_black", "AMOLED Black", "#000000", "#050505", "#101010",
            "#A7FFCE", "#090909", "#A7FFCE", "#1B5438", "#FFFFFF", "#A0A0A0", "#050505",
            border = "#2FFFFFFF", radius = 8f
        )
        val CARBON = theme(
            "carbon", "Carbon", "#121416", "#22272A", "#2A3034",
            "#B7C2C8", "#202529", "#B7C2C8", "#52606A", "#F2F5F7", "#AAB4B9", "#181C1F",
            border = "#38FFFFFF", glow = 0.08f
        )
        val AURORA = theme(
            "aurora", "Aurora", "#101229", "#1C1740", "#28214A",
            "#B89CFF", "#211A3B", "#B89CFF", "#4B318D", "#FAF8FF", "#B7ACD0", "#181438",
            border = "#35C9B8FF", radius = 12f, glow = 0.3f
        )
        val GLASS = theme(
            "glass", "Glass", "#10161A", "#193039", "#33444A",
            "#8DEBFF", "#26383E", "#8DEBFF", "#205B69", "#F0FCFF", "#A4C5CB", "#1B2930",
            border = "#5CFFFFFF", transparency = .82f, radius = 14f, glow = 0.2f
        )
        val NEON = theme(
            "neon", "Neon", "#050509", "#180D22", "#211127",
            "#FF5CCF", "#170C1D", "#FF5CCF", "#64205A", "#FFF5FC", "#C6A9BD", "#100914",
            border = "#45FF5CCF", radius = 10f, glow = 0.34f
        )
        val LIGHT_PREMIUM = theme(
            "light_premium", "Light Premium", "#F4F8F5", "#DDEEE5", "#FFFFFF",
            "#32A875", "#E7F1EB", "#168A5A", "#B7E4CD", "#173328", "#5E786C", "#EAF4EE",
            border = "#260B3A2A", glow = 0.08f
        )

        // Kept as aliases so existing callers remain source-compatible.
        val EMERALD_NIGHT = SHAN_X_NOVA
        val NEON_CARBON = NEON
        val FOREST_MIST = MIDNIGHT_GREEN

        val ALL = listOf(
            SHAN_X_NOVA, MIDNIGHT_GREEN, AMOLED_BLACK, CARBON,
            AURORA, GLASS, NEON, LIGHT_PREMIUM
        )

        fun custom(accentHex: String, backgroundHex: String, keyHex: String): KeyboardTheme {
            val accent = safeColor(accentHex, Color.parseColor("#36D28B"))
            val background = safeColor(backgroundHex, Color.parseColor("#070909"))
            val key = safeColor(keyHex, Color.parseColor("#18211E"))
            return KeyboardTheme(
                id = "custom",
                displayName = "My Custom Theme",
                backgroundColor = background,
                backgroundGradientEnd = blend(background, Color.BLACK, .35f),
                keyColor = key,
                keyPressedColor = blend(accent, Color.WHITE, .25f),
                specialKeyColor = blend(key, background, .4f),
                accentColor = accent,
                accentDarkColor = blend(accent, Color.BLACK, .55f),
                primaryTextColor = Color.WHITE,
                secondaryTextColor = 0xFFB7C8C0.toInt(),
                panelColor = background,
                borderColor = 0x35FFFFFF,
                glowColor = accent,
                cornerRadiusDp = 10f,
                glowIntensity = .22f
            )
        }

        private fun safeColor(value: String, fallback: Int): Int = try {
            Color.parseColor(value.trim().let { if (it.startsWith("#")) it else "#$it" })
        } catch (_: IllegalArgumentException) {
            fallback
        }

        private fun blend(first: Int, second: Int, amount: Float): Int {
            val t = amount.coerceIn(0f, 1f)
            return Color.rgb(
                (Color.red(first) * (1 - t) + Color.red(second) * t).toInt(),
                (Color.green(first) * (1 - t) + Color.green(second) * t).toInt(),
                (Color.blue(first) * (1 - t) + Color.blue(second) * t).toInt()
            )
        }

        fun byId(id: String?): KeyboardTheme = ALL.firstOrNull { it.id == id } ?: SHAN_X_NOVA
    }
}
