package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import ca.fuwafuwa.kaku.Windows.Interfaces.WindowTouchListener;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class ResizeView extends LinearLayout implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private WindowTouchListener mWindowTouchListener;
    private GestureDetectorCompat mDetector;

    public ResizeView(Context context) {
        super(context);
        init(context);
    }

    public ResizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ResizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mDetector = new GestureDetectorCompat(context, this);
        mDetector.setOnDoubleTapListener(this);
    }

    public void setWindowListener(WindowTouchListener windowTouchListener){
        this.mWindowTouchListener = windowTouchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if (mDetector.onTouchEvent(e)){
            return mWindowTouchListener.onDoubleTap(e);
        }
        return mWindowTouchListener.onResize(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
