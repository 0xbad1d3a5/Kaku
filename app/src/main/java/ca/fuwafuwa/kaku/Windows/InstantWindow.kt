package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

import ca.fuwafuwa.kaku.Ocr.OcrResult
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView
import ca.fuwafuwa.kaku.Windows.Views.SquareGridView
import ca.fuwafuwa.kaku.dpToPx
import kotlin.math.max

class InstantWindow(private val ccontext: Context, private val ocrResult: OcrResult) : Window(ccontext, R.layout.instant_window), SquareGridView.SquareViewListener
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

        var text = window.findViewById<TextView>(R.id.instant_window_text)
        text.text = ocrResult.text
        text.setTextColor(Color.BLACK)
    }

    override fun onSquareScrollStart(kanjiView: KanjiCharacterView?, e: MotionEvent?)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSquareScroll(kanjiView: KanjiCharacterView?, oe: MotionEvent?, e: MotionEvent?)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSquareScrollEnd(kanjiView: KanjiCharacterView?, e: MotionEvent?)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSquareTouch(kanjiView: KanjiCharacterView?)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDown(e: MotionEvent?): Boolean
    {
        //InformationWindow(context, ocrResult)
        stop()
        return super.onDown(e)
    }

    private fun calcParamsForHorizontal()
    {
        val topRectHeight = ocrResult.boxParams.y - statusBarHeight
        val bottomRectHeight = realDisplaySize.y - ocrResult.boxParams.y - ocrResult.boxParams.height - (realDisplaySize.y - heightViewHeight - statusBarHeight)

        val boxMidPoint = ocrResult.boxParams.x + (ocrResult.boxParams.width / 2)
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
        }
        else {
            params.x = xPos
            params.y = ocrResult.boxParams.y + ocrResult.boxParams.height - statusBarHeight
            params.height = bottomRectHeight
        }
    }

    private fun calcParamsForVertical()
    {
        val leftRectWidth = ocrResult.boxParams.x
        val rightRectWidth = realDisplaySize.x - (ocrResult.boxParams.x + ocrResult.boxParams.width)

        var yPos = ocrResult.boxParams.y - statusBarHeight
        var maxHeight = dpToPx(context, 600)

        maxHeight = minOf(maxHeight, realDisplaySize.y)

        if (yPos + maxHeight > realDisplaySize.y){
            yPos = heightViewHeight - maxHeight
        }

        params.height = maxHeight
        if (leftRectWidth > rightRectWidth)
        {
            var maxWidth = dpToPx(context, 400)
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
            var maxWidth = dpToPx(context, 400)
            var xPos = ocrResult.boxParams.x + ocrResult.boxParams.width

            params.x = xPos
            params.y = yPos
            params.width = minOf(rightRectWidth, maxWidth)
        }
    }
}