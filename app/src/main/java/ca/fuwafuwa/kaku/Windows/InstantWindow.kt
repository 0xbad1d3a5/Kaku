package ca.fuwafuwa.kaku.Windows

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager

import ca.fuwafuwa.kaku.*
import ca.fuwafuwa.kaku.R

class InstantWindow(context: Context) : Window(context, R.layout.instant_window)
{
    init
    {
        params.x = 100
        params.y = 100
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
}