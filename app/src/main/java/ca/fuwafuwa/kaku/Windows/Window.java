package ca.fuwafuwa.kaku.Windows;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;
import ca.fuwafuwa.kaku.Windows.Views.ResizeView;
import ca.fuwafuwa.kaku.Windows.Views.WindowView;

public abstract class Window implements Stoppable, WindowListener {

    private static final String TAG = Window.class.getName();

    protected MainService context;
    protected WindowManager windowManager;
    protected View window;
    protected WindowManager.LayoutParams params;

    private WindowView mWindowView;
    private ResizeView mResizeView;
    private int mDX;
    private int mDY;
    private Point mDisplaySize;
    private long mParamUpdateTimer = System.currentTimeMillis();

    public Window(MainService context, int contentView){
        this.context = context;

        windowManager = (WindowManager) this.context.getSystemService(context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) this.context.getApplicationContext().getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.window, null);
        window.setTag(this);

        mWindowView = (WindowView) window.findViewById(R.id.window_view);
        mResizeView = (ResizeView) window.findViewById(R.id.resize_view);
        mWindowView.setWindowListener(this);
        mResizeView.setWindowListener(this);

        RelativeLayout relativeLayout = (RelativeLayout) window.findViewById(R.id.content_view);
        relativeLayout.addView(inflater.inflate(contentView, relativeLayout, false));

        mDisplaySize = this.context.getDisplaySize();
        params = getDefaultParams();

        windowManager.addView(window, params);
    }

    public void reInit(){
        mDisplaySize = context.getDisplaySize();
        fixBoxBounds();
        windowManager.updateViewLayout(window, params);
    }

    /**
     * stop() MUST be called or the window does not get removed from the android screen
     * otherwise, the view remains on the screen even after you stop the service.
     *
     * If you choose to override stop(), you should call super.stop() to remove the view.
     */
    @Override
    public void stop() {
        Log.d(TAG, "WINDOW CLOSING");
        windowManager.removeView(window);
    }

    /**
     * Override this if implementing Window does not need to move around
     *
     * @param e MotionEvent for moving the Window
     * @return Returns whether the MotionEvent was handled
     */
    public boolean onTouchEvent(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDX = params.x - (int) e.getRawX();
                mDY = params.y - (int) e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                fixBoxBounds();
                return true;
            case MotionEvent.ACTION_MOVE:
                params.x = mDX + (int) e.getRawX();
                params.y = mDY + (int) e.getRawY();
                fixBoxBounds();
                windowManager.updateViewLayout(window, params);
                return true;
        }
        return false;
    }

    /**
     * Override this if implementing Window does not need to resize
     *
     * @param e MotionEvent for resizing the Window
     * @return Returns whether the MotionEvent was handled
     */
    @Override
    public boolean onResizeEvent(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDX = params.width - (int) e.getRawX();
                mDY = params.height - (int) e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                fixBoxBounds();
                return true;
            case MotionEvent.ACTION_MOVE:
                params.width = mDX + (int) e.getRawX();
                params.height = mDY + (int) e.getRawY();
                fixBoxBounds();
                long currTime = System.currentTimeMillis();
                if (currTime - mParamUpdateTimer > 50){
                    mParamUpdateTimer = currTime;
                    windowManager.updateViewLayout(window, params);
                }
                return true;
        }
        return false;
    }

    /**
     * @return Display size of the current screen
     */
    protected Point getDisplaySize(){
        return mDisplaySize;
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

    protected int getNavigationBarHeight(){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0){
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void fixBoxBounds(){
        if (params.x < 0){
            params.x = 0;
        }
        else if (params.x + params.width > mDisplaySize.x) {
            params.x = mDisplaySize.x - params.width;
        }
        if (params.y < 0){
            params.y = 0;
        }
        else if (params.y + params.height > mDisplaySize.y) {
            params.y = mDisplaySize.y - params.height - getStatusBarHeight();
        }
        if (params.width > mDisplaySize.x){
            params.width = mDisplaySize.x;
        }
        if (params.height > mDisplaySize.y){
            params.height = mDisplaySize.y;
        }
        if (params.width < 100){
            params.width = 100;
        }
        if (params.height < 100){
            params.height = 100;
        }
    }
}

