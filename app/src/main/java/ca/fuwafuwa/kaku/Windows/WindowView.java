package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class WindowView extends RelativeLayout {

    private WindowCallback windowCallback;

    public WindowView(Context context) {
        super(context);
    }

    public WindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void registerCallback(WindowCallback windowCallback){
        this.windowCallback = windowCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return windowCallback.onMoveEvent(e);
    }
}
