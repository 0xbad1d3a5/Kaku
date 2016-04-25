package ca.fuwafuwa.kaku.Windows;

import android.view.MotionEvent;

/**
 * Created by 0x1bad1d3a on 4/12/2016.
 */
public interface WindowCallback {
    boolean onMoveEvent(MotionEvent e);
    boolean onResizeEvent(MotionEvent e);
}