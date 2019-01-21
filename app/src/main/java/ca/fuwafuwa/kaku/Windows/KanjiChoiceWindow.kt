package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import ca.fuwafuwa.kaku.Ocr.BoxParams
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Data.SquareCharOcr
import ca.fuwafuwa.kaku.dpToPx

class KanjiChoiceWindow(context: Context, windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_kanji_choice)
{
    val choiceWindow = window.findViewById<RelativeLayout>(R.id.kanji_choice_window)

    /**
     * KanjiChoiceWindow does not need to reInit layout as its getDefaultParams() are all relative. Re-initing will cause bugs.
     */
    override fun reInit(options: Window.ReinitOptions)
    {
        options.reinitViewLayout = false
        super.reInit(options)
    }

    fun onSquareScrollStart(e: MotionEvent, squareChar: ISquareChar, kanjiBoxParams: BoxParams)
    {
        choiceWindow.removeAllViews()

        if (squareChar !is SquareCharOcr)
        {
            return
        }

        val topHeight = kanjiBoxParams.y
        val bottomHeight = viewHeight - (kanjiBoxParams.y + kanjiBoxParams.height)

        if (bottomHeight > topHeight)
        {
            drawOnBottom(squareChar, kanjiBoxParams)
        }
        else
        {
            drawOnTop(squareChar, kanjiBoxParams)
        }



        show()
    }

    fun onSquareScroll(e: MotionEvent)
    {
    }

    fun onSquareScrollEnd(e: MotionEvent)
    {
        hide()
    }

    private fun drawOnBottom(squareChar: SquareCharOcr, kanjiBoxParams: BoxParams)
    {
        val startHeight = kanjiBoxParams.y + kanjiBoxParams.height + dpToPx(context, 10)
        val choiceHeight = kanjiBoxParams.height * 2
        val choiceWidth = kanjiBoxParams.width * 2

        val outerPadding = dpToPx(context, 10)
        val drawableWidth = viewWidth - outerPadding
        val minPadding = dpToPx(context, 5)
        val numColumns = minOf(calculateNumColumns(drawableWidth, choiceWidth, minPadding), squareChar.allChoices.size)
        val outerSpacing = (viewWidth - (choiceWidth + minPadding * 2) * numColumns) / 2
        val innerSpacing = minPadding

        var currColumn = 0
        var currWidth = outerSpacing + innerSpacing
        var currHeight = startHeight

        for ((index, choice) in squareChar.allChoices.withIndex())
        {
            if (currColumn >= numColumns)
            {
                currHeight += choiceHeight + innerSpacing
                currWidth = outerSpacing + innerSpacing
                currColumn = 0
            }

            if (index == 0)
            {
                val pos = squareChar.bitmapPos
                val dp10 = dpToPx(context, 10)
                val orig = squareChar.displayData.bitmap
                var width = pos[2] - pos[0]
                var height = pos[3] - pos[1]
                width = if (width <= 0) 1 else width
                height = if (height <= 0) 1 else height
                val bitmapChar = Bitmap.createBitmap(orig, pos[0], pos[1], width, height)
                val charImage = ImageView(context)

                charImage.setPadding(dp10, dp10, dp10, dp10)
                charImage.layoutParams = LinearLayout.LayoutParams(choiceWidth, choiceHeight)
                charImage.x = currWidth.toFloat()
                charImage.y = currHeight.toFloat()
                charImage.scaleType = ImageView.ScaleType.FIT_CENTER
                charImage.cropToPadding = true
                charImage.setImageBitmap(bitmapChar)
                charImage.background = context.getDrawable(R.drawable.bg_translucent_border_0_black_black)
                choiceWindow.addView(charImage)
            }
            else {
                val tv = TextView(context)
                tv.text = choice.first
                tv.gravity = Gravity.CENTER
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (choiceWidth / 1.5).toFloat())
                tv.setTextColor(Color.BLACK)
                tv.setBackgroundResource(R.drawable.bg_solid_border_0_white_black)
                tv.width = choiceWidth
                tv.height = choiceHeight
                tv.x = currWidth.toFloat()
                tv.y = currHeight.toFloat()
                choiceWindow.addView(tv)
            }

            currWidth += choiceWidth + innerSpacing
            currColumn++
        }
    }

    private fun calculateNumColumns(drawableWidth: Int, columnWidth: Int, minPadding: Int) : Int
    {
        var count = 0
        var width = 0
        val columnAndPadding = columnWidth + (minPadding * 2)

        while ((width + columnAndPadding) < drawableWidth)
        {
            width += columnAndPadding
            count++
        }

        return count
    }

    private fun drawOnTop(squareChar: SquareCharOcr, kanjiBoxParams: BoxParams)
    {

    }


    override fun onTouch(e: MotionEvent): Boolean
    {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean
    {
        return false
    }

    override fun onResize(e: MotionEvent): Boolean
    {
        return false
    }

    override fun getDefaultParams(): WindowManager.LayoutParams
    {
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT > 25) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
                0,
                PixelFormat.TRANSLUCENT)
        params.x = 0
        params.y = 0
        return params
    }
}