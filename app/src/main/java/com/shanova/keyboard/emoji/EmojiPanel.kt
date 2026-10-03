package com.shanova.keyboard.emoji

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.shanova.keyboard.keyboard.KeyboardView
import com.shanova.keyboard.keyboard.languages.EnglishLanguage
import com.shanova.keyboard.keyboard.model.Key
import com.shanova.keyboard.keyboard.model.KeyType
import com.shanova.keyboard.theme.KeyboardTheme

/**
 * Emoji panel: recently used, category tabs, live search and insertion.
 * Renders with the platform emoji font — no bundled artwork.
 *
 * Search uses an embedded letter keyboard (IME windows are not focusable,
 * so a plain EditText cannot receive input reliably).
 */
@SuppressLint("ClickableViewAccessibility")
class EmojiPanel(
    context: Context,
    private val theme: KeyboardTheme,
    private val recents: () -> List<String>,
    private val onEmojiPicked: (String) -> Unit,
    private val onBackspace: () -> Unit,
    private val onEnter: () -> Unit,
    private val onClose: () -> Unit
) : LinearLayout(context) {

    private val grid: GridView
    private val adapter = EmojiAdapter()
    private val tabRow: LinearLayout
    private val queryView: TextView
    private val bottomArea: FrameLayout
    private val searchKeyboard: KeyboardView

    private var searchMode = false
    private val query = StringBuilder()

    init {
        orientation = VERTICAL
        setPadding(dp(6), dp(6), dp(6), dp(6))
        setBackgroundColor(theme.backgroundColor)

        // ---- search bar ----
        val searchBar = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                cornerRadius = dp(10).toFloat()
                setColor(theme.keyColor)
                setStroke(dp(1), 0x22FFFFFF)
            }
            layoutParams = LayoutParams(MATCH_PARENT, dp(38)).apply { bottomMargin = dp(6) }
            setPadding(dp(12), 0, dp(12), 0)
            setOnClickListener { toggleSearchMode() }
        }
        searchBar.addView(TextView(context).apply {
            text = "🔍"; textSize = 14f
        })
        queryView = TextView(context).apply {
            text = "Search emoji"
            setTextColor(theme.secondaryTextColor)
            textSize = 14f
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                .apply { marginStart = dp(8) }
        }
        searchBar.addView(queryView)
        addView(searchBar)

        // ---- category tabs ----
        tabRow = LinearLayout(context).apply { orientation = HORIZONTAL }
        tabRow.addView(buildTab(-1, "🕘"))
        EmojiData.categories.forEachIndexed { i, cat -> tabRow.addView(buildTab(i, cat.icon)) }
        addView(HorizontalScrollView(context).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LayoutParams(MATCH_PARENT, dp(42))
            addView(tabRow)
        })

        // ---- emoji grid ----
        grid = GridView(context).apply {
            numColumns = 8
            verticalSpacing = dp(2)
            horizontalSpacing = dp(2)
            stretchMode = GridView.STRETCH_COLUMN_WIDTH
            layoutParams = LayoutParams(MATCH_PARENT, 0, 1f)
            setAdapter(this@EmojiPanel.adapter)
            setOnItemClickListener { _, _, pos, _ ->
                this@EmojiPanel.adapter.getEmoji(pos)?.let(onEmojiPicked)
            }
        }
        addView(grid)

        // ---- bottom area: normal bar or embedded search keyboard ----
        bottomArea = FrameLayout(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .apply { topMargin = dp(6) }
        }
        searchKeyboard = KeyboardView(context).apply {
            this.theme = this@EmojiPanel.theme
            showLayout(EnglishLanguage.lettersNormal, shiftActive = false, spaceLabel = " ")
            onKeyListener = object : KeyboardView.OnKeyListener {
                override fun onKey(key: Key) = handleSearchKey(key)
            }
        }
        showNormalBottomBar()
        addView(bottomArea)

        showCategory(if (recents().isNotEmpty()) -1 else 0)
    }

    /* -------------------- search mode -------------------- */

    private fun toggleSearchMode() {
        searchMode = !searchMode
        if (searchMode) {
            bottomArea.removeAllViews()
            bottomArea.addView(searchKeyboard)
        } else {
            showNormalBottomBar()
        }
    }

    private fun handleSearchKey(key: Key) {
        when (key.type) {
            KeyType.CHARACTER -> query.append(key.output)
            KeyType.SPACE -> query.append(' ')
            KeyType.BACKSPACE -> if (query.isNotEmpty()) query.deleteCharAt(query.length - 1)
            KeyType.ENTER, KeyType.ABC, KeyType.EMOJI_BACK -> {
                if (searchMode) toggleSearchMode()
                return
            }
            else -> return
        }
        refreshQuery()
    }

    private fun refreshQuery() {
        val q = query.toString()
        if (q.isEmpty()) {
            queryView.text = "Search emoji"
            queryView.setTextColor(theme.secondaryTextColor)
            showCategory(if (recents().isNotEmpty()) -1 else 0)
        } else {
            queryView.text = q
            queryView.setTextColor(theme.primaryTextColor)
            setTabHighlight(-2)
            adapter.submit(EmojiData.search(q).map { it.first })
        }
    }

    /* -------------------- tabs & grid -------------------- */

    private fun buildTab(index: Int, icon: String): View =
        TextView(context).apply {
            text = icon
            textSize = 18f
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(dp(44), ViewGroup.LayoutParams.MATCH_PARENT)
                .apply { marginEnd = dp(4) }
            setOnClickListener {
                query.clear()
                queryView.text = "Search emoji"
                queryView.setTextColor(theme.secondaryTextColor)
                showCategory(index)
            }
        }

    private fun showCategory(index: Int) {
        setTabHighlight(index)
        val items = if (index == -1) recents()
            else EmojiData.categories[index].emojis.map { it.first }
        if (index == -1 && items.isEmpty()) {
            showCategory(0)
            return
        }
        adapter.submit(items)
    }

    private fun setTabHighlight(activeIndex: Int) {
        for (i in 0 until tabRow.childCount) {
            val tab = tabRow.getChildAt(i) as TextView
            tab.background = GradientDrawable().apply {
                cornerRadius = dp(8).toFloat()
                setColor(if (i - 1 == activeIndex) theme.accentDarkColor else 0)
            }
        }
    }

    /** Refresh the recents tab contents (call after picking an emoji). */
    fun refreshRecents() {
        if (!searchMode && query.isEmpty()) showCategory(if (recents().isNotEmpty()) -1 else 0)
    }

    /* -------------------- bottom bar -------------------- */

    private fun showNormalBottomBar() {
        bottomArea.removeAllViews()
        val bar = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(MATCH_PARENT, dp(46))
        }
        bar.addView(bottomButton("ABC", 1.3f) { onClose() })
        bar.addView(View(context).apply {
            layoutParams = LayoutParams(0, 1, 3.4f)
        })
        bar.addView(bottomButton("⌫", 1.3f) { onBackspace() })
        bar.addView(bottomButton("⏎", 1.3f, accent = true) { onEnter() })
        bottomArea.addView(bar)
    }

    private fun bottomButton(
        label: String,
        weight: Float,
        accent: Boolean = false,
        onClick: () -> Unit
    ): View = TextView(context).apply {
        text = label
        textSize = 16f
        gravity = Gravity.CENTER
        setTextColor(theme.primaryTextColor)
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        background = GradientDrawable().apply {
            cornerRadius = dp(9).toFloat()
            setColor(if (accent) theme.accentDarkColor else theme.specialKeyColor)
            setStroke(dp(1), if (accent) theme.accentColor else 0x22FFFFFF)
        }
        layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
            .apply {
                val m = dp(3)
                setMargins(m, 0, m, 0)
            }
        setOnClickListener { onClick() }
    }

    /* -------------------- adapter -------------------- */

    private inner class EmojiAdapter : BaseAdapter() {
        private var items: List<String> = emptyList()

        fun submit(newItems: List<String>) {
            items = newItems
            notifyDataSetChanged()
        }

        fun getEmoji(position: Int): String? = items.getOrNull(position)

        override fun getCount() = items.size
        override fun getItem(position: Int) = items[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val tv = (convertView as? TextView) ?: TextView(context).apply {
                textSize = 22f
                gravity = Gravity.CENTER
                layoutParams = ViewGroup.LayoutParams(dp(44), dp(44))
            }
            tv.text = items[position]
            return tv
        }
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
        ).toInt()

    companion object {
        private const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    }
}
