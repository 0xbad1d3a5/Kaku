package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by 0x1bad1d3a on 5/6/2016.
 */
public class LinearLayoutView extends LinearLayout {

    public LinearLayoutView(Context context) {
        super(context);
    }

    public LinearLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return true;
    }
}
