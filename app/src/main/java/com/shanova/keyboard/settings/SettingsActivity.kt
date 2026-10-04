package com.shanova.keyboard.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.text.InputType
import com.shanova.keyboard.R
import com.shanova.keyboard.theme.KeyboardTheme

/**
 * SHAN-X-NOVA setup + settings screen.
 *
 * Guides the user through enabling the IME and selecting it,
 * then exposes height / sound / haptic / theme preferences.
 */
class SettingsActivity : Activity() {

    private lateinit var prefs: Prefs

    private lateinit var statusEnable: TextView
    private lateinit var statusActive: TextView
    private lateinit var setupDone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        setContentView(R.layout.activity_settings)

        statusEnable = findViewById(R.id.statusEnable)
        statusActive = findViewById(R.id.statusActive)
        setupDone = findViewById(R.id.setupDone)

        findViewById<Button>(R.id.btnEnable).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        findViewById<Button>(R.id.btnSwitch).setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        setupHeight()
        setupToggles()
        setupThemes()
        setupDashboardActions()
    }

    override fun onResume() {
        super.onResume()
        refreshSetupStatus()
    }

    /* -------------------- setup status -------------------- */

    private fun refreshSetupStatus() {
        val enabled = isImeEnabled()
        val active = isImeActive()

        statusEnable.text = getString(
            if (enabled) R.string.status_enabled else R.string.status_not_enabled
        )
        statusEnable.setTextColor(if (enabled) ACCENT else GREY)

        statusActive.text = getString(
            if (active) R.string.status_active else R.string.status_not_active
        )
        statusActive.setTextColor(if (active) ACCENT else GREY)

        setupDone.visibility = if (enabled && active) TextView.VISIBLE else TextView.GONE
    }

    private fun isImeEnabled(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_INPUT_METHODS
        ) ?: return false
        return enabled.contains(packageName)
    }

    private fun isImeActive(): Boolean {
        val current = Settings.Secure.getString(
            contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD
        ) ?: return false
        return current.startsWith(packageName)
    }

    /* -------------------- preferences -------------------- */

    private fun setupHeight() {
        val seek = findViewById<SeekBar>(R.id.seekHeight)
        val label = findViewById<TextView>(R.id.heightLabel)
        seek.progress = scaleToProgress(prefs.keyboardHeightScale)
        label.text = heightLabel(prefs.keyboardHeightScale)
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
                val scale = progressToScale(progress)
                prefs.keyboardHeightScale = scale
                label.text = heightLabel(scale)
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
    }

    private fun setupToggles() {
        findViewById<Switch>(R.id.switchSound).apply {
            isChecked = prefs.keySoundEnabled
            setOnCheckedChangeListener { _, checked -> prefs.keySoundEnabled = checked }
        }
        findViewById<Switch>(R.id.switchHaptic).apply {
            isChecked = prefs.hapticEnabled
            setOnCheckedChangeListener { _, checked -> prefs.hapticEnabled = checked }
        }
        findViewById<Switch>(R.id.switchToolbar).apply {
            isChecked = prefs.toolbarEnabled
            setOnCheckedChangeListener { _, checked -> prefs.toolbarEnabled = checked }
        }
        findViewById<Switch>(R.id.switchClipboardHistory).apply {
            isChecked = prefs.clipboardHistoryEnabled
            setOnCheckedChangeListener { _, checked -> prefs.clipboardHistoryEnabled = checked }
        }
        findViewById<Switch>(R.id.switchAnimation).apply {
            isChecked = prefs.keyAnimationEnabled
            setOnCheckedChangeListener { _, checked -> prefs.keyAnimationEnabled = checked }
        }
        findViewById<Switch>(R.id.switchLargeKeys).apply {
            isChecked = prefs.largeKeysEnabled
            setOnCheckedChangeListener { _, checked -> prefs.largeKeysEnabled = checked }
        }
        findViewById<Switch>(R.id.switchReducedMotion).apply {
            isChecked = prefs.reducedMotion
            setOnCheckedChangeListener { _, checked -> prefs.reducedMotion = checked }
        }
        findViewById<Switch>(R.id.switchHighContrast).apply {
            isChecked = prefs.highContrastEnabled
            setOnCheckedChangeListener { _, checked -> prefs.highContrastEnabled = checked }
        }
    }

    private fun setupThemes() {
        val group = findViewById<RadioGroup>(R.id.themeGroup)
        val selectedId = when (KeyboardTheme.byId(prefs.themeId).id) {
            KeyboardTheme.MIDNIGHT_GREEN.id -> R.id.themeMidnight
            KeyboardTheme.AMOLED_BLACK.id -> R.id.themeAmoled
            KeyboardTheme.CARBON.id -> R.id.themeCarbon
            KeyboardTheme.AURORA.id -> R.id.themeAurora
            KeyboardTheme.GLASS.id -> R.id.themeGlass
            KeyboardTheme.NEON.id -> R.id.themeNeon
            KeyboardTheme.LIGHT_PREMIUM.id -> R.id.themeLight
            else -> R.id.themeNova
        }
        group.check(selectedId)
        group.setOnCheckedChangeListener { _, checkedId ->
            prefs.themeId = when (checkedId) {
                R.id.themeMidnight -> KeyboardTheme.MIDNIGHT_GREEN.id
                R.id.themeAmoled -> KeyboardTheme.AMOLED_BLACK.id
                R.id.themeCarbon -> KeyboardTheme.CARBON.id
                R.id.themeAurora -> KeyboardTheme.AURORA.id
                R.id.themeGlass -> KeyboardTheme.GLASS.id
                R.id.themeNeon -> KeyboardTheme.NEON_CARBON.id
                R.id.themeLight -> KeyboardTheme.LIGHT_PREMIUM.id
                else -> KeyboardTheme.SHAN_X_NOVA.id
            }
        }
        findViewById<Button>(R.id.btnCustomTheme).setOnClickListener { showCustomThemeDialog() }
    }

    private fun setupDashboardActions() {
        findViewById<Button>(R.id.btnShortcuts).setOnClickListener {
            startActivity(Intent(this, ShortcutActivity::class.java))
        }
        findViewById<Button>(R.id.btnPrivacyCenter).setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }
        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        val scroll = findViewById<ScrollView>(R.id.settingsScroll)
        findViewById<EditText>(R.id.settingsSearch).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase().orEmpty()
                val targetId = when {
                    "theme" in query || "color" in query -> R.id.themeGroup
                    "haptic" in query || "vibration" in query -> R.id.switchHaptic
                    "clipboard" in query -> R.id.switchClipboardHistory
                    "toolbar" in query -> R.id.switchToolbar
                    "animation" in query || "motion" in query -> R.id.switchAnimation
                    "sound" in query -> R.id.switchSound
                    "height" in query || "size" in query -> R.id.seekHeight
                    "privacy" in query || "offline" in query -> R.id.btnPrivacyCenter
                    "shortcut" in query || "dictionary" in query -> R.id.btnShortcuts
                    "custom" in query -> R.id.btnCustomTheme
                    else -> null
                }
                targetId?.let { id ->
                    findViewById<android.view.View>(id)?.post {
                        scroll.smoothScrollTo(0, findViewById<android.view.View>(id).top)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun showCustomThemeDialog() {
        val fields = listOf(
            EditText(this).apply { hint = "Accent hex  (#36D28B)"; setText(prefs.customAccent) },
            EditText(this).apply { hint = "Background hex  (#070909)"; setText(prefs.customBackground) },
            EditText(this).apply { hint = "Key hex  (#18211E)"; setText(prefs.customKey) }
        )
        fields.forEach { it.inputType = InputType.TYPE_CLASS_TEXT; it.selectAll() }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 8, 40, 0)
            fields.forEach { addView(it) }
        }
        AlertDialog.Builder(this)
            .setTitle("Create custom theme")
            .setMessage("Choose three local colors using #RRGGBB values.")
            .setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Apply") { _, _ ->
                prefs.customAccent = fields[0].text.toString()
                prefs.customBackground = fields[1].text.toString()
                prefs.customKey = fields[2].text.toString()
                prefs.themeId = "custom"
            }
            .show()
    }

    /* -------------------- height mapping -------------------- */

    private fun scaleToProgress(scale: Float): Int =
        ((scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE) * 100).toInt().coerceIn(0, 100)

    private fun progressToScale(progress: Int): Float =
        MIN_SCALE + (MAX_SCALE - MIN_SCALE) * progress / 100f

    private fun heightLabel(scale: Float): String =
        getString(R.string.settings_height) + "  ·  " +
            String.format("%.0f%%", scale * 100)

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MAX_SCALE = 1.35f
        private const val ACCENT = 0xFF00C853.toInt()
        private const val GREY = 0xFF9E9EA6.toInt()
    }
}
