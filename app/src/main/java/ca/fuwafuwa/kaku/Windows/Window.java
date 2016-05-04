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
import ca.fuwafuwa.kaku.Stoppable;

public abstract class Window implements Stoppable, WindowCallback {

    private static final String TAG = Window.class.getName();

    protected MainService mContext;
    protected WindowManager mWindowManager;
    protected View mWindow;
    protected WindowManager.LayoutParams params;

    private WindowView windowView;
    private ResizeView resizeView;

    private int dX;
    private int dY;
    private Point displaySize;
    private long paramUpdateTimer = System.currentTimeMillis();

    public Window(MainService context, int contentView){
        this.mContext = context;

        mWindowManager = (WindowManager) mContext.getSystemService(context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        mWindow = inflater.inflate(R.layout.window, null);
        mWindow.setTag(this);

        windowView = (WindowView) mWindow.findViewById(R.id.window_view);
        resizeView = (ResizeView) mWindow.findViewById(R.id.resize_view);
        windowView.registerCallback(this);
        resizeView.registerCallback(this);

        RelativeLayout relativeLayout = (RelativeLayout) mWindow.findViewById(R.id.content_view);
        relativeLayout.addView(inflater.inflate(contentView, relativeLayout, false));

        displaySize = mContext.getDisplaySize();
        params = getDefaultParams();

        mWindowManager.addView(mWindow, params);
    }

    public void reInit(){
        displaySize = mContext.getDisplaySize();
        fixBoxBounds();
        mWindowManager.updateViewLayout(mWindow, params);
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
     * Override this if implementing Window does not need to move around
     *
     * @param e MotionEvent for moving the Window
     * @return Returns whether the MotionEvent was handled
     */
    public boolean onMoveEvent(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = params.x - (int) e.getRawX();
                dY = params.y - (int) e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                fixBoxBounds();
                return true;
            case MotionEvent.ACTION_MOVE:
                params.x = dX + (int) e.getRawX();
                params.y = dY + (int) e.getRawY();
                fixBoxBounds();
                mWindowManager.updateViewLayout(mWindow, params);
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
                dX = params.width - (int) e.getRawX();
                dY = params.height - (int) e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                fixBoxBounds();
                return true;
            case MotionEvent.ACTION_MOVE:
                params.width = dX + (int) e.getRawX();
                params.height = dY + (int) e.getRawY();
                fixBoxBounds();
                long currTime = System.currentTimeMillis();
                if (currTime - paramUpdateTimer > 50){
                    paramUpdateTimer = currTime;
                    mWindowManager.updateViewLayout(mWindow, params);
                }
                return true;
        }
        return false;
    }

    /**
     * Implementing classes of Window MUST implement cleanup if they need to release resources
     */
    protected abstract void cleanup();

    /**
     * @return Display size of the current screen
     */
    protected Point getDisplaySize(){
        return displaySize;
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
        int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0){
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void fixBoxBounds(){
        if (params.x < 0){
            params.x = 0;
        }
        else if (params.x + params.width > displaySize.x) {
            params.x = displaySize.x - params.width;
        }
        if (params.y < 0){
            params.y = 0;
        }
        else if (params.y + params.height > displaySize.y) {
            params.y = displaySize.y - params.height - getStatusBarHeight();
        }
        if (params.width > displaySize.x){
            params.width = displaySize.x;
        }
        if (params.height > displaySize.y){
            params.height = displaySize.y;
        }
        if (params.width < 100){
            params.width = 100;
        }
        if (params.height < 100){
            params.height = 100;
        }
    }
}

