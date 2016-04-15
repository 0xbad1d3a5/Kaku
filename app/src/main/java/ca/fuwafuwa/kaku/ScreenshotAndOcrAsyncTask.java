package ca.fuwafuwa.kaku;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Xyresic on 4/14/2016.
 */
public class ScreenshotAndOcrAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private String TAG = this.getClass().getName();

    MainService mContext;
    TessBaseAPI tessBaseAPI;
    Bitmap bitmap;

    private final int iX;
    private final int iY;
    private final int width;
    private final int height;

    public ScreenshotAndOcrAsyncTask(MainService context, TessBaseAPI tessBaseAPI, Bitmap bitmap,
                                     int iX, int iY, int width, int height){
        this.mContext = context;
        this.tessBaseAPI = tessBaseAPI;
        this.bitmap = bitmap;

        this.iX = iX;
        this.iY = iY;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        bitmap = bitmap.createBitmap(bitmap, iX, iY + getStatusBarHeight(), width, height);

        tessBaseAPI.setImage(bitmap);
        String text = tessBaseAPI.getUTF8Text();
        Log.e(TAG, text);
        tessBaseAPI.clear();

        Message m = Message.obtain(mContext.getHandler(), 0, text);
        m.sendToTarget();

        return null;
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
