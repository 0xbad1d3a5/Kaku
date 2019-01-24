package ca.fuwafuwa.kaku.Windows.Views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView

import ca.fuwafuwa.kaku.*

/**
 * Created by 0xbad1d3a5 on 1/11/2017.
 */

class KanjiImageView : ImageView
{
    private var mSizePx: Int = 0

    constructor(context: Context) : super(context)
    {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    {
    }

    fun setSize(dp: Int)
    {
        mSizePx = dpToPx(context, dp)
        Log.d(TAG, String.format("setSize: X: %d Y: %d", width, height))
    }

    fun setBackground(id: Int)
    {
        val bg = context.getDrawable(id)
        background = bg
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        Log.d(TAG, String.format("mSizePx: %d", mSizePx))
        setMeasuredDimension(mSizePx, mSizePx)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        super.onLayout(changed, left, top, right, bottom)

        Log.d(TAG, String.format("onLayout: X: %d Y: %d", width, height))
    }

    companion object
    {

        private val TAG = KanjiImageView::class.java.name
    }
}