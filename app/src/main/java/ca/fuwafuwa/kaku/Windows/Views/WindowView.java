package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import ca.fuwafuwa.kaku.Windows.Interfaces.WindowTouchListener;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class WindowView extends RelativeLayout {

    private WindowTouchListener mWindowTouchListener;
    private GestureDetectorCompat mDetector;

    public WindowView(Context context) {
        super(context);
    }

    public WindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWindowListener(WindowTouchListener windowTouchListener){
        this.mWindowTouchListener = windowTouchListener;
    }

    public void setDetector(GestureDetectorCompat detector){
        this.mDetector = detector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if (mDetector.onTouchEvent(e)){
            return true;
        }
        return mWindowTouchListener.onTouch(e);
    }
}
