package ca.fuwafuwa.kaku;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class CaptureWindow extends Window implements CaptureWindowCallback  {

    private static final String TAG = CaptureWindow.class.getName();

    private TesseractThread tesseractThread;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    private Point displaySize;

    public CaptureWindow(MainService context) {
        super(context);

        displaySize = mContext.getDisplaySize();

        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String storagePath = mContext.getExternalFilesDir(null).getAbsolutePath();
        Log.e(TAG, storagePath);
        tessBaseAPI.init(storagePath, "jpn");

        ((WindowView) mWindow.findViewById(R.id.capture_window)).registerCallback(this);
        ((ResizeView) mWindow.findViewById(R.id.resize_box)).registerCallback(this);

        tesseractThread = new TesseractThread(mContext, tessBaseAPI);
        (new Thread(tesseractThread)).start();
    }

    @Override
    public boolean onMoveEvent(MotionEvent e) {
        setOpacity(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = params.x;
                initialY = params.y;
                initialTouchX = e.getRawX();
                initialTouchY = e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                fixBoxBounds();
                tesseractThread.runTess(new BoxParams(params.x, params.y, params.width, params.height));
                return true;
            case MotionEvent.ACTION_MOVE:
                params.x = initialX + (int) (e.getRawX() - initialTouchX);
                params.y = initialY + (int) (e.getRawY() - initialTouchY);
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
                initialX = params.width;
                initialY = params.height;
                initialTouchX = e.getRawX();
                initialTouchY = e.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                fixBoxBounds();
                tesseractThread.runTess(new BoxParams(params.x, params.y, params.width, params.height));
                return true;
            case MotionEvent.ACTION_MOVE:
                params.width = initialX + (int) (e.getRawX() - initialTouchX);
                params.height = initialY + (int) (e.getRawY() - initialTouchY);
                mWindowManager.updateViewLayout(mWindow, params);
                return true;
        }
        return true;
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
        params.y += getStatusBarHeight();
    }

    private void setOpacity(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindow.findViewById(R.id.capture_box).setBackground(mContext.getResources().getDrawable(R.drawable.border_translucent, null));
                break;
            case MotionEvent.ACTION_UP:
                mWindow.findViewById(R.id.capture_box).setBackground(mContext.getResources().getDrawable(R.drawable.border9patch_transparent, null));
                break;
        }
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
