package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class WindowView extends RelativeLayout {

    private WindowListener mWindowListener;

    public WindowView(Context context) {
        super(context);
    }

    public WindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWindowListener(WindowListener windowListener){
        this.mWindowListener = windowListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return mWindowListener.onTouchEvent(e);
    }
}
