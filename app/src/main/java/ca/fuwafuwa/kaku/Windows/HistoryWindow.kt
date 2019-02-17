package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.*
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import ca.fuwafuwa.kaku.DB_JMDICT_NAME
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized
import ca.fuwafuwa.kaku.LangUtils
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Search.JmSearchResult
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Enums.LayoutPosition
import ca.fuwafuwa.kaku.dpToPx

class HistoryWindow(context: Context,
                    windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_history)
{
    private data class PastKanji(
            val kanji: String,
            val results: List<JmSearchResult>)
    {
        var timesSeen: Int = 0
    }

    private val container = window.findViewById<LinearLayout>(R.id.history_window_container)
    private val childKanji = container.findViewById<LinearLayout>(R.id.history_window_kanji)
    private val childResults = container.findViewById<LinearLayout>(R.id.history_window_results)
    private val changeLayoutButton = window.findViewById<TextView>(R.id.history_window_layout_button)

    private val maxShownHistory = 40
    private val normalHeight = dpToPx(context, 45)
    private val pastKanjiView = window.findViewById<LinearLayout>(R.id.past_kanji)!!
    private val pastKanjiScrollView = window.findViewById<HorizontalScrollView>(R.id.past_kanji_scroll_view)!!
    private val pastDictResults = window.findViewById<TextView>(R.id.history_dict_result)!!

    private var pastKanjis = mutableListOf<PastKanji>()
    private var isOnTop = true

    init
    {
        changeLayoutButton.setOnClickListener {
            isOnTop = !isOnTop
            relayoutWindow(if (isOnTop) LayoutPosition.TOP else LayoutPosition.BOTTOM)
        }

        val tv = createTextView(0)
        tv.text = "Lookup History"
        pastKanjiView.addView(tv)
    }

    override fun onTouch(e: MotionEvent?): Boolean
    {
        childResults.layoutParams.height = 0
        childKanji.visibility = View.VISIBLE

        params.height = normalHeight
        windowManager.updateViewLayout(window, params)

        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean
    {
        return false
    }

    override fun onResize(e: MotionEvent?): Boolean
    {
        return false
    }

    override fun show()
    {
        params.width = MATCH_PARENT
        params.height = normalHeight
        super.show()
    }

    /**
     * HistoryWindow does not need to reInit layout as its getDefaultParams() are all relative. Re-initing will cause bugs.
     */
    override fun reInit(options: Window.ReinitOptions)
    {
        options.reinitViewLayout = false
        super.reInit(options)
    }

    fun addResult(squareChar: ISquareChar, results: List<JmSearchResult>)
    {
        if (!squareChar.userTouched)
        {
            return
        }

        val word = results.first().entry.kanji

        if (pastKanjis.any { k -> k.kanji == word })
        {
            pastKanjis.first { k -> k.kanji == word }.timesSeen++
        }
        else
        {
            pastKanjis.add(PastKanji(word, results))
            if (pastKanjiView.childCount < maxShownHistory)
            {
                pastKanjiView.addView(createTextView(pastKanjis.size))
            }
        }

        recalculateViews()
    }

    @Synchronized private fun relayoutWindow(pos: LayoutPosition)
    {
        params.height = normalHeight

        val marginSize = dpToPx(context, 5)

        container.removeAllViews()

        if (pos == LayoutPosition.TOP)
        {
            val childResultsParams = childResults.layoutParams as LinearLayout.LayoutParams
            childResultsParams.setMargins(marginSize, marginSize, marginSize, 0)
            childResultsParams.height = 0
            childResults.layoutParams = childResultsParams

            val childKanjiParams = childKanji.layoutParams as LinearLayout.LayoutParams
            childKanjiParams.setMargins(marginSize, marginSize, marginSize, 0)
            childKanji.layoutParams = childKanjiParams

            changeLayoutButton.text = "▼"

            container.addView(childKanji)
            container.addView(childResults)
            container.gravity = Gravity.TOP

            params.y = 0
        }
        else if (pos == LayoutPosition.BOTTOM)
        {
            val childResultsParams = childResults.layoutParams as LinearLayout.LayoutParams
            childResultsParams.setMargins(marginSize, 0, marginSize, marginSize)
            childResultsParams.height = 0
            childResults.layoutParams = childResultsParams

            val childKanjiParams = childKanji.layoutParams as LinearLayout.LayoutParams
            childKanjiParams.setMargins(marginSize, 0, marginSize, marginSize)
            childKanji.layoutParams = childKanjiParams

            changeLayoutButton.text = "▲"

            container.addView(childResults)
            container.addView(childKanji)
            container.gravity = Gravity.BOTTOM

            params.y = viewHeight - normalHeight
        }

        windowManager.updateViewLayout(window, params)
    }

    private fun recalculateViews()
    {
        pastKanjis.sortByDescending { k -> k.timesSeen }
        pastKanjis = pastKanjis.take(100).toMutableList()

        for (i in 0 until pastKanjis.size)
        {
            if (i >= pastKanjiView.childCount)
            {
                break
            }
            (pastKanjiView.getChildAt(i) as TextView).text = pastKanjis[i].kanji
        }

        pastKanjiScrollView.scrollTo(0, 0)
    }

    private fun createTextView(index: Int) : TextView
    {
        val tv = TextView(context)
        val padding = dpToPx(context, 5)

        tv.setTextColor(Color.BLACK)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.toFloat())

        if (index == 0)
        {
            tv.setPadding(padding * 2, padding, padding, padding)
        }
        else if (index == maxShownHistory - 1)
        {
            tv.setPadding(padding, padding, padding * 2, padding)
        }
        else
        {
            tv.setPadding(padding, padding, padding, padding)
        }

        tv.setOnClickListener { v ->
            childKanji.visibility = View.GONE
            childKanji.requestLayout()
            showEntry(pastKanjiView.indexOfChild(v))
        }

        return tv
    }

    private fun showEntry(index: Int)
    {
        childResults.layoutParams.height = WRAP_CONTENT
        params.height = MATCH_PARENT
        windowManager.updateViewLayout(window, params)

        if (index < pastKanjis.size)
        {
            displayResults(pastKanjis[index].results)
        }
    }

    private fun displayResults(jmResults: List<JmSearchResult>)
    {
        val sb = StringBuilder()

        for ((entry, _) in jmResults)
        {
            sb.append(entry.kanji)

            if (!entry.readings.isEmpty())
            {
                if (DB_JMDICT_NAME == entry.dictionary)
                {
                    sb.append(" (")
                } else
                {
                    sb.append(" ")
                }
                sb.append(entry.readings)
                if (DB_JMDICT_NAME == entry.dictionary) sb.append(")")
            }

            sb.append("\n")
            sb.append(getMeaning(entry))
            sb.append("\n\n")
        }

        if (sb.length > 2)
        {
            sb.setLength(sb.length - 2)
        }

        pastDictResults.setText(sb.toString())
    }

    private fun getMeaning(entry: EntryOptimized): String
    {
        val meanings = entry.meanings.split("\ufffc".toRegex()).toTypedArray()
        val pos = entry.pos.split("\ufffc".toRegex()).toTypedArray()

        val sb = StringBuilder()

        for (i in meanings.indices)
        {
            if (i != 0)
            {
                sb.append(" ")
            }
            sb.append(LangUtils.ConvertIntToCircledNum(i + 1))
            sb.append(" ")
            if (DB_JMDICT_NAME == entry.dictionary && !pos[i].isEmpty())
            {
                sb.append(String.format("(%s) ", pos[i]))
            }
            sb.append(meanings[i])
        }

        return sb.toString()
    }
}