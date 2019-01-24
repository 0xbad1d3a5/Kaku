package ca.fuwafuwa.kaku.Windows.Views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by 0xbad1d3a5 on 1/8/2017.
 */

class LinearLayout : android.widget.LinearLayout
{

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

    /**
     * Eat touches so that InfoWindow doesn't get dismissed on touches outside of dismiss area
     */
    override fun onTouchEvent(event: MotionEvent): Boolean
    {
        return true
    }
}
