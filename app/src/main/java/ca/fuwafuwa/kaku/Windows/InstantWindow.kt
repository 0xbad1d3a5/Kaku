package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import ca.fuwafuwa.kaku.DB_JMDICT_NAME
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized
import ca.fuwafuwa.kaku.LangUtils

import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Search.JmSearchResult
import ca.fuwafuwa.kaku.Search.SearchInfo
import ca.fuwafuwa.kaku.Search.Searcher
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Interfaces.IRecalculateKanjiViews
import ca.fuwafuwa.kaku.Windows.Interfaces.ISearchPerformer
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView
import ca.fuwafuwa.kaku.dpToPx

class InstantWindow(context: Context, windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_instant), Searcher.SearchDictDone, IRecalculateKanjiViews, ISearchPerformer
{


    enum class LayoutPosition {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    private val isBoxHorizontal: Boolean
        get()
        {
            return displayData.boxParams.width > displayData.boxParams.height;
        }

    private val instantKanjiWindow = InstantKanjiWindow(context, windowCoordinator, this)

    private val paddingSize = dpToPx(context, 5)

    private lateinit var layoutPosition: LayoutPosition

    private lateinit var displayData: DisplayDataOcr

    private var searcher: Searcher = Searcher(context)

    init
    {
        searcher.registerCallback(this)
    }

    override fun reInit(options: ReinitOptions?)
    {
        instantKanjiWindow.reInit(options);
        super.reInit(options)
    }

    override fun onDown(e: MotionEvent?): Boolean
    {
        //InformationWindow(context, ocrResult)
        hide()
        return super.onDown(e)
    }

    override fun show()
    {
        synchronized(this)
        {
            if (!addedToWindowManager)
            {
                var text = window.findViewById<TextView>(R.id.instant_window_text)
                text.text = displayData.text
                text.setTextColor(Color.BLACK)

                if (isBoxHorizontal)
                {
                    calcParamsForHorizontal()
                } else
                {
                    calcParamsForVertical()
                }
                setPadding(paddingSize, paddingSize, paddingSize, paddingSize)

                windowManager.addView(window, params)
                addedToWindowManager = true
            }

            instantKanjiWindow.setResult(displayData)
            instantKanjiWindow.show()
        }
    }

    override fun hide()
    {
        instantKanjiWindow.hide()
        super.hide()
    }

    override fun stop()
    {
        instantKanjiWindow.stop()
        super.stop()
    }

    override fun jmResultsCallback(results: MutableList<JmSearchResult>, search: SearchInfo)
    {
        displayResults(results)
    }

    override fun recalculateKanjiViews()
    {
    }

    override fun performSearch(squareChar: ISquareChar)
    {
    }

    fun search(kanjiView: KanjiCharacterView)
    {
        val kanjiViewList = instantKanjiWindow.getKanjiView().kanjiViewList
        for (k in kanjiViewList)
        {
            k.unhighlight()
        }

//        searcher.search(SearchInfo(kanjiViewList.joinToString(
//                separator = "",
//                transform = fun(kcv: KanjiCharacterView): CharSequence { return kcv.squareChar.char }
//        ), kanjiView.index, kanjiView))
    }

    fun setResult(result: DisplayDataOcr)
    {
        displayData = result
    }

    fun changeLayoutForKanjiWindow(minSize: Int)
    {
        when(layoutPosition)
        {
            LayoutPosition.TOP ->
            {
                params.y -= minSize

                if (params.y < 0)
                {
                    params.height += params.y
                    params.y = 0
                }

                setPadding(paddingSize, paddingSize, paddingSize, 0)
            }
            LayoutPosition.BOTTOM ->
            {
                params.y += minSize

                if (params.y + params.height > viewHeight)
                {
                    val overflowHeight = params.y + params.height - viewHeight
                    params.height -= overflowHeight
                }

                setPadding(paddingSize, 0, paddingSize, paddingSize)
            }
            LayoutPosition.LEFT ->
            {
                params.x -= minSize

                if (params.x < 0)
                {
                    params.width += params.x
                    params.x = 0
                }

                setPadding(paddingSize, paddingSize, 0, paddingSize)
            }
            LayoutPosition.RIGHT ->
            {
                params.x += minSize

                if (params.x + params.width > realDisplaySize.x)
                {
                    val overflowWidth = params.x + params.width - realDisplaySize.x
                    params.width -= overflowWidth
                }

                setPadding(0, paddingSize, paddingSize, paddingSize)
            }
        }

        windowManager.updateViewLayout(window, params)
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

        val tv = window.findViewById<TextView>(R.id.instant_window_text)
        tv.text = sb.toString()
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
    }
}