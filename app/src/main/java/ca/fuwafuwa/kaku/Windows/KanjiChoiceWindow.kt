package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
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
    private val choiceWindow = window.findViewById<RelativeLayout>(R.id.kanji_choice_window)!!
    private val choiceIcon = ImageView(context)
    private val currentKanjiViews = mutableListOf<View>()

    init
    {
        choiceWindow.addView(choiceIcon)
    }

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
        choiceIcon.x = kanjiBoxParams.x.toFloat()
        choiceIcon.y = kanjiBoxParams.y.toFloat() - statusBarHeight
        choiceIcon.layoutParams = RelativeLayout.LayoutParams(kanjiBoxParams.width, kanjiBoxParams.height)
        choiceIcon.setImageResource(R.drawable.icon_swap)

        if (squareChar !is SquareCharOcr)
        {
            show()
            return
        }

        val topRectHeight = kanjiBoxParams.y - statusBarHeight
        val bottomRectHeight = realDisplaySize.y - kanjiBoxParams.y - kanjiBoxParams.height - (realDisplaySize.y - viewHeight - statusBarHeight)

        if (bottomRectHeight > topRectHeight)
        {
            drawOnBottom(squareChar, kanjiBoxParams, calculateBounds(kanjiBoxParams, topRectHeight, bottomRectHeight))
        }
        else
        {
            drawOnTop(squareChar, kanjiBoxParams, calculateBounds(kanjiBoxParams, topRectHeight, bottomRectHeight))
        }

        show()
    }

    fun onSquareScroll(e: MotionEvent)
    {
        for (kanjiView in currentKanjiViews)
        {
            val isTextView = kanjiView is TextView

            if (checkForSelection(kanjiView, e) && isTextView)
            {
                kanjiView.setBackgroundResource(R.drawable.bg_solid_border_0_blue_black)
            }
            else if (isTextView)
            {
                kanjiView.setBackgroundResource(R.drawable.bg_solid_border_0_white_black)
            }
        }
    }

    fun onSquareScrollEnd(e: MotionEvent) : String?
    {
        var returnVal: String? = null

        for (kanjiView in currentKanjiViews)
        {
            if (checkForSelection(kanjiView, e) && kanjiView is TextView)
            {
                returnVal = kanjiView.text.toString()
            }
        }

        removeKanjiViews()
        hide()

        return returnVal
    }

    private fun checkForSelection(kanjiView: View, e: MotionEvent): Boolean
    {
        var pos = IntArray(2)
        kanjiView.getLocationOnScreen(pos)

        return pos[0] < e.rawX && e.rawX < pos[0] + kanjiView.width &&
               pos[1] < e.rawY && e.rawY < pos[1] + kanjiView.height
    }

    private fun removeKanjiViews()
    {
        for (k in currentKanjiViews)
        {
            choiceWindow.removeView(k)
        }

        currentKanjiViews.clear()
    }

    private fun calculateBounds(kanjiBoxParams: BoxParams, topRectHeight: Int, bottomRectHeight: Int) : BoxParams
    {
        val midPoint = kanjiBoxParams.x + (kanjiBoxParams.width / 2)
        var maxWidth = dpToPx(context, 400)
        var xPos = 0

        if (realDisplaySize.x > maxWidth)
        {
            xPos = midPoint - (maxWidth / 2)
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

        if (topRectHeight > bottomRectHeight)
        {
            return BoxParams(xPos, 0, maxWidth, topRectHeight)
        }
        else
        {
            return BoxParams(xPos, kanjiBoxParams.y + kanjiBoxParams.height - statusBarHeight, maxWidth, bottomRectHeight)
        }
    }

    private fun drawOnBottom(squareChar: SquareCharOcr, kanjiBoxParams: BoxParams, choiceParams: BoxParams)
    {
        val kanjiHeight = kanjiBoxParams.height * 2
        val kanjiWidth = kanjiBoxParams.width * 2

        val outerPadding = dpToPx(context, 10)
        val startHeight = choiceParams.y + outerPadding

        val drawableWidth = choiceParams.width - outerPadding
        val minPadding = dpToPx(context, 5)
        val numColumns = minOf(calculateNumColumns(drawableWidth, kanjiWidth, minPadding), squareChar.allChoices.size + 1)
        val outerSpacing = (choiceParams.width - (kanjiWidth + minPadding * 2) * numColumns) / 2
        val innerSpacing = minPadding

        var currColumn = 0
        var currWidth = choiceParams.x + outerSpacing + innerSpacing
        var currHeight = startHeight

        drawKanjiImage(squareChar, currWidth, currHeight, kanjiWidth, kanjiHeight)
        currWidth += kanjiWidth + innerSpacing
        currColumn++

        for (choice in squareChar.allChoices)
        {
            if (currColumn >= numColumns)
            {
                currHeight += kanjiHeight + innerSpacing
                currWidth = choiceParams.x + outerSpacing + innerSpacing
                currColumn = 0
            }

            drawKanjiText(choice.first, currWidth, currHeight, kanjiWidth, kanjiHeight)
            currWidth += kanjiWidth + innerSpacing
            currColumn++
        }
    }

    private fun drawOnTop(squareChar: SquareCharOcr, kanjiBoxParams: BoxParams, choiceParams: BoxParams)
    {
        val kanjiHeight = kanjiBoxParams.height * 2
        val kanjiWidth = kanjiBoxParams.width * 2

        val outerPadding = dpToPx(context, 10)
        val startHeight = kanjiBoxParams.y - statusBarHeight - kanjiHeight - outerPadding

        val drawableWidth = choiceParams.width - outerPadding
        val minPadding = dpToPx(context, 5)
        val numColumns = minOf(calculateNumColumns(drawableWidth, kanjiWidth, minPadding), squareChar.allChoices.size + 1)
        val outerSpacing = (choiceParams.width - (kanjiWidth + minPadding * 2) * numColumns) / 2
        val innerSpacing = minPadding

        var currColumn = 0
        var currWidth = choiceParams.x + outerSpacing + innerSpacing
        var currHeight = startHeight

        drawKanjiImage(squareChar, currWidth, currHeight, kanjiWidth, kanjiHeight)
        currWidth += kanjiWidth + innerSpacing
        currColumn++

        for (choice in squareChar.allChoices)
        {
            if (currColumn >= numColumns)
            {
                currHeight -= kanjiHeight + innerSpacing
                currWidth = choiceParams.x + outerSpacing + innerSpacing
                currColumn = 0
            }

            drawKanjiText(choice.first, currWidth, currHeight, kanjiWidth, kanjiHeight)
            currWidth += kanjiWidth + innerSpacing
            currColumn++
        }
    }

    private fun drawKanjiText(kanji: String, x: Int, y: Int, kanjiWidth: Int, kanjiHeight: Int)
    {
        val tv = TextView(context)
        tv.text = kanji
        tv.gravity = Gravity.CENTER
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (kanjiWidth / 1.5).toFloat())
        tv.setTextColor(Color.BLACK)
        tv.setBackgroundResource(R.drawable.bg_solid_border_0_white_black)
        tv.width = kanjiWidth
        tv.height = kanjiHeight
        tv.x = x.toFloat()
        tv.y = y.toFloat()
        choiceWindow.addView(tv)
        currentKanjiViews.add(tv)
    }

    private fun drawKanjiImage(squareChar: SquareCharOcr, x: Int, y: Int, kanjiWidth: Int, kanjiHeight: Int)
    {
        // Image nonsense
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
        charImage.layoutParams = LinearLayout.LayoutParams(kanjiWidth, kanjiHeight)
        charImage.x = x.toFloat()
        charImage.y = y.toFloat()
        charImage.scaleType = ImageView.ScaleType.FIT_CENTER
        charImage.cropToPadding = true
        charImage.setImageBitmap(bitmapChar)
        charImage.background = context.getDrawable(R.drawable.bg_translucent_border_0_black_black)
        choiceWindow.addView(charImage)
        currentKanjiViews.add(charImage)
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