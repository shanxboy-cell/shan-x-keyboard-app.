package com.shanova.keyboard.keyboard

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.shanova.keyboard.theme.KeyboardTheme

/** Lightweight, offline toolbar shown above the keyboard when enabled. */
class KeyboardToolbar(
    context: Context,
    private var theme: KeyboardTheme,
    private val onEmoji: () -> Unit,
    private val onClipboard: () -> Unit,
    private val onEdit: () -> Unit,
    private val onSymbols: () -> Unit,
    private val onThemes: () -> Unit,
    private val onLanguage: () -> Unit,
    private val onSettings: () -> Unit
) : HorizontalScrollView(context) {

    private val row = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    init {
        isHorizontalScrollBarEnabled = false
        setPadding(dp(4), dp(2), dp(4), dp(2))
        addView(row)
        rebuild()
    }

    fun applyTheme(newTheme: KeyboardTheme) {
        theme = newTheme
        rebuild()
    }

    private fun rebuild() {
        row.removeAllViews()
        setBackgroundColor(theme.panelColor)
        listOf(
            "🔍" to { },
            "😀" to onEmoji,
            "📋" to onClipboard,
            "✎" to onEdit,
            "∑" to onSymbols,
            "🎨" to onThemes,
            "🌐" to onLanguage,
            "🎙" to { },
            "⚙" to onSettings
        ).forEach { (label, action) ->
            row.addView(TextView(context).apply {
                text = label
                textSize = 18f
                gravity = Gravity.CENTER
                contentDescription = label
                background = GradientDrawable().apply {
                    cornerRadius = dp(7).toFloat()
                    setColor(theme.keyColor)
                    setStroke(dp(1), theme.borderColor)
                }
                layoutParams = LinearLayout.LayoutParams(dp(42), dp(32)).apply {
                    setMargins(dp(2), 0, dp(2), 0)
                }
                setOnClickListener { action() }
            })
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    }
}
