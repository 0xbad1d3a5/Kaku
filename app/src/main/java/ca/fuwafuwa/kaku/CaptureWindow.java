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

    private int dX;
    private int dY;

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
        if (params.width < 100){
            params.width = 100;
        }
        if (params.height < 100){
            params.height = 100;
        }
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
