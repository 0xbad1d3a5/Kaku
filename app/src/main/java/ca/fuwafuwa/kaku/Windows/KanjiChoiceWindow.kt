package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.MotionEvent
import android.view.WindowManager
import ca.fuwafuwa.kaku.R

class KanjiChoiceWindow(context: Context, windowCoordinator: WindowCoordinator) : Window(context, windowCoordinator, R.layout.window_kanji_choice)
{


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