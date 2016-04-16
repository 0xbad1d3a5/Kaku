package ca.fuwafuwa.kaku;

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

    public CaptureWindow(MainService context) {
        super(context);

        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String storagePath = mContext.getExternalFilesDir(null).getAbsolutePath();
        Log.e(TAG, storagePath);
        tessBaseAPI.init(storagePath, "jpn");

        ((WindowView) mWindow.findViewById(R.id.capture_window)).registerCallback(this);
        ((ResizeView) mWindow.findViewById(R.id.resize_box)).registerCallback(this);

        tesseractThread = new TesseractThread(mContext, tessBaseAPI);
        (new Thread(tesseractThread)).start();
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
                //new ScreenshotAndOcrAsyncTask(mContext, tessBaseAPI, params.x, params.y, params.width, params.height).execute();
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
