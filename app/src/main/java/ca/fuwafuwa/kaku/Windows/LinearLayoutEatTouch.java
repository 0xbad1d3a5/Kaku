package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by 0x1bad1d3a on 5/6/2016.
 */
public class LinearLayoutEatTouch extends LinearLayout {
    public LinearLayoutEatTouch(Context context) {
        super(context);
    }

    public LinearLayoutEatTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutEatTouch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutEatTouch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return true;
    }
}
