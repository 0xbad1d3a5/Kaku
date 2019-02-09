package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import ca.fuwafuwa.kaku.*
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized

import ca.fuwafuwa.kaku.Search.JmSearchResult
import ca.fuwafuwa.kaku.Search.SearchInfo
import ca.fuwafuwa.kaku.Search.Searcher
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Interfaces.IRecalculateKanjiViews
import ca.fuwafuwa.kaku.Windows.Interfaces.ISearchPerformer

enum class LayoutPosition {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
}

class InstantInfoWindow(context: Context,
                        windowCoordinator: WindowCoordinator,
                        private val instantKanjiWindow: InstantKanjiWindow) : Window(context, windowCoordinator, R.layout.window_instant_info), Searcher.SearchDictDone, IRecalculateKanjiViews, ISearchPerformer
{
    private val isBoxHorizontal: Boolean
        get()
        {
            return displayData.boxParams.width > displayData.boxParams.height;
        }

    private val paddingSize = dpToPx(context, 5)

    private lateinit var layoutPosition: LayoutPosition

    private lateinit var displayData: DisplayDataOcr

    private var searcher: Searcher = Searcher(context)

    private var textInfo = window.findViewById<TextView>(R.id.instant_window_text)

    private var textFrame = window.findViewById<LinearLayout>(R.id.instant_window_text_frame)

    private var updateView = false

    init
    {
        searcher.registerCallback(this)

        textFrame.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            run {
                if (updateView)
                {
                    when (layoutPosition)
                    {
                        LayoutPosition.LEFT ->
                        {
                            params.x = params.x + (params.width - v.width) - dpToPx(context, 10)
                            //params.y = params.y + (params.height - v.height) - dpToPx(context, 10)
                            params.width = v.width + dpToPx(context, 10)
                            params.height = v.height + dpToPx(context, 10)
                        }
                        LayoutPosition.RIGHT ->
                        {
                            //params.y = params.y + (params.height - v.height) - dpToPx(context, 10)
                            params.width = v.width + dpToPx(context, 10)
                            params.height = v.height + dpToPx(context, 10)
                        }
                        LayoutPosition.TOP ->
                        {
                            params.y = params.y + (params.height - v.height) - dpToPx(context, 10)
                            params.width = v.width + dpToPx(context, 10)
                            params.height = v.height + dpToPx(context, 10)
                        }
                        LayoutPosition.BOTTOM ->
                        {
                            params.width = v.width + dpToPx(context, 10)
                            params.height = v.height + dpToPx(context, 10)
                        }
                    }

                    window.visibility = VISIBLE
                    windowManager.updateViewLayout(window, params)
                    updateView = false
                }
            }
        }
    }

    override fun onDown(e: MotionEvent?): Boolean
    {
        instantKanjiWindow.hide()
        return super.onDown(e)
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
        synchronized(this)
        {
            if (!addedToWindowManager)
            {
                textInfo.text = displayData.text
                textInfo.setTextColor(Color.BLACK)

                if (isBoxHorizontal)
                {
                    calcParamsForHorizontal()
                } else
                {
                    calcParamsForVertical()
                }

                window.visibility = INVISIBLE
                windowManager.addView(window, params)
                addedToWindowManager = true
            }
        }
    }

    override fun jmResultsCallback(results: MutableList<JmSearchResult>, search: SearchInfo)
    {
        show()
        updateView = true
        displayResults(results)

        // Highlights words in the window as long as they match
        val start = search.index
        if (results.size > 0)
        {
            val kanji = results[0].word
            for (i in start until start + kanji.codePointCount(0, kanji.length))
            {
                if (i >= instantKanjiWindow.getKanjiView().kanjiViewList.size)
                {
                    break
                }
                instantKanjiWindow.getKanjiView().kanjiViewList[i].highlight()
            }
        } else
        {
            instantKanjiWindow.getKanjiView().kanjiViewList[start].highlight()
        }
    }

    override fun recalculateKanjiViews()
    {
        instantKanjiWindow.recalculateKanjiViews()
    }

    override fun performSearch(squareChar: ISquareChar)
    {
        hide()
        instantKanjiWindow.getKanjiView().unhighlightAll(squareChar)
        searcher.search(SearchInfo(squareChar))
    }

    fun setResult(result: DisplayDataOcr)
    {
        displayData = result
    }

    private fun changeLayoutForKanjiWindow()
    {
        if (instantKanjiWindow.getLayoutPosition() != layoutPosition)
        {
            setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
            return
        }

        var kanjiWindowSize = if (isBoxHorizontal) instantKanjiWindow.getHeight() else instantKanjiWindow.getWidth()

        when(layoutPosition)
        {
            LayoutPosition.TOP ->
            {
                kanjiWindowSize -= dpToPx(context, 5)
                params.y -= kanjiWindowSize

                if (params.y < 0)
                {
                    params.height += params.y
                    params.y = 0
                }

                setPadding(paddingSize, paddingSize, paddingSize, 0)
            }
            LayoutPosition.BOTTOM ->
            {
                params.y += kanjiWindowSize

                if (params.y + params.height > viewHeight)
                {
                    val overflowHeight = params.y + params.height - viewHeight
                    params.height -= overflowHeight
                }

                setPadding(paddingSize, 0, paddingSize, paddingSize)
            }
            LayoutPosition.LEFT ->
            {
                kanjiWindowSize += dpToPx(context, 5)
                params.x -= kanjiWindowSize

                if (params.x < 0)
                {
                    params.width += params.x
                    params.x = 0
                }

                setPadding(paddingSize, paddingSize, 0, paddingSize)
            }
            LayoutPosition.RIGHT ->
            {
                kanjiWindowSize += dpToPx(context, 5)
                params.x += kanjiWindowSize

                if (params.x + params.width > realDisplaySize.x)
                {
                    val overflowWidth = params.x + params.width - realDisplaySize.x
                    params.width -= overflowWidth
                }

                setPadding(0, paddingSize, paddingSize, paddingSize)
            }
        }
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

        textInfo.text = sb.toString()
    }

    private fun getMeaning(entry: EntryOptimized): String
    {
        val meanings = entry.meanings.split("\ufffc".toRegex()).toTypedArray()
        val pos = entry.pos.split("\ufffc".toRegex()).toTypedArray()

        val sb = StringBuilder()

        for (i in meanings.indices)
        {
            if (i > 2)
            {
                sb.append(" [......]")
                break
            }
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

    private fun setPadding(l: Int, t: Int, r: Int, b: Int)
    {
        val frameLayout = window.findViewById<FrameLayout>(R.id.instant_window_layout)
        frameLayout.setPadding(l, t, r, b)
    }

    private fun calcParamsForHorizontal()
    {
        val topRectHeight = displayData.boxParams.y - statusBarHeight
        val bottomRectHeight = realDisplaySize.y - displayData.boxParams.y - displayData.boxParams.height - (realDisplaySize.y - viewHeight - statusBarHeight)

        val boxMidPoint = displayData.boxParams.x + (displayData.boxParams.width / 2)
        var maxWidth = dpToPx(context, 400)
        var xPos = 0

        if (realDisplaySize.x > maxWidth)
        {
            xPos = boxMidPoint - maxWidth / 2
            if (xPos < 0)
            {
                xPos = 0
            }
            else if (xPos + maxWidth > realDisplaySize.x)
            {
                xPos = realDisplaySize.x - maxWidth
            }
        }

        maxWidth = minOf(realDisplaySize.x, maxWidth)

        params.width = maxWidth
        if (topRectHeight > bottomRectHeight){
            params.x = xPos
            params.y = 0
            params.height = topRectHeight
            layoutPosition = LayoutPosition.TOP
        }
        else {
            params.x = xPos
            params.y = displayData.boxParams.y + displayData.boxParams.height - statusBarHeight
            params.height = bottomRectHeight
            layoutPosition = LayoutPosition.BOTTOM
        }

        changeLayoutForKanjiWindow()
    }

    private fun calcParamsForVertical()
    {
        val leftRectWidth = displayData.boxParams.x
        val rightRectWidth = viewWidth - (displayData.boxParams.x + displayData.boxParams.width)

        var yPos = displayData.boxParams.y - statusBarHeight
        var maxHeight = dpToPx(context, 600)

        maxHeight = minOf(maxHeight, realDisplaySize.y)

        if (yPos + maxHeight > realDisplaySize.y){
            yPos = viewHeight - maxHeight
        }

        params.height = maxHeight
        if (leftRectWidth > rightRectWidth)
        {
            var maxWidth = dpToPx(context, 400)
            var xPos = displayData.boxParams.x - maxWidth

            if (xPos < 0)
            {
                xPos = 0
            }

            params.x = xPos
            params.y = yPos
            params.width = minOf(leftRectWidth, maxWidth)
            layoutPosition = LayoutPosition.LEFT
        }
        else {
            var maxWidth = dpToPx(context, 400)
            var xPos = displayData.boxParams.x + displayData.boxParams.width

            params.x = xPos
            params.y = yPos
            params.width = minOf(rightRectWidth, maxWidth)
            layoutPosition = LayoutPosition.RIGHT
        }

        changeLayoutForKanjiWindow()
    }

    companion object
    {
        val TAG = InstantInfoWindow::class.java.name
    }
}