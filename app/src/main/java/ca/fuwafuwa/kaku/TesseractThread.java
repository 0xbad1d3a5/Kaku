package ca.fuwafuwa.kaku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Xyresic on 4/16/2016.
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
                e.printStackTrace();
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

    private Bitmap getReadyScreenshot(BoxParams box) {
        Bitmap bitmapOriginal = convertImageToBitmap(mContext.getScreenshot());

        Log.d(TAG, String.format("X:%d Y:%d (%dx%d)", box.x, box.y, box.width, box.height));

        Bitmap borderPattern = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.border_pattern);
        Bitmap croppedPattern = Bitmap.createBitmap(bitmapOriginal, box.x, box.y, 8, 1);
        if (!croppedPattern.sameAs(borderPattern)){
            if (!box.equals(mBox)){
                return getReadyScreenshot(mBox);
            }
            return getReadyScreenshot(box);
        }

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmapOriginal, box.x, box.y, box.width, box.height);
        return croppedBitmap;
    }

    private Bitmap convertImageToBitmap(Image image){
        Log.d(TAG, String.format("Image Dimensions: %dx%d", image.getWidth(), image.getHeight()));

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * image.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();

        return bitmap;
    }
}
