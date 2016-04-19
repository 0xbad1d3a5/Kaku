package ca.fuwafuwa.kaku;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class CaptureWindow extends Window implements CaptureWindowCallback  {

    private static final String TAG = CaptureWindow.class.getName();

    private TesseractThread tesseractThread;
    private WindowView windowView;
    private ResizeView resizeView;
    private View windowBox;
    private Animation fadeRepeat;
    private Drawable borderTranslucent;
    private Drawable border9PatchTransparent;

    private int dX;
    private int dY;

    private long paramUpdateTimer = System.currentTimeMillis();
    private Point displaySize;

    public CaptureWindow(MainService context) {
        super(context);

        displaySize = mContext.getDisplaySize();

        windowView = (WindowView) mWindow.findViewById(R.id.capture_window);
        resizeView = (ResizeView) mWindow.findViewById(R.id.resize_box);
        windowBox = mWindow.findViewById(R.id.capture_box);

        fadeRepeat = AnimationUtils.loadAnimation(mContext, R.anim.fade_repeat);
        borderTranslucent = mContext.getResources().getDrawable(R.drawable.border_translucent, null);
        border9PatchTransparent = mContext.getResources().getDrawable(R.drawable.border9patch_transparent, null);

        windowView.registerCallback(this);
        resizeView.registerCallback(this);

        tesseractThread = new TesseractThread(mContext, this);
        (new Thread(tesseractThread)).start();
    }

    @Override
    public boolean onMoveEvent(MotionEvent e) {
        setOpacity(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = params.x - (int) e.getRawX();
                dY = params.y - (int) e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                tesseractThread.runTess(new BoxParams(params.x, params.y + getStatusBarHeight(), params.width, params.height));
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

    @Override
    public boolean onResizeEvent(MotionEvent e) {
        setOpacity(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = params.width - (int) e.getRawX();
                dY = params.height - (int) e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                tesseractThread.runTess(new BoxParams(params.x, params.y + getStatusBarHeight(), params.width, params.height));
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
        return true;
    }

    public void showLoadingAnimation(){
        mContext.getHandler().post(new Runnable() {
            @Override
            public void run() {
                windowBox.setAnimation(fadeRepeat);
                windowBox.startAnimation(fadeRepeat);
                windowBox.setBackground(borderTranslucent);
            }
        });
    }

    public void stopLoadingAnimation(){
        mContext.getHandler().post(new Runnable() {
            @Override
            public void run() {
                windowBox.setBackground(border9PatchTransparent);
                windowBox.clearAnimation();
            }
        });
    }

    private void setOpacity(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                windowBox.setBackground(borderTranslucent);
                break;
            case MotionEvent.ACTION_UP:
                windowBox.setBackground(border9PatchTransparent);
                break;
        }
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
            Log.d(TAG, "PARAMS Y: " + params.y);
            params.y = displaySize.y - params.height - getStatusBarHeight();
        }
        if (params.width < 100){
            params.width = 100;
        }
        if (params.height < 100){
            params.height = 100;
        }
    }

    private int getNavigationBarHeight(){
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0){
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
