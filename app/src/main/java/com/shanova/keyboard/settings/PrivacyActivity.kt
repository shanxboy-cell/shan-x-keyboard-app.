package com.shanova.keyboard.settings

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView

class PrivacyActivity : Activity() {
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(10, 10, 10))
        }
        content.addView(title("Privacy Center"))
        content.addView(body("SHAN-X-NOVA processes keyboard input through Android's local InputConnection. No typed text, clipboard content, or preferences are uploaded."))
        addSwitch(content, "Strict offline mode", prefs.strictOfflineMode) { prefs.strictOfflineMode = it }
        addSwitch(content, "Personalized learning", prefs.personalizedLearningEnabled) { prefs.personalizedLearningEnabled = it }
        addSwitch(content, "Keep clipboard history locally", prefs.clipboardHistoryEnabled) { prefs.clipboardHistoryEnabled = it }
        content.addView(Button(this).apply {
            text = "Clear clipboard history"
            setOnClickListener { prefs.clearClipboardItems() }
        })
        content.addView(body("Password and sensitive-field learning is not enabled by this app. Clipboard history is bounded and can be disabled or cleared at any time."))
        setContentView(ScrollView(this).apply { addView(content) })
    }

    private fun addSwitch(parent: LinearLayout, label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
        parent.addView(Switch(this).apply {
            text = label
            isChecked = checked
            setTextColor(Color.WHITE)
            setPadding(0, 12, 0, 12)
            setOnCheckedChangeListener { _, value -> onChange(value) }
        })
    }

    private fun title(value: String) = TextView(this).apply {
        text = value
        textSize = 28f
        setTextColor(Color.WHITE)
        gravity = Gravity.START
        setPadding(0, 8, 0, 16)
    }

    private fun body(value: String) = TextView(this).apply {
        text = value
        textSize = 14f
        setTextColor(Color.LTGRAY)
        setPadding(0, 8, 0, 16)
    }
}
