package ca.fuwafuwa.kaku.Ocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Message;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.CaptureWindow;

/**
 * Created by 0x1bad1d3a on 4/16/2016.
 */
public class TesseractThread implements Runnable, Stoppable {

    private static final String TAG = TesseractThread.class.getName();

    private MainService mContext;
    private CaptureWindow mCaptureWindow;
    private TessBaseAPI mTessBaseAPI;
    private boolean mRunning = true;
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
        while(mRunning){

            Log.d(TAG, "THREAD STARTING NEW LOOP");

            try {
                if (mBox == null){
                    synchronized (mBoxLock){
                        Log.d(TAG, "WAITING");
                        mBoxLock.wait();
                        if (mBox == null){
                            continue;
                        }
                    }
                }

                Log.d(TAG, "THREAD STOPPED WAITING");

                long startTime = System.currentTimeMillis();
                Bitmap bitmap = getReadyScreenshotBox(mBox, 0);
                long screenTime = System.currentTimeMillis();

                if (bitmap == null){
                    sendMessageToMainThread(new OcrResult("Error getting image", screenTime - startTime, 0));
                    mBox = null;
                    continue;
                }

                mCaptureWindow.showLoadingAnimation();

                mTessBaseAPI.setImage(bitmap);
                mTessBaseAPI.getHOCRText(0);
                String text = mTessBaseAPI.getUTF8Text();
                text = text.replaceAll("\\s+", "");
                mTessBaseAPI.clear();

                if (text != null){
                    sendMessageToMainThread(new OcrResult(text, screenTime - startTime, System.currentTimeMillis() - screenTime));
                }

                mBox = null;
                mCaptureWindow.stopLoadingAnimation();
                saveBitmap(bitmap);
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
            catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public void runTess(BoxParams box){
        synchronized (mBoxLock){
            mBox = box;
            mTessBaseAPI.stop();
            mBoxLock.notify();
            Log.d(TAG, "NOTIFIED");
        }
    }

    public void cancel(){
        mTessBaseAPI.stop();
        Log.d(TAG, "CANCELED");
    }

    @Override
    public void stop(){
        synchronized (mBoxLock){
            mRunning = false;
            cancel();
            Log.d(TAG, "THREAD STOPPED");
        }
    }

    private Bitmap getReadyScreenshotBox(BoxParams box, int attempts) throws OutOfMemoryError, StackOverflowError, TimeoutException, FileNotFoundException {

        if (attempts > 10){
            return null;
        }

        Log.d(TAG, String.format("X:%d Y:%d (%dx%d)", box.x, box.y, box.width, box.height));
        Bitmap bitmapOriginal = convertImageToBitmap(mContext.getScreenshot());
        //saveBitmap(bitmapOriginal);

        Bitmap borderPattern = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.border_pattern);
        Bitmap croppedPattern = Bitmap.createBitmap(bitmapOriginal, box.x, box.y, 8, 1);
        if (!croppedPattern.sameAs(borderPattern)){
            bitmapOriginal.recycle();
            if (!box.equals(mBox)){
                return getReadyScreenshotBox(mBox, attempts + 1);
            }
            return getReadyScreenshotBox(box, attempts + 1);
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

    private void saveBitmap(Bitmap bitmap) throws FileNotFoundException {
        String fs = String.format("%s/screenshots/screen %d.png", mContext.getExternalFilesDir(null).getAbsolutePath(), System.nanoTime());
        Log.d(TAG, fs);
        FileOutputStream fos = new FileOutputStream(fs);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    }

    private void sendMessageToMainThread(OcrResult result){
        Message m = Message.obtain(mContext.getHandler(), 0, result);
        m.sendToTarget();
    }
}
