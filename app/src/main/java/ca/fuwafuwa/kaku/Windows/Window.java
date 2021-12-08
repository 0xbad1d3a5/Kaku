package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.os.Build;
import androidx.core.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;
import ca.fuwafuwa.kaku.Windows.Views.ResizeView;
import ca.fuwafuwa.kaku.Windows.Views.WindowView;

import static android.content.Context.WINDOW_SERVICE;

public abstract class Window implements Stoppable, WindowListener {

    private static final String TAG = Window.class.getName();

    public interface OnHeightKnown {
        void performAction();
    }

    public static class ReinitOptions
    {
        public boolean reinitViewLayout = true;
    }

    protected final int minSize;

    protected Context context;
    protected WindowManager windowManager;
    protected View window;
    protected WindowManager.LayoutParams params;
    protected boolean addedToWindowManager;
    protected WindowCoordinator windowCoordinator;

    private Point mRealDisplaySize;
    private int mDX;
    private int mDY;
    private View mDummyViewForSize;
    private int mViewHeight;
    private int mViewWidth;
    private List<ViewTreeObserver.OnGlobalLayoutListener> mOnHeightKnownListeners;

    private boolean mWindowClosed = false;
    private long mParamUpdateTimer = System.currentTimeMillis();

    public Window(Context context, WindowCoordinator windowCoordinator, int contentView){

        this.context = context;
        this.windowCoordinator = windowCoordinator;

        LayoutInflater inflater = (LayoutInflater) this.context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        windowManager = (WindowManager) this.context.getSystemService(WINDOW_SERVICE);
        window = inflater.inflate(R.layout.window, null);
        mRealDisplaySize = getRealDisplaySizeFromContext();
        params = getDefaultParams();
        minSize = KakuTools.dpToPx(context, 15);
        mOnHeightKnownListeners = new ArrayList<>();

        WindowView windowView = window.findViewById(R.id.window_view);
        ResizeView resizeView = window.findViewById(R.id.resize_view);
        windowView.setWindowListener(this);
        resizeView.setWindowListener(this);
        GestureDetectorCompat detectorCompat = new GestureDetectorCompat(context, this);
        detectorCompat.setOnDoubleTapListener(this);
        windowView.setDetector(detectorCompat);

        RelativeLayout relativeLayout = window.findViewById(R.id.content_view);
        relativeLayout.addView(inflater.inflate(contentView, relativeLayout, false));

        // Hacky way to check if we are fullscreen by inserting a dummy view and seeing if
        // realDisplaySize matches this view's height. Also determines the drawable view size.
        WindowManager.LayoutParams heightViewParams = new WindowManager.LayoutParams();
        heightViewParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        heightViewParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        heightViewParams.type = Build.VERSION.SDK_INT > 25 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        heightViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        heightViewParams.format = PixelFormat.TRANSPARENT;
        heightViewParams.gravity = Gravity.END | Gravity.TOP;
        heightViewParams.alpha = 0.0F;
        mDummyViewForSize = new View(context);
        mDummyViewForSize.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                mViewHeight = mDummyViewForSize.getMeasuredHeight();
                mViewWidth = mDummyViewForSize.getMeasuredWidth();
            }
        });

        windowManager.addView(mDummyViewForSize, heightViewParams);
    }

    public void reInit(ReinitOptions options)
    {
        Log.d(TAG, String.format("Window.reInit() for %s called", this.getClass()));

        synchronized (this)
        {
            mRealDisplaySize = getRealDisplaySizeFromContext();

            if (options.reinitViewLayout)
            {
                fixBoxBounds();

                if (addedToWindowManager){
                    windowManager.updateViewLayout(window, params);
                }
            }
        }
    }

    private Point getRealDisplaySizeFromContext()
    {
        Point displaySize = new Point();
        ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealSize(displaySize);
        return displaySize;
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
    public void stop()
    {
        Log.d(TAG, String.format("Window.stop() for %s called", this.getClass()));

        synchronized (this)
        {
            if (mWindowClosed) return;

            for (ViewTreeObserver.OnGlobalLayoutListener listener : mOnHeightKnownListeners)
            {
                mDummyViewForSize.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            }

            mWindowClosed = true;
            windowManager.removeView(mDummyViewForSize);
            windowCoordinator.removeWindow(this);
            if (addedToWindowManager) windowManager.removeView(window);

            context = null;
            windowCoordinator = null;
        }
    }

    public void show()
    {
        Log.d(TAG, String.format("Window.show() for %s called, %b", this.getClass(), addedToWindowManager));

        synchronized (this)
        {
            if (!addedToWindowManager)
            {
                windowManager.addView(window, params);
                addedToWindowManager = true;
            }

            windowManager.updateViewLayout(window, params);
        }
    }

    public void hide()
    {
        Log.d(TAG, String.format("Window.hide() for %s called, %b", this.getClass(), addedToWindowManager));

        synchronized (this)
        {
            if (addedToWindowManager)
            {
                windowManager.removeView(window);
                addedToWindowManager = false;
            }
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
                windowManager.updateViewLayout(window, params);
                onUp(e);
                return true;
        }
        return false;
    }

    /**
     * See {@link #onTouch}
     */
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

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    public boolean onUp(MotionEvent e){ return false; }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public void onShowPress(MotionEvent e) {
    }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public void onLongPress(MotionEvent e) {
    }

    /**
     * Override if your implementing Window needs to deal with this touch event
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * Override this if implementing Window does not need to resize.
     *
     * Overriding {@link #onTouch} will NOT prevent this event from being triggered as
     * it is bring triggered from another view (the resize view) at the current moment.
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
                windowManager.updateViewLayout(window, params);
                onUp(e);
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
     * Some Windows requires the drawable view and status bar height to be known so they can position themselves appropriately
     * Set handler here if that is the case for that window
     */
    public void setOnHeightKnownAction(final OnHeightKnown onHeightKnown)
    {
        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                onHeightKnown.performAction();
            }
        };

        mDummyViewForSize.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        mOnHeightKnownListeners.add(listener);
    }

    /**
     * @return Default LayoutParams for Window
     */
    protected WindowManager.LayoutParams getDefaultParams(){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = KakuTools.dpToPx(context, 150);
        params.height = KakuTools.dpToPx(context, 150);
        params.type = Build.VERSION.SDK_INT > 25 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.alpha = 0.8F;
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    /**
     * @return Real screen display size
     */
    protected Point getRealDisplaySize()
    {
        return new Point(mRealDisplaySize);
    }

    /**
     * @return System status bar height in pixels. Note that the View MUST have been drawn for this to have any meaning!
     */
    protected int getStatusBarHeight()
    {
        Log.d(TAG, String.format("Window - getStatusBarHeight %d vs %d", mRealDisplaySize.y, mViewHeight));

        if (mRealDisplaySize.y == mViewHeight)
        {
            return 0;
        }

        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    /**
     * @return The height of portions of the screen the view can be draw on. Note that the View MUST have been drawn for this to have any meaning!
     */
    protected int getViewHeight(){
        return mViewHeight;
    }

    /**
     * @return The width of portions of the screen the view can be draw on. Note that the View MUST have been drawn for this to have any meaning!
     */
    protected int getViewWidth(){
        return mViewWidth;
    }

    /**
     * Fixes window so that it stays inside the screen even if the user is trying to drag it off screen
     * Also makes sure that the window size is not smaller than a specified value
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
        if (params.width < minSize){
            params.width = minSize;
        }
        if (params.height < minSize){
            params.height = minSize;
        }
    }
}

