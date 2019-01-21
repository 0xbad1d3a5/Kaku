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
    protected lateinit var mContext: Context

    private var mItemCount = 0
    private var mCellSize = 0

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
        mContext = context
        mCellSize = dpToPx(mContext, 37)
    }

    fun setCellSize(dp: Int)
    {
        mCellSize = dpToPx(mContext, dp)
    }

    fun setItemCount(items: Int)
    {
        mItemCount = items
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val cellWidthSpec = View.MeasureSpec.makeMeasureSpec(mCellSize, View.MeasureSpec.EXACTLY)
        val cellHeightSpec = View.MeasureSpec.makeMeasureSpec(mCellSize, View.MeasureSpec.EXACTLY)

        val count = childCount
        for (index in 0 until count)
        {
            val child = getChildAt(index)
            child.measure(cellWidthSpec, cellHeightSpec)
        }

        // set width to mCellSize * count if width is smaller than screen, and just screen width if larger
        val x = View.resolveSize(mCellSize * count, widthMeasureSpec)
        var rows = Math.ceil(mItemCount.toDouble() / (x / mCellSize).toDouble()).toInt()
        rows = if (rows <= 0) 1 else rows
        rows = if (rows >= 4) 4 else rows
        val y = mCellSize * rows

        setMeasuredDimension(x, y)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int)
    {
        var columns = (r - l) / mCellSize
        val xStart = (r - l - mCellSize * columns) / 2
        if (columns < 0)
        {
            columns = 1
        }

        var x = xStart
        var y = 0
        var i = 0
        val count = childCount
        for (index in 0 until count)
        {
            val child = getChildAt(index)
            val w = child.measuredWidth
            val h = child.measuredHeight
            val left = x + (mCellSize - w) / 2
            val top = y + (mCellSize - h) / 2
            child.layout(left, top, left + w, top + h)
            if (i >= columns - 1)
            {
                // advance to next row
                i = 0
                x = xStart
                y += mCellSize
            } else
            {
                i++
                x += mCellSize
            }
        }
    }

    companion object
    {
        private val TAG = SquareGridView::class.java.name
    }
}
