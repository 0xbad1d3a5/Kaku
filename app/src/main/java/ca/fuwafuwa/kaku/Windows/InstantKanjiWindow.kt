package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import ca.fuwafuwa.kaku.Ocr.OcrResult
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView
import ca.fuwafuwa.kaku.Windows.Views.SquareGridView
import ca.fuwafuwa.kaku.dpToPx

class InstantKanjiWindow(private val ccontext: Context, private val ocrResult: OcrResult) : Window(ccontext, R.layout.instant_kanji_window), SquareGridView.SquareViewListener
{
    private val isBoxHorizontal: Boolean
        get()
        {
            return ocrResult.boxParams.width > ocrResult.boxParams.height;
        }

    init
    {
        setOnHeightKnownAction(fun()
        {
            if (isBoxHorizontal)
            {
                calcParamsForHorizontal()
            } else
            {
                calcParamsForVertical()
            }

            windowManager.updateViewLayout(window, params)
        })

        val mKanjiGrid = window.findViewById<View>(R.id.kanji_grid) as KanjiGridView
        mKanjiGrid.setText(this, ocrResult)
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
        val bottomRectHeight = realDisplaySize.y - ocrResult.boxParams.y - ocrResult.boxParams.height - (realDisplaySize.y - heightViewHeight - statusBarHeight)

        val boxMidPoint = ocrResult.boxParams.x + (ocrResult.boxParams.width / 2)
        val maxHeight = dpToPx(context, 60)
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
        if (topRectHeight < bottomRectHeight){
            params.x = xPos
            params.y = ocrResult.boxParams.y - (maxHeight + statusBarHeight)
            params.height = maxHeight
        }
        else {
            params.x = xPos
            params.y = ocrResult.boxParams.y + ocrResult.boxParams.height - statusBarHeight
            params.height = maxHeight
        }
    }

    private fun calcParamsForVertical()
    {
        val leftRectWidth = ocrResult.boxParams.x
        val rightRectWidth = realDisplaySize.x - (ocrResult.boxParams.x + ocrResult.boxParams.width)

        var yPos = ocrResult.boxParams.y - statusBarHeight
        var maxHeight = dpToPx(context, 300)

        maxHeight = minOf(maxHeight, realDisplaySize.y)

        if (yPos + maxHeight > realDisplaySize.y){
            yPos = heightViewHeight - maxHeight
        }

        params.height = maxHeight
        if (leftRectWidth < rightRectWidth)
        {
            var maxWidth = dpToPx(context, 60)
            var xPos = ocrResult.boxParams.x - maxWidth

            if (xPos < 0)
            {
                xPos = 0
            }

            params.x = xPos
            params.y = yPos
            params.width = minOf(leftRectWidth, maxWidth)
        }
        else {
            var maxWidth = dpToPx(context, 60)
            var xPos = ocrResult.boxParams.x + ocrResult.boxParams.width

            params.x = xPos
            params.y = yPos
            params.width = minOf(rightRectWidth, maxWidth)
        }
    }
}