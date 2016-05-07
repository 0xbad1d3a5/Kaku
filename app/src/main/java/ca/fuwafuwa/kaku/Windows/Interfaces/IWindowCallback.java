package ca.fuwafuwa.kaku.Windows.Interfaces;

import android.view.MotionEvent;

/**
 * Created by 0x1bad1d3a on 4/12/2016.
 */
public interface IWindowCallback {
    boolean onTouchEvent(MotionEvent e);
    boolean onResizeEvent(MotionEvent e);
}