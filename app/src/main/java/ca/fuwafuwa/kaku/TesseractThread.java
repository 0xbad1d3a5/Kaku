package ca.fuwafuwa.kaku;

import android.graphics.Bitmap;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by 0x1bad1d3a on 4/16/2016.
 */
public class TesseractThread implements Runnable {

    private static final String TAG = TesseractThread.class.getName();

    private MainService mContext;
    private TessBaseAPI mTessBaseAPI;
    private BoxParams mBox;

    private Object mBoxLock = new Object();

    public TesseractThread(MainService context, TessBaseAPI tessBaseAPI){
        mContext = context;
        mTessBaseAPI = tessBaseAPI;
        mBox = null;
    }

    @Override
    public void run() {
        while(true){
            try {
                if (mBox == null){
                    synchronized (mBoxLock){
                        Log.d(TAG, "WAITING");
                        mBoxLock.wait();
                    }
                }

                long startTime = System.currentTimeMillis();
                Bitmap bitmap = getReadyScreenshot(mBox);
                long screenTime = System.currentTimeMillis();

                mTessBaseAPI.setImage(bitmap);
                String text = mTessBaseAPI.getUTF8Text();
                mTessBaseAPI.clear();

                Message m = Message.obtain(mContext.getHandler(), 0, text + String.format("\nScreenshot Time:%d\nOCR Time: %d", screenTime - startTime, System.currentTimeMillis() - screenTime));
                m.sendToTarget();

                FileOutputStream fos = new FileOutputStream(mContext.getExternalFilesDir(null).getAbsolutePath() + String.format("/screenshots/screen %d.png", System.nanoTime()));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
                Log.e(TAG, "Unable to write file");
            }
            catch (InterruptedException e){

            }
            finally {
                mBox = null;
            }
        }
    }

    public void runTess(BoxParams box){
        synchronized (mBoxLock){
            mBox = box;
            mBoxLock.notify();
            Log.d(TAG, "NOTIFIED");
        }
    }

    private Bitmap getReadyScreenshot(BoxParams box){
        Bitmap bitmap = mContext.getScreenshot();
        Log.d(TAG, String.format("X: %d Y: %d\nWidth: %d Height: %d", box.x, box.y + getStatusBarHeight(), box.width, box.height));
        bitmap = bitmap.createBitmap(bitmap, box.x, box.y + getStatusBarHeight(), box.width, box.height);
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
            if (!box.equals(mBox)){
                return getReadyScreenshot(mBox);
            }
            return getReadyScreenshot(box);
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
