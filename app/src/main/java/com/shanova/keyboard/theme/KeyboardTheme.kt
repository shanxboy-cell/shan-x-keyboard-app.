package com.shanova.keyboard.theme

import android.graphics.Color

/**
 * SHAN-X-NOVA visual theme. All themes share the brand identity:
 * black background, green accent, white text.
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
    val panelColor: Int
) {
    companion object {
        val EMERALD_NIGHT = KeyboardTheme(
            id = "emerald",
            displayName = "Emerald Night",
            backgroundColor = Color.parseColor("#0A0A0A"),
            backgroundGradientEnd = Color.parseColor("#0E1A12"),
            keyColor = Color.parseColor("#1E1E22"),
            keyPressedColor = Color.parseColor("#00C853"),
            specialKeyColor = Color.parseColor("#17171B"),
            accentColor = Color.parseColor("#00C853"),
            accentDarkColor = Color.parseColor("#0A3D22"),
            primaryTextColor = Color.parseColor("#F5F5F7"),
            secondaryTextColor = Color.parseColor("#9E9EA6"),
            panelColor = Color.parseColor("#101012")
        )

        val NEON_CARBON = KeyboardTheme(
            id = "neon",
            displayName = "Neon Carbon",
            backgroundColor = Color.parseColor("#000000"),
            backgroundGradientEnd = Color.parseColor("#00160A"),
            keyColor = Color.parseColor("#131316"),
            keyPressedColor = Color.parseColor("#00E676"),
            specialKeyColor = Color.parseColor("#0E0E11"),
            accentColor = Color.parseColor("#00E676"),
            accentDarkColor = Color.parseColor("#064E2B"),
            primaryTextColor = Color.parseColor("#FFFFFF"),
            secondaryTextColor = Color.parseColor("#8F8F98"),
            panelColor = Color.parseColor("#0A0A0C")
        )

        val FOREST_MIST = KeyboardTheme(
            id = "forest",
            displayName = "Forest Mist",
            backgroundColor = Color.parseColor("#0B1410"),
            backgroundGradientEnd = Color.parseColor("#0F211A"),
            keyColor = Color.parseColor("#1A2B22"),
            keyPressedColor = Color.parseColor("#34D399"),
            specialKeyColor = Color.parseColor("#142119"),
            accentColor = Color.parseColor("#34D399"),
            accentDarkColor = Color.parseColor("#14532D"),
            primaryTextColor = Color.parseColor("#ECFDF5"),
            secondaryTextColor = Color.parseColor("#8FA89C"),
            panelColor = Color.parseColor("#0E1B15")
        )

        val ALL = listOf(EMERALD_NIGHT, NEON_CARBON, FOREST_MIST)

        fun byId(id: String?): KeyboardTheme = ALL.firstOrNull { it.id == id } ?: EMERALD_NIGHT
    }
}
