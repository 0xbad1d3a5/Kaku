package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowTouchListener;
import ca.fuwafuwa.kaku.Windows.Views.ResizeView;
import ca.fuwafuwa.kaku.Windows.Views.WindowView;

public abstract class Window implements Stoppable, WindowTouchListener {

    private static final String TAG = Window.class.getName();

    protected MainService context;
    protected WindowManager windowManager;
    protected View window;
    protected WindowManager.LayoutParams params;

    private Point mRealDisplaySize;
    private int mDX;
    private int mDY;
    private View mHeightView;

    private boolean mWindowClosed = false;
    private long mParamUpdateTimer = System.currentTimeMillis();

    public Window(MainService context, int contentView){

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) this.context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        window = inflater.inflate(R.layout.window, null);
        mRealDisplaySize = this.context.getRealDisplaySize();
        params = getDefaultParams();

        WindowView mWindowView = (WindowView) window.findViewById(R.id.window_view);
        ResizeView mResizeView = (ResizeView) window.findViewById(R.id.resize_view);
        mWindowView.setWindowListener(this);
        mResizeView.setWindowListener(this);
        GestureDetectorCompat detectorCompat = new GestureDetectorCompat(context, this);
        detectorCompat.setOnDoubleTapListener(this);
        mWindowView.setDetector(detectorCompat);

        RelativeLayout relativeLayout = (RelativeLayout) window.findViewById(R.id.content_view);
        relativeLayout.addView(inflater.inflate(contentView, relativeLayout, false));

        windowManager.addView(window, params);

        // Hacky way to check if we are fullscreen
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.width = 1;
        p.height = WindowManager.LayoutParams.MATCH_PARENT;
        p.type = WindowManager.LayoutParams.TYPE_PHONE;
        p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        p.format = PixelFormat.TRANSPARENT;
        p.gravity = Gravity.END | Gravity.TOP;
        mHeightView = new View(context);
        windowManager.addView(mHeightView, p);
    }

    public void reInit(){
        mRealDisplaySize = context.getRealDisplaySize();
        fixBoxBounds();
        windowManager.updateViewLayout(window, params);
    }

    /**
     * {@link #stop()} MUST be called or the window does not get removed from the android screen
     * otherwise, the view remains on the screen even after you stop the service.
     *
     * If you choose to override {@link #stop()}, you should call super.stop() to remove the view.
     * Try not to use WindowManager to remove the view yourself, as attempting to remove the view
     * twice from WindowManager (very possible if you have a touch event closing the window) will
     * cause a crash.
     */
    @Override
    public void stop() {

        Log.d(TAG, "WINDOW CLOSING");

        synchronized (this){

            if (mWindowClosed){
                return;
            }

            mWindowClosed = true;
            windowManager.removeView(window);
        }
    }

    /**
     * Override this and {@link #onScroll} if implementing Window does not need to move around
     *
     * @param e MotionEvent for moving the Window
     * @return Returns whether the MotionEvent was handled
     */
    public boolean onTouch(MotionEvent e){

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDX = params.x - (int) e.getRawX();
                mDY = params.y - (int) e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                fixBoxBounds();
                return true;
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        if (e1 == null || e2 == null){
            return false;
        }

        params.x = mDX + (int) e2.getRawX();
        params.y = mDY + (int) e2.getRawY();
        fixBoxBounds();
        windowManager.updateViewLayout(window, params);
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * Override this if implementing Window does not need to resize
     *
     * @param e MotionEvent for resizing the Window
     * @return Returns whether the MotionEvent was handled
     */
    public boolean onResize(MotionEvent e){

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
     * @return Default LayoutParams for Window
     */
    protected WindowManager.LayoutParams getDefaultParams(){

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = KakuTools.dpToPx(context, 150);
        params.height = KakuTools.dpToPx(context, 150);
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    /**
     * @return Real screen display size
     */
    protected Point getRealDisplaySize(){
        return mRealDisplaySize;
    }

    /**
     * @return System status bar height in pixels
     */
    protected int getStatusBarHeight() {

        if (mRealDisplaySize.y == mHeightView.getHeight()){
            return 0;
        }

        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Fixes window so that it stays inside the screen even if the user is trying to drag it off screen
     * Also makes sure that the window size is not smaller than a specified value (currently hard coded)
     */
    private void fixBoxBounds(){
        if (params.x < 0){
            params.x = 0;
        }
        else if (params.x + params.width > mRealDisplaySize.x) {
            params.x = mRealDisplaySize.x - params.width;
        }
        if (params.y < 0){
            params.y = 0;
        }
        else if (params.y + params.height > mRealDisplaySize.y) {
            params.y = mRealDisplaySize.y - params.height - getStatusBarHeight();
        }
        if (params.width > mRealDisplaySize.x){
            params.width = mRealDisplaySize.x;
        }
        if (params.height > mRealDisplaySize.y){
            params.height = mRealDisplaySize.y;
        }
        if (params.width < 100){
            params.width = 100;
        }
        if (params.height < 100){
            params.height = 100;
        }
    }
}

