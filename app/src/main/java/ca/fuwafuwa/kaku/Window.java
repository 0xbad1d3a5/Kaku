package ca.fuwafuwa.kaku;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public abstract class Window implements Stoppable {

    private static final String TAG = Window.class.getName();

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

        params = getDefaultParams();

        mWindowManager.addView(mWindow, params);
    }

    protected WindowManager.LayoutParams getDefaultParams(){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                400,
                400,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    @Override
    public final void stop() {
        Log.d(TAG, "WINDOW CLOSING");
        cleanup();
        mWindowManager.removeView(mWindow);
    }

    protected abstract void cleanup();
}

