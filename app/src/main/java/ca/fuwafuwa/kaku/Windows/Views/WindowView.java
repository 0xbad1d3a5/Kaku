package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import ca.fuwafuwa.kaku.Windows.Interfaces.IWindowCallback;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class WindowView extends RelativeLayout {

    private IWindowCallback mWindowCallback;

    public WindowView(Context context) {
        super(context);
    }

    public WindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void registerCallback(IWindowCallback windowCallback){
        this.mWindowCallback = windowCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return mWindowCallback.onTouchEvent(e);
    }
}
