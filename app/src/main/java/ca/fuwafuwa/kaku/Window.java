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

    /**
     * stop() MUST be called or the window does not get removed from the android screen
     * otherwise, the view remains on the screen even after you stop the service
     */
    @Override
    public final void stop() {
        Log.d(TAG, "WINDOW CLOSING");
        cleanup();
        mWindowManager.removeView(mWindow);
    }

    /**
     * Implementing classes of Window MUST call cleanup if they need to release resources
     */
    protected abstract void cleanup();
}

