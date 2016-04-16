package ca.fuwafuwa.kaku;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class CaptureWindow extends Window implements CaptureWindowCallback  {

    private static final String TAG = CaptureWindow.class.getName();

    TessBaseAPI tessBaseAPI;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    Boolean captureScreen = false;

    public CaptureWindow(MainService context) {
        super(context);

        tessBaseAPI = new TessBaseAPI();
        String storagePath = mContext.getExternalFilesDir(null).getAbsolutePath();
        Log.e(TAG, storagePath);
        tessBaseAPI.init(storagePath, "jpn");

        ((WindowView) mWindow.findViewById(R.id.capture_window)).registerCallback(this);
        ((ResizeView) mWindow.findViewById(R.id.resize_box)).registerCallback(this);
    }

    private void setOpacity(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindow.findViewById(R.id.capture_box).setBackground(mContext.getResources().getDrawable(R.drawable.border_translucent, null));
                captureScreen = false;
                break;
            case MotionEvent.ACTION_UP:
                mWindow.findViewById(R.id.capture_box).setBackground(mContext.getResources().getDrawable(R.drawable.border9patch_transparent, null));
                captureScreen = true;
                break;
        }
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
                new ScreenshotAndOcrAsyncTask(mContext, tessBaseAPI, params.x, params.y, params.width, params.height).execute();
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
                return true;
            case MotionEvent.ACTION_MOVE:
                params.width = initialX + (int) (e.getRawX() - initialTouchX);
                params.height = initialY + (int) (e.getRawY() - initialTouchY);
                mWindowManager.updateViewLayout(mWindow, params);
                return true;
        }
        return true;
    }
}
