package ca.fuwafuwa.kaku.Windows.Interfaces;

import android.view.MotionEvent;

/**
 * Created by Xyresic on 4/12/2016.
 */
public interface WindowListener {
    boolean onTouchEvent(MotionEvent e);
    boolean onResizeEvent(MotionEvent e);
}