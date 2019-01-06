package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.view.MotionEvent
import android.view.View
import ca.fuwafuwa.kaku.Ocr.OcrResult
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView
import ca.fuwafuwa.kaku.Windows.Views.SquareGridView
import ca.fuwafuwa.kaku.dpToPx


class InstantKanjiWindow(context: Context,
                         windowCoordinator: WindowCoordinator,
                         val instantWindow: InstantWindow) : Window(context, windowCoordinator, R.layout.instant_kanji_window), SquareGridView.SquareViewListener
{
    private val isBoxHorizontal: Boolean
        get()
        {
            return ocrResult.boxParams.width > ocrResult.boxParams.height;
        }

    private lateinit var ocrResult: OcrResult

    fun setResult(result: OcrResult)
    {
        ocrResult = result
    }

    override fun show()
    {
        synchronized(this)
        {
            if (!addedToWindowManager)
            {
                val mKanjiGrid = window.findViewById<View>(R.id.kanji_grid) as KanjiGridView
                mKanjiGrid.clearText()
                mKanjiGrid.setText(this, ocrResult)

                if (isBoxHorizontal)
                {
                    calcParamsForHorizontal()
                } else
                {
                    calcParamsForVertical()
                }

                windowManager.addView(window, params)
                addedToWindowManager = true
            }
        }
    }

    override fun onSquareScrollStart(kanjiView: KanjiCharacterView?, e: MotionEvent?)
    {
    }

    override fun onSquareScroll(kanjiView: KanjiCharacterView?, oe: MotionEvent?, e: MotionEvent?)
    {
    }

    override fun onSquareScrollEnd(kanjiView: KanjiCharacterView?, e: MotionEvent?)
    {
    }

    override fun onSquareTouch(kanjiView: KanjiCharacterView?)
    {
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

    private fun calcParamsForHorizontal()
    {
        val topRectHeight = ocrResult.boxParams.y - statusBarHeight
        val bottomRectHeight = realDisplaySize.y - ocrResult.boxParams.y - ocrResult.boxParams.height - (realDisplaySize.y - viewHeight - statusBarHeight)

        val boxMidPoint = ocrResult.boxParams.x + (ocrResult.boxParams.width / 2)
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
            params.y = ocrResult.boxParams.y - (minHeight + statusBarHeight)
            params.height = minHeight
        }

        val drawOnBottom = fun()
        {
            params.x = xPos
            params.y = ocrResult.boxParams.y + ocrResult.boxParams.height - statusBarHeight
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
        val leftRectWidth = ocrResult.boxParams.x
        val rightRectWidth = viewWidth - (ocrResult.boxParams.x + ocrResult.boxParams.width)

        var yPos = ocrResult.boxParams.y - statusBarHeight
        var maxHeight = dpToPx(context, 300)
        var minWidth = dpToPx(context, 60)

        maxHeight = minOf(maxHeight, realDisplaySize.y)

        if (yPos + maxHeight > realDisplaySize.y)
        {
            yPos = viewHeight - maxHeight
        }

        params.height = maxHeight

        val drawOnLeftSide = fun()
        {
            var xPos = ocrResult.boxParams.x - minWidth

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
            var xPos = ocrResult.boxParams.x + ocrResult.boxParams.width

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