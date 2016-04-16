package ca.fuwafuwa.kaku;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileOutputStream;

/**
 * Created by Xyresic on 4/14/2016.
 */
public class ScreenshotAndOcrAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private String TAG = this.getClass().getName();

    MainService mContext;
    TessBaseAPI tessBaseAPI;

    private final int iX;
    private final int iY;
    private final int width;
    private final int height;

    public ScreenshotAndOcrAsyncTask(MainService context, TessBaseAPI tessBaseAPI,
                                     int iX, int iY, int width, int height){
        this.mContext = context;
        this.tessBaseAPI = tessBaseAPI;

        this.iX = iX;
        this.iY = iY;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        long startTime = System.currentTimeMillis();
        Bitmap bitmap = getReadyScreenshot();
        long screenTime = System.currentTimeMillis();

        tessBaseAPI.setImage(bitmap);
        String text = tessBaseAPI.getUTF8Text();
        tessBaseAPI.clear();

        Message m = Message.obtain(mContext.getHandler(), 0, text + String.format("\nScreenshot Time:%d\nOCR Time: %d", screenTime - startTime, System.currentTimeMillis() - screenTime));
        m.sendToTarget();

        try {
            FileOutputStream fos = new FileOutputStream(mContext.getExternalFilesDir(null).getAbsolutePath() + String.format("/screenshots/screen %d.png", System.nanoTime()));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Unable to write file");
        }

        return null;
    }
    private Bitmap getReadyScreenshot(){
        Bitmap bitmap = mContext.getScreenshot();
        Log.d(TAG, String.format("X: %d Y: %d\nWidth: %d Height: %d", iX, iY + getStatusBarHeight(), width, height));
        bitmap = bitmap.createBitmap(bitmap, iX, iY + getStatusBarHeight(), width, height);
        Log.d(TAG, String.format("Pixel Identifiers:\n%d\n%d\n%d\n%d\n#%06X\n#%06X\n#%06X\n#%06X",
                bitmap.getPixel(0,0),
                bitmap.getPixel(1,0),
                bitmap.getPixel(2,0),
                bitmap.getPixel(3,0),
                bitmap.getPixel(4,0),
                bitmap.getPixel(5,0),
                bitmap.getPixel(6,0),
                bitmap.getPixel(7,0)));
        if (bitmap.getPixel(1, 0) != ContextCompat.getColor(mContext, R.color.red)){
            try{
                Thread.sleep(100);
            }
            catch(Exception e){
            }

            return getReadyScreenshot();
        }
        return bitmap;
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
