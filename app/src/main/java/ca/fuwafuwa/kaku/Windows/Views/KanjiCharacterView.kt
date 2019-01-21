package ca.fuwafuwa.kaku.Windows.Views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import ca.fuwafuwa.kaku.*
import ca.fuwafuwa.kaku.R
import ca.fuwafuwa.kaku.Windows.Data.DisplayData
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Interfaces.ISearchPerformer
import ca.fuwafuwa.kaku.Windows.KanjiChoiceWindow
import ca.fuwafuwa.kaku.Windows.WindowCoordinator

/**
 * Created by 0xbad1d3a5 on 5/5/2016.
 */
class KanjiCharacterView : FrameLayout, GestureDetector.OnGestureListener
{
    private lateinit var mContext: Context
    private lateinit var mKanjiTextView: TextView
    private lateinit var mKanjiEditImg: ImageView
    private lateinit var mFrameView: View
    private lateinit var mGestureDetector: GestureDetector
    private lateinit var mWindowCoordinator: WindowCoordinator
    private lateinit var mSearchPerformer: ISearchPerformer
    private lateinit var mKanjiChoiceWindow: KanjiChoiceWindow
    private lateinit var squareChar: ISquareChar

    private var mScrollStartEvent: MotionEvent? = null

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
        mGestureDetector = GestureDetector(mContext, this)

        val view = LayoutInflater.from(mContext).inflate(R.layout.view_kanji_character, this, true)
        mKanjiTextView = view.findViewById(R.id.kanji_character)
        mKanjiEditImg = view.findViewById(R.id.kanji_choice_edit)
        mFrameView = view.findViewById(R.id.border_frame)
    }

    fun setDependencies(windowCoordinator: WindowCoordinator, searchPerformer: ISearchPerformer)
    {
        mWindowCoordinator = windowCoordinator
        mSearchPerformer = searchPerformer

        mKanjiChoiceWindow = mWindowCoordinator.getWindow(WINDOW_KANJI_CHOICE) as KanjiChoiceWindow
    }

    fun setText(squareChar: ISquareChar)
    {
        this.squareChar = squareChar
        mKanjiTextView.text = squareChar.char
    }

    fun highlight()
    {
        val bg = mContext.getDrawable(R.drawable.bg_translucent_border_0_blue_blue)
        mFrameView.background = bg
    }

    fun unhighlight()
    {
        mFrameView.background = null
    }

    override fun onTouchEvent(e: MotionEvent): Boolean
    {
        mGestureDetector.onTouchEvent(e)

        if (e.action == MotionEvent.ACTION_UP)
        {
            mKanjiTextView.visibility = View.VISIBLE
            mKanjiEditImg.visibility = View.INVISIBLE

            if (mScrollStartEvent != null)
            {
                //mCallback.onSquareScrollEnd(this, e);
                mScrollStartEvent = null
            }
        }

        return true
    }

    override fun onDown(motionEvent: MotionEvent): Boolean
    {
        Log.d(TAG, String.format("%s: onDown: %f x %f", squareChar.char, x, y))
        mKanjiTextView.visibility = View.INVISIBLE
        mKanjiEditImg.visibility = View.VISIBLE
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean
    {
        Log.d(TAG, "onSingleTapUp")
        mSearchPerformer.performSearch(squareChar.displayData, squareChar)
        return true
    }

    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean
    {
        if (mScrollStartEvent == null)
        {
            mScrollStartEvent = motionEvent
            //mCallback.onSquareScrollStart(this, mScrollStartEvent);
        } else
        {
            //mCallback.onSquareScroll(this, motionEvent, motionEvent1);
        }

        return true
    }

    override fun onFling(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean
    {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent)
    {
    }

    override fun onShowPress(e: MotionEvent?)
    {
    }

    companion object
    {
        private val TAG = KanjiCharacterView::class.java.name
    }
}
