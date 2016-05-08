package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class ResizeView extends LinearLayout {

    private WindowListener mWindowListener;

    public ResizeView(Context context) {
        super(context);
    }

    public ResizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWindowListener(WindowListener windowListener){
        this.mWindowListener = windowListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return mWindowListener.onResizeEvent(e);
    }
}
