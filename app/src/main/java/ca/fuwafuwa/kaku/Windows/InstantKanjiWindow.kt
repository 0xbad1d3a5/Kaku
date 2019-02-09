package ca.fuwafuwa.kaku.Windows

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.widget.LinearLayout
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr
import ca.fuwafuwa.kaku.Windows.Interfaces.ICopyText
import ca.fuwafuwa.kaku.Windows.Interfaces.IRecalculateKanjiViews
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView
import ca.fuwafuwa.kaku.dpToPx


class InstantKanjiWindow(context: Context,
                         windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_instant_kanji), IRecalculateKanjiViews, ICopyText
{
    private val isBoxHorizontal: Boolean
        get()
        {
            return displayData.boxParams.width > displayData.boxParams.height;
        }

    private lateinit var displayData: DisplayDataOcr

    private lateinit var layoutPosition: LayoutPosition

    private val kanjiGrid = window.findViewById<View>(R.id.kanji_grid) as KanjiGridView

    private val kanjiFrame = window.findViewById<LinearLayout>(R.id.instant_window_kanji_frame)

    private val instantInfoWindow = InstantInfoWindow(context, windowCoordinator, this)

    val minHeight = dpToPx(context, 60)

    val minWidth = dpToPx(context, 65)

    init
    {
        kanjiGrid.setDependencies(windowCoordinator, instantInfoWindow)

        kanjiFrame.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            run {
                val oldWidth = params.width
                val oldHeight = params.height
                params.width = v.width + dpToPx(context, 10)
                params.height = v.height + dpToPx(context, 10)

                if (isBoxHorizontal)
                {
                    if (params.height < minHeight)
                    {
                        params.height = minHeight
                        params.width = oldWidth
                    }
                }
                else {
                    if (params.width < minWidth)
                    {
                        params.width = minWidth
                        params.height = oldHeight
                    }
                }

                when (layoutPosition)
                {
                    LayoutPosition.TOP ->
                    {
                        params.y = displayData.boxParams.y - (params.height + statusBarHeight)
                    }
                    LayoutPosition.BOTTOM ->
                    {
                        params.y = displayData.boxParams.y + displayData.boxParams.height - statusBarHeight
                    }
                    LayoutPosition.LEFT ->
                    {
                        params.x = displayData.boxParams.x - params.width
                    }
                    LayoutPosition.RIGHT ->
                    {
                        params.x = displayData.boxParams.x + displayData.boxParams.width
                    }
                }

                window.visibility = View.VISIBLE
                windowManager.updateViewLayout(window, params)
                Log.d(TAG, "layoutChanged")
            }
        }
    }

    fun getLayoutPosition() : LayoutPosition
    {
        return layoutPosition
    }

    fun getWidth() : Int
    {
        return window.width
    }

    fun getHeight() : Int
    {
        return window.height
    }

    fun setResult(result: DisplayDataOcr)
    {
        displayData = result
        instantInfoWindow.setResult(result)
        instantInfoWindow.performSearch(result.squareChars[0])
    }

    override fun recalculateKanjiViews()
    {
        kanjiGrid.recalculateKanjiViews()
    }

    override fun copyText()
    {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(null, kanjiGrid.getText())
        clipboard.primaryClip = clip

        hide()
    }

    override fun hide()
    {
        instantInfoWindow.hide()
        super.hide()
    }

    override fun show()
    {
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

                //window.visibility = INVISIBLE
                windowManager.addView(window, params)
                addedToWindowManager = true
            }
        }

        instantInfoWindow.show()
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
            layoutPosition = LayoutPosition.TOP
        }

        val drawOnBottom = fun()
        {
            params.x = xPos
            params.y = displayData.boxParams.y + displayData.boxParams.height - statusBarHeight
            params.height = minHeight
            layoutPosition = LayoutPosition.BOTTOM
        }

        if (topRectHeight < bottomRectHeight)
        {
            if (topRectHeight > minHeight)
            {
                drawOnTop()
            }
            else {
                drawOnBottom()
            }
        }
        else {
            if (bottomRectHeight > minHeight)
            {
                drawOnBottom()
            }
            else {
                drawOnTop()
            }
        }
    }

    private fun calcParamsForVertical()
    {
        val leftRectWidth = displayData.boxParams.x
        val rightRectWidth = viewWidth - (displayData.boxParams.x + displayData.boxParams.width)

        var yPos = displayData.boxParams.y - statusBarHeight
        var maxHeight = dpToPx(context, 320)

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
            layoutPosition = LayoutPosition.LEFT
        }

        val drawOnRightSide = fun()
        {
            var xPos = displayData.boxParams.x + displayData.boxParams.width

            params.x = xPos
            params.y = yPos
            params.width = minOf(rightRectWidth, minWidth)
            layoutPosition = LayoutPosition.RIGHT
        }

        if (leftRectWidth < rightRectWidth)
        {
            if (leftRectWidth > minWidth)
            {
                drawOnLeftSide()
            }
            else {
                drawOnRightSide()
            }
        }
        else {
            if (rightRectWidth > minWidth)
            {
                drawOnRightSide()
            }
            else {
                drawOnLeftSide()
            }
        }
    }

    companion object
    {
        val TAG = InstantKanjiWindow::class.java.name
    }
}