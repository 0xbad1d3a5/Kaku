package ca.fuwafuwa.kaku.Windows.Interfaces;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by 0xbad1d3a5 on 4/12/2016.
 */
public interface WindowListener extends GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    boolean onTouch(MotionEvent e);
    boolean onResize(MotionEvent e);
}