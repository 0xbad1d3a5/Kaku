package ca.fuwafuwa.kaku;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class ResizeView extends LinearLayout {

    private CaptureWindowCallback captureWindowCallback;

    public ResizeView(Context context) {
        super(context);
    }

    public ResizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void registerCallback(CaptureWindowCallback captureWindowCallback){
        this.captureWindowCallback = captureWindowCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return captureWindowCallback.onResizeEvent(e);
    }
}
