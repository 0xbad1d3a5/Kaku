package ca.fuwafuwa.kaku;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class Window {

    private String TAG = this.getClass().getName();

    protected MainService mContext;
    protected WindowManager mWindowManager;
    protected View mWindow;

    protected WindowManager.LayoutParams params;

    public Window(MainService context){
        this.mContext = context;

        mWindowManager = (WindowManager) mContext.getSystemService(context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        mWindow = inflater.inflate(R.layout.capture_window, null);
        mWindow.setTag(this);

        params = new WindowManager.LayoutParams(
                400,
                400,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;

        mWindowManager.addView(mWindow, params);
    }
}

