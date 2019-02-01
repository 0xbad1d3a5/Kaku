package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.view.MotionEvent
import android.view.View
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.WINDOW_INSTANT_INFO
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr
import ca.fuwafuwa.kaku.Windows.Interfaces.IRecalculateKanjiViews
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView
import ca.fuwafuwa.kaku.dpToPx


class InstantKanjiWindow(context: Context,
                         windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_instant_kanji), IRecalculateKanjiViews
{
    private val isBoxHorizontal: Boolean
        get()
        {
            return displayData.boxParams.width > displayData.boxParams.height;
        }

    private lateinit var displayData: DisplayDataOcr

    private val kanjiGrid = window.findViewById<View>(R.id.kanji_grid) as KanjiGridView

    private val instantWindow = windowCoordinator.getWindow(WINDOW_INSTANT_INFO) as InstantInfoWindow

    init
    {
        kanjiGrid.setDependencies(windowCoordinator, instantWindow)
    }

    fun setResult(result: DisplayDataOcr)
    {
        displayData = result
        instantWindow.setResult(result)
        instantWindow.performSearch(result.squareChars[0])
    }

    override fun recalculateKanjiViews()
    {
        kanjiGrid.recalculateKanjiViews()
    }

    override fun show()
    {
        instantWindow.show()

        synchronized(this)
        {
            if (!addedToWindowManager)
            {
                if (isBoxHorizontal)
                {
                    kanjiGrid.setRowLimit(1)
                    calcParamsForHorizontal()
                } else
                {
                    kanjiGrid.setRowLimit(2)
                    calcParamsForVertical()
                }

                kanjiGrid.clearText()
                kanjiGrid.setText(displayData)

                windowManager.addView(window, params)
                addedToWindowManager = true
            }
        }
    }

    override fun onTouch(e: MotionEvent?): Boolean
    {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean
    {
        return false
    }

    override fun onResize(e: MotionEvent?): Boolean
    {
        return false
    }

    fun getKanjiView() : KanjiGridView
    {
        return kanjiGrid
    }

    private fun calcParamsForHorizontal()
    {
        val topRectHeight = displayData.boxParams.y - statusBarHeight
        val bottomRectHeight = realDisplaySize.y - displayData.boxParams.y - displayData.boxParams.height - (realDisplaySize.y - viewHeight - statusBarHeight)

        val boxMidPoint = displayData.boxParams.x + (displayData.boxParams.width / 2)
        val minHeight = dpToPx(context, 60)
        var maxWidth = dpToPx(context, 300)
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

        val drawOnTop = fun()
        {
            params.x = xPos
            params.y = displayData.boxParams.y - (minHeight + statusBarHeight)
            params.height = minHeight
        }

        val drawOnBottom = fun()
        {
            params.x = xPos
            params.y = displayData.boxParams.y + displayData.boxParams.height - statusBarHeight
            params.height = minHeight
        }

        if (topRectHeight < bottomRectHeight)
        {
            if (topRectHeight > minHeight)
            {
                drawOnTop()
            }
            else {
                drawOnBottom()
                instantWindow.changeLayoutForKanjiWindow(minHeight)
            }
        }
        else {
            if (bottomRectHeight > minHeight)
            {
                drawOnBottom()
            }
            else {
                drawOnTop()
                instantWindow.changeLayoutForKanjiWindow(minHeight)
            }
        }
    }

    private fun calcParamsForVertical()
    {
        val leftRectWidth = displayData.boxParams.x
        val rightRectWidth = viewWidth - (displayData.boxParams.x + displayData.boxParams.width)

        var yPos = displayData.boxParams.y - statusBarHeight
        var maxHeight = dpToPx(context, 320)
        var minWidth = dpToPx(context, 65)

        maxHeight = minOf(maxHeight, realDisplaySize.y)

        if (yPos + maxHeight > realDisplaySize.y)
        {
            yPos = viewHeight - maxHeight
        }

        params.height = maxHeight

        val drawOnLeftSide = fun()
        {
            var xPos = displayData.boxParams.x - minWidth

            if (xPos < 0)
            {
                xPos = 0
            }

            params.x = xPos
            params.y = yPos
            params.width = minOf(leftRectWidth, minWidth)
        }

        val drawOnRightSide = fun()
        {
            var xPos = displayData.boxParams.x + displayData.boxParams.width

            params.x = xPos
            params.y = yPos
            params.width = minOf(rightRectWidth, minWidth)
        }

        if (leftRectWidth < rightRectWidth)
        {
            if (leftRectWidth > minWidth)
            {
                drawOnLeftSide()
            }
            else {
                drawOnRightSide()
                instantWindow.changeLayoutForKanjiWindow(minWidth)
            }
        }
        else {
            if (rightRectWidth > minWidth)
            {
                drawOnRightSide()
            }
            else {
                drawOnLeftSide()
                instantWindow.changeLayoutForKanjiWindow(minWidth)
            }
        }
    }
}