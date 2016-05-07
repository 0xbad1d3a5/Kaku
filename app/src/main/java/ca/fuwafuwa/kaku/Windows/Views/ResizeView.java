package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import ca.fuwafuwa.kaku.Windows.Interfaces.IWindowCallback;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class ResizeView extends LinearLayout {

    private IWindowCallback mWindowCallback;

    public ResizeView(Context context) {
        super(context);
    }

    public ResizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void registerCallback(IWindowCallback windowCallback){
        this.mWindowCallback = windowCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return mWindowCallback.onResizeEvent(e);
    }
}
