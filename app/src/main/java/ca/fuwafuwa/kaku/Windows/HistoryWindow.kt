package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.*
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import ca.fuwafuwa.kaku.DB_JMDICT_NAME
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized
import ca.fuwafuwa.kaku.LangUtils
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Search.JmSearchResult
import ca.fuwafuwa.kaku.Search.SearchInfo
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.dpToPx

class HistoryWindow(context: Context,
                    windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_history)
{
    private data class PastKanji(
            val word: String,
            val results: List<JmSearchResult>)
    {
        var timesSeen: Int = 0
    }

    private val pastKanjiView = window.findViewById<LinearLayout>(R.id.past_kanji)!!
    private val pastKanjiScrollView = window.findViewById<HorizontalScrollView>(R.id.past_kanji_scroll_view)!!
    //private val pastDictResults = window.findViewById<TextSwitcher>(R.id.history_dict_result)!!
    private val pastDictResults = window.findViewById<TextView>(R.id.history_dict_result)!!
    private val pastKanjis = mutableListOf<PastKanji>()

    private val normalHeight = dpToPx(context, 35)

    init
    {

    }

    override fun onTouch(e: MotionEvent?): Boolean
    {
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

    fun addResult(results: List<JmSearchResult>)
    {
        val word = results.first().entry.kanji

        if (pastKanjis.any { k -> k.word == word })
        {
            pastKanjis.first { k -> k.word == word }.timesSeen++
        }
        else {
            pastKanjis.add(PastKanji(word, results))
            pastKanjiView.addView(getTextView(word))
        }

        recalculateViews()
    }

    private fun recalculateViews()
    {
        pastKanjis.sortByDescending { k -> k.timesSeen }

        for (i in 0 until pastKanjiView.childCount){
            (pastKanjiView.getChildAt(i) as TextView).text = pastKanjis[i].word
        }

        pastKanjiScrollView.scrollTo(0, 0)
    }

    private fun getTextView(text: String) : TextView
    {
        val tv = TextView(context)
        val padding = dpToPx(context, 5)

        tv.setTextColor(Color.BLACK)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.toFloat())
        tv.setPadding(padding, padding, padding, padding)
        tv.text = text

        tv.setOnClickListener { v -> showEntry(pastKanjiView.indexOfChild(v)) }

        return tv
    }

    private fun showEntry(index: Int)
    {
        params.height = MATCH_PARENT
        windowManager.updateViewLayout(window, params)

        displayResults(pastKanjis[index].results)
    }

    private fun displayResults(jmResults: List<JmSearchResult>)
    {
        val sb = StringBuilder()

        for ((entry, deinfInfo) in jmResults)
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

            val deinfReason = deinfInfo!!.reason
            if (deinfReason != null && !deinfReason.isEmpty())
            {
                sb.append(String.format(" %s", deinfReason))
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