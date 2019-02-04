package ca.fuwafuwa.kaku.Windows.Views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

import ca.fuwafuwa.kaku.*

/**
 * Created by 0xbad1d3a5 on 5/5/2016.
 */
open class SquareGridView : ViewGroup
{
    protected var squareCellSize = 0
    protected var maxSquares = 0

    private var mItemCount = 0
    private var mRowLimit = 0
    private var mRows = 1

    constructor(context: Context) : super(context)
    {
        Init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        Init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
        Init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    {
        Init(context)
    }

    private fun Init(context: Context)
    {
        squareCellSize = dpToPx(context, 37)
    }

    fun setCellSize(dp: Int)
    {
        squareCellSize = dpToPx(context, dp)
    }

    fun setItemCount(items: Int)
    {
        mItemCount = items
    }

    fun setRowLimit(rowLimit: Int)
    {
        mRowLimit = rowLimit
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val cellWidthSpec = View.MeasureSpec.makeMeasureSpec(squareCellSize, View.MeasureSpec.EXACTLY)
        val cellHeightSpec = View.MeasureSpec.makeMeasureSpec(squareCellSize, View.MeasureSpec.EXACTLY)

        val count = childCount
        for (index in 0 until count)
        {
            val child = getChildAt(index)
            child.measure(cellWidthSpec, cellHeightSpec)
        }

        // set width to squareCellSize * count if width is smaller than screen, and just screen width if larger
        val x = View.resolveSize(squareCellSize * count, widthMeasureSpec)
        mRows = Math.ceil(mItemCount.toDouble() / (x / squareCellSize).toDouble()).toInt()
        mRows = if (mRows <= 0) 1 else mRows

        when (mRowLimit)
        {
            0 -> { mRows = if (mRows >= 4) 4 else mRows }
            1 -> { mRows = 1 }
            2 -> { mRows = 8}
        }

        val y = View.resolveSize(squareCellSize * mRows, heightMeasureSpec)

        setMeasuredDimension(x, y)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int)
    {
        var columns = (r - l) / squareCellSize
        val xStart = (r - l - squareCellSize * columns) / 2
        if (columns < 0)
        {
            columns = 1
        }

        var rows = 1
        var x = xStart
        var y = 0
        var i = 0
        val count = childCount
        for (index in 0 until count)
        {
            val child = getChildAt(index)
            val w = child.measuredWidth
            val h = child.measuredHeight
            val left = x + (squareCellSize - w) / 2
            val top = y + (squareCellSize - h) / 2
            child.layout(left, top, left + w, top + h)
            if (i >= columns - 1)
            {
                // advance to next row
                i = 0
                x = xStart
                y += squareCellSize
                rows++
                if (rows > mRows)
                {
                    break
                }
            } else
            {
                i++
                x += squareCellSize
            }
        }

        maxSquares = columns * mRows
    }

    companion object
    {
        private val TAG = SquareGridView::class.java.name
    }
}
