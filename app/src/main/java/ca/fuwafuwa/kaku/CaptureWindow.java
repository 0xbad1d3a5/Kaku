package ca.fuwafuwa.kaku;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class CaptureWindow extends Window implements CaptureWindowCallback  {

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    public CaptureWindow(MainService context) {
        super(context);

        ((WindowView) mWindow.findViewById(R.id.capture_window)).registerCallback(this);
        ((ResizeView) mWindow.findViewById(R.id.resize_box)).registerCallback(this);
    }

    @Override
    public boolean onMoveEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = params.x;
                initialY = params.y;
                initialTouchX = e.getRawX();
                initialTouchY = e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                mContext.saveImage();
                mWindowManager.updateViewLayout(mWindow, params);
                return true;
            case MotionEvent.ACTION_MOVE:
                params.x = initialX + (int) (e.getRawX() - initialTouchX);
                params.y = initialY + (int) (e.getRawY() - initialTouchY);
                mWindowManager.updateViewLayout(mWindow, params);
                return true;
        }
        return false;
    }

    @Override
    public boolean onResizeEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = params.width;
                initialY = params.height;
                initialTouchX = e.getRawX();
                initialTouchY = e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                params.width = initialX + (int) (e.getRawX() - initialTouchX);
                params.height = initialY + (int) (e.getRawY() - initialTouchY);
                mWindowManager.updateViewLayout(mWindow, params);
                return true;
        }
        return true;
    }
}
