package ca.fuwafuwa.kaku;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Message;
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
    private CaptureWindow mCaptureWindow;
    private TessBaseAPI mTessBaseAPI;
    private boolean running = true;
    private BoxParams mBox;
    private Object mBoxLock = new Object();

    public TesseractThread(MainService context, CaptureWindow captureWindow){
        mContext = context;
        mCaptureWindow = captureWindow;
        mBox = null;

        mTessBaseAPI = new TessBaseAPI();
        String storagePath = mContext.getExternalFilesDir(null).getAbsolutePath();
        Log.e(TAG, storagePath);
        mTessBaseAPI.init(storagePath, "jpn");
    }

    @Override
    public void run() {
        while(running){

            Log.d(TAG, "THREAD STARTING NEW LOOP");

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

                mCaptureWindow.showLoadingAnimation();
                
                mTessBaseAPI.setImage(bitmap);
                String text = mTessBaseAPI.getUTF8Text();
                mTessBaseAPI.clear();

                if (text != null){
                    Message m = Message.obtain(mContext.getHandler(), 0, text + String.format("\nScreenshot Time:%d\nOCR Time: %d", screenTime - startTime, System.currentTimeMillis() - screenTime));
                    m.sendToTarget();
                    mBox = null;
                }

                mCaptureWindow.stopLoadingAnimation();

                String fs = String.format("%s/screenshots/screen %d.png", mContext.getExternalFilesDir(null).getAbsolutePath(), System.nanoTime());
                Log.d(TAG, fs);
                FileOutputStream fos = new FileOutputStream(fs);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                bitmap.recycle();
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
            catch (OutOfMemoryError e){
                e.printStackTrace();
            }
            catch (StackOverflowError e){
                e.printStackTrace();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        Log.d(TAG, "THREAD STOPPED");
        running = false;
    }

    public void runTess(BoxParams box){
        synchronized (mBoxLock){
            mBox = box;
            mTessBaseAPI.stop();
            mBoxLock.notify();
            Log.d(TAG, "NOTIFIED");
        }
    }

    private Bitmap getReadyScreenshot(BoxParams box) throws OutOfMemoryError, StackOverflowError {
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
        bitmapOriginal.recycle();
        return croppedBitmap;
    }

    private Bitmap convertImageToBitmap(Image image) throws OutOfMemoryError {
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
