package ca.fuwafuwa.kaku.Windows.Interfaces;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Xyresic on 4/12/2016.
 */
public interface WindowTouchListener extends GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    boolean onTouch(MotionEvent e);
    boolean onResize(MotionEvent e);
}