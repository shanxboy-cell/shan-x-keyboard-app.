package com.shanova.keyboard.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
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
    }

    private fun setupThemes() {
        val group = findViewById<RadioGroup>(R.id.themeGroup)
        when (KeyboardTheme.byId(prefs.themeId).id) {
            KeyboardTheme.NEON_CARBON.id -> group.check(R.id.themeNeon)
            KeyboardTheme.FOREST_MIST.id -> group.check(R.id.themeForest)
            else -> group.check(R.id.themeEmerald)
        }
        group.setOnCheckedChangeListener { _, checkedId ->
            prefs.themeId = when (checkedId) {
                R.id.themeNeon -> KeyboardTheme.NEON_CARBON.id
                R.id.themeForest -> KeyboardTheme.FOREST_MIST.id
                else -> KeyboardTheme.EMERALD_NIGHT.id
            }
        }
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
