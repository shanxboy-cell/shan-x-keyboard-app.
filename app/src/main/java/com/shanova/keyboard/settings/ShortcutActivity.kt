package com.shanova.keyboard.settings

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class ShortcutActivity : Activity() {
    private lateinit var prefs: Prefs
    private lateinit var list: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(10, 10, 10))
        }
        root.addView(TextView(this).apply {
            text = "Shortcut Manager"
            textSize = 26f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, 16)
        })
        val shortcut = EditText(this).apply { hint = "Shortcut, e.g. gm"; setSingleLine(true) }
        val expansion = EditText(this).apply { hint = "Expansion, e.g. Good morning"; setSingleLine(true) }
        root.addView(shortcut)
        root.addView(expansion)
        root.addView(Button(this).apply {
            text = "Add shortcut"
            setOnClickListener {
                prefs.saveShortcut(shortcut.text.toString(), expansion.text.toString())
                shortcut.text.clear(); expansion.text.clear(); render()
            }
        })
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(ScrollView(this).apply {
            addView(list)
            layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
        })
        setContentView(root)
        render()
    }

    private fun render() {
        list.removeAllViews()
        if (prefs.shortcuts.isEmpty()) {
            list.addView(TextView(this).apply {
                text = "No shortcuts configured. Expansions are only applied after you explicitly add them."
                setTextColor(Color.LTGRAY)
                setPadding(0, 18, 0, 18)
            })
            return
        }
        prefs.shortcuts.toSortedMap().forEach { (key, value) ->
            list.addView(TextView(this).apply {
                text = "$key  →  $value\nTap to delete"
                textSize = 15f
                setTextColor(Color.WHITE)
                gravity = Gravity.START
                setPadding(8, 16, 8, 16)
                setOnClickListener { prefs.deleteShortcut(key); render() }
            })
        }
    }
}
