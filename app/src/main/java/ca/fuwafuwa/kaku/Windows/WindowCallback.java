package ca.fuwafuwa.kaku.Windows;

import android.view.MotionEvent;

/**
 * Created by Xyresic on 4/12/2016.
 */
public interface WindowCallback {
    boolean onTouchEvent(MotionEvent e);
    boolean onResizeEvent(MotionEvent e);
}