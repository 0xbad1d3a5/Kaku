package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.view.MotionEvent

import ca.fuwafuwa.kaku.Ocr.OcrResult
import ca.fuwafuwa.kaku.R

class InstantWindow(private val context: Context, private val ocrResult: OcrResult) : Window(context, R.layout.instant_window)
{
    init
    {
        if (isBoxHorizontal())
        {
            calcParamsForHorizontal()
        }
        else {
            calcParamsForVertical()
        }

        windowManager.updateViewLayout(window, params)
    }

    override fun onTouch(e: MotionEvent?): Boolean
    {
        stop()
        return false
    }

    override fun onResize(e: MotionEvent?): Boolean
    {
        stop()
        return false
    }

    private fun isBoxHorizontal(): Boolean
    {
        return ocrResult.boxParams.width > ocrResult.boxParams.height;
    }

    private fun calcParamsForHorizontal()
    {
        val topRectHeight = ocrResult.boxParams.y - statusBarHeight
        val bottomRectHeight = realDisplaySize.y - ocrResult.boxParams.y - ocrResult.boxParams.height - statusBarHeight

        val windowHeight = realDisplaySize.y / 3
        params.width = realDisplaySize.x;
        if (topRectHeight > bottomRectHeight){
            params.x = 0
            params.y = topRectHeight - windowHeight
            params.height = windowHeight
        }
        else {
            params.x = 0
            params.y = ocrResult.boxParams.y + ocrResult.boxParams.height - statusBarHeight
            params.height = windowHeight
        }
    }

    private fun calcParamsForVertical()
    {

    }
}