package com.shanova.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.shanova.keyboard.keyboard.model.Key
import com.shanova.keyboard.keyboard.model.KeyRow
import com.shanova.keyboard.keyboard.model.KeyType
import com.shanova.keyboard.keyboard.model.KeyboardLayout
import com.shanova.keyboard.theme.KeyboardTheme

/**
 * Renders a [KeyboardLayout] as weighted rows of rounded keys with
 * press animations, key repeat (backspace) and sound/haptic feedback.
 */
@SuppressLint("ClickableViewAccessibility")
class KeyboardView(context: Context) : LinearLayout(context) {

    interface OnKeyListener {
        fun onKey(key: Key)
    }

    var onKeyListener: OnKeyListener? = null
    var theme: KeyboardTheme = KeyboardTheme.EMERALD_NIGHT
    var keySoundEnabled = false
    var hapticEnabled = true
    var keyAnimationEnabled = true
    var reducedMotion = false
    var largeKeysEnabled = false
    var highContrastEnabled = false
    var heightScale = 1.0f

    private val handler = Handler(Looper.getMainLooper())

    private val audioManager: android.media.AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager

    init {
        orientation = VERTICAL
    }

    fun showLayout(
        layout: KeyboardLayout,
        shiftActive: Boolean,
        spaceLabel: String? = null
    ) {
        removeAllViews()
        val sidePad = dp(4)
        setPadding(sidePad, dp(6), sidePad, dp(8))
        layout.rows.forEach { row -> addView(buildRow(row, shiftActive, spaceLabel)) }
    }

    private fun buildRow(row: KeyRow, shiftActive: Boolean, spaceLabel: String?): View {
        val rowLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (BASE_KEY_HEIGHT_DP * heightScale * if (largeKeysEnabled) 1.12f else 1f)
                    .toInt().let { dp(it) }
            ).apply { topMargin = dp(4) }
        }

        if (row.marginWeight > 0f) {
            rowLayout.addView(spacer(row.marginWeight))
        }
        row.keys.forEach { key ->
            rowLayout.addView(buildKey(key, shiftActive, spaceLabel))
        }
        if (row.marginWeight > 0f) {
            rowLayout.addView(spacer(row.marginWeight))
        }
        return rowLayout
    }

    private fun spacer(weight: Float): View =
        View(context).apply {
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
        }

    private fun buildKey(key: Key, shiftActive: Boolean, spaceLabel: String?): View {
        val label = if (key.type == KeyType.SPACE && spaceLabel != null) spaceLabel
            else key.label

        val keyView = TextView(context).apply {
            text = label
            gravity = Gravity.CENTER
            setTextColor(
                if (key.type == KeyType.SPACE) theme.secondaryTextColor
                else theme.primaryTextColor
            )
            setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                when (key.type) {
                    KeyType.CHARACTER -> if (label.length > 2) 16f else 21f
                    KeyType.SPACE -> 13f
                    else -> 17f
                } * theme.fontScale * if (largeKeysEnabled) 1.06f else 1f
            )
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, key.widthWeight)
                .apply {
                    val m = dp(3)
                    setMargins(m, 0, m, 0)
                }
            background = keyBackground(key, shiftActive, pressed = false)
            isClickable = true
        }

        keyView.setOnTouchListener { v, event -> handleTouch(v as TextView, key, shiftActive, event) }
        return keyView
    }

    private fun handleTouch(v: TextView, key: Key, shiftActive: Boolean, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pressVisual(v, key, shiftActive, true)
                feedback()
                if (key.type == KeyType.BACKSPACE) startRepeat(key)
                return true
            }
            MotionEvent.ACTION_UP -> {
                pressVisual(v, key, shiftActive, false)
                stopRepeat()
                if (isInside(v, event)) onKeyListener?.onKey(key)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                pressVisual(v, key, shiftActive, false)
                stopRepeat()
                return true
            }
        }
        return false
    }

    private fun isInside(v: View, e: MotionEvent): Boolean =
        e.x >= 0 && e.x <= v.width && e.y >= -v.height * 0.5f && e.y <= v.height * 1.5f

    /* -------------------- visuals -------------------- */

    private fun keyBackground(key: Key, shiftActive: Boolean, pressed: Boolean): GradientDrawable {
        val fill = when {
            pressed -> theme.keyPressedColor
            key.type == KeyType.SHIFT && shiftActive -> theme.accentDarkColor
            key.type == KeyType.ENTER -> theme.accentDarkColor
            key.type != KeyType.CHARACTER && key.type != KeyType.SPACE -> theme.specialKeyColor
            else -> theme.keyColor
        }
        val stroke = if (key.type == KeyType.ENTER || (key.type == KeyType.SHIFT && shiftActive))
            theme.accentColor else 0x22FFFFFF
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(theme.cornerRadiusDp.toInt()).toFloat()
            setColor(withAlpha(fill, theme.transparency))
            setStroke(dp(if (highContrastEnabled) 2 else 1), if (highContrastEnabled) theme.accentColor else stroke)
        }
    }

    private fun pressVisual(v: TextView, key: Key, shiftActive: Boolean, pressed: Boolean) {
        v.background = keyBackground(key, shiftActive, pressed)
        v.animate().cancel()
        if (keyAnimationEnabled && !reducedMotion) {
            v.animate()
                .scaleX(if (pressed) 1.045f else 1f)
                .scaleY(if (pressed) 1.045f else 1f)
                .setDuration(if (pressed) 55 else 100)
                .start()
        } else {
            v.scaleX = 1f
            v.scaleY = 1f
        }
        if (key.type == KeyType.CHARACTER) {
            v.setTextColor(if (pressed) 0xFF000000.toInt() else theme.primaryTextColor)
        }
    }

    /* -------------------- feedback & repeat -------------------- */

    private fun feedback() {
        if (hapticEnabled) {
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        if (keySoundEnabled) {
            audioManager.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    private var repeatRunnable: Runnable? = null

    private fun startRepeat(key: Key) {
        stopRepeat()
        val runnable = object : Runnable {
            override fun run() {
                onKeyListener?.onKey(key)
                feedback()
                handler.postDelayed(this, REPEAT_INTERVAL_MS)
            }
        }
        repeatRunnable = runnable
        handler.postDelayed(runnable, REPEAT_DELAY_MS)
    }

    private fun stopRepeat() {
        repeatRunnable?.let { handler.removeCallbacks(it) }
        repeatRunnable = null
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
        ).toInt()

    private fun withAlpha(color: Int, opacity: Float): Int =
        android.graphics.Color.argb(
            (android.graphics.Color.alpha(color) * opacity.coerceIn(0.2f, 1f)).toInt(),
            android.graphics.Color.red(color),
            android.graphics.Color.green(color),
            android.graphics.Color.blue(color)
        )

    companion object {
        private const val BASE_KEY_HEIGHT_DP = 52f
        private const val REPEAT_DELAY_MS = 400L
        private const val REPEAT_INTERVAL_MS = 55L
    }
}
