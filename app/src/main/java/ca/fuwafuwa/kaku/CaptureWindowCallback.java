package ca.fuwafuwa.kaku;

import android.view.MotionEvent;

/**
 * Created by 0x1bad1d3a on 4/12/2016.
 */
interface CaptureWindowCallback {
    boolean onMoveEvent(MotionEvent e);
    boolean onResizeEvent(MotionEvent e);
}