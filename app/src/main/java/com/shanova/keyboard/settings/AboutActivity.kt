package com.shanova.keyboard.settings

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class AboutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 32, 28, 28)
            setBackgroundColor(Color.rgb(7, 9, 9))
        }
        content.addView(TextView(this).apply {
            text = "SHAN-X-NOVA"
            textSize = 34f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })
        content.addView(TextView(this).apply {
            text = "Keyboard\n\nType Beyond Ordinary."
            textSize = 18f
            setTextColor(Color.rgb(54, 210, 139))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 28)
        })
        content.addView(info("Developer", "SHAN"))
        content.addView(info("Version", "1.0 · Android IME"))
        content.addView(info("Privacy", "Offline-first. No internet permission. Typed content is handled locally through Android InputConnection."))
        content.addView(info("Included", "Sinhala · English · Tamil · Emoji · Premium themes · Custom themes · Toolbar · Local clipboard"))
        setContentView(ScrollView(this).apply { addView(content) })
    }

    private fun info(label: String, value: String) = TextView(this).apply {
        text = "$label\n$value"
        textSize = 15f
        setTextColor(Color.LTGRAY)
        setPadding(0, 12, 0, 12)
    }
}
