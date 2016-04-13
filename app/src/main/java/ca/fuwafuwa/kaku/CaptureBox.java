package ca.fuwafuwa.kaku;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Xyresic on 4/12/2016.
 */
public class CaptureBox extends LinearLayout {

    MoveCallback moveCallback;

    private float initialX;
    private float initialY;
    private float initialTouchX;
    private float initialTouchY;

    public CaptureBox(Context context){
        super(context);
    }

    public CaptureBox(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public void registerMoveCallback(MoveCallback moveCallback){
        this.moveCallback = moveCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        View v = findViewById(R.id.capture_box);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = v.getX();
                initialY = v.getY();
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                moveCallback.screenshotCallback();
                return true;
            case MotionEvent.ACTION_MOVE:
                float x = initialX + (int) (event.getRawX() - initialTouchX);
                float y = initialY + (int) (event.getRawY() - initialTouchY);
                moveCallback.moveCallback(x, y);
                return true;
        }

        /*
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height++;
        setLayoutParams(params);
        invalidate();
        */

        return true;
    }
}
