package ca.fuwafuwa.kaku.Ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.Constants;
import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Windows.CaptureWindow;
import ca.fuwafuwa.kaku.Windows.InstantWindow;

/**
 * Created by 0xbad1d3a5 on 4/16/2016.
 */
public class OcrRunnable implements Runnable, Stoppable {

    private static final String TAG = OcrRunnable.class.getName();

    private MainService mContext;
    private CaptureWindow mCaptureWindow;
    private TessBaseAPI mTessBaseAPI;
    private boolean mThreadRunning = true;
    private boolean mTessReady = false;
    private boolean mHorizontalText;
    private OcrParams mOcrParams;
    private Object mOcrLock = new Object();

    public OcrRunnable(Context context, CaptureWindow captureWindow, boolean horizontalText){
        mContext = (MainService) context;
        mCaptureWindow = captureWindow;
        mOcrParams = null;
        mHorizontalText = horizontalText;
    }

    @Override
    public void run()
    {
        mTessBaseAPI = new TessBaseAPI();
        String storagePath = mContext.getExternalFilesDir(null).getAbsolutePath();
        mTessBaseAPI.init(storagePath, "jpn");

        if (!mHorizontalText)
        {
            mTessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT);
        }

        mTessReady = true;

        while(mThreadRunning)
        {
            Log.d(TAG, "THREAD STARTING NEW LOOP");

            try
            {
                synchronized (mOcrLock)
                {
                    if (!mThreadRunning){
                        break;
                    }

                    Log.d(TAG, "WAITING");
                    mOcrLock.wait();
                    Log.d(TAG, "THREAD STOPPED WAITING");

                    if (mOcrParams == null)
                    {
                        Log.d(TAG, "OcrRunnable - OcrParams null");
                        continue;
                    }

                    Log.d(TAG, "Processing OCR with params " + mOcrParams.toString());

                    long startTime = System.currentTimeMillis();

                    saveBitmap(mOcrParams.getBitmap());

                    mCaptureWindow.showLoadingAnimation();

                    mTessBaseAPI.setImage(mOcrParams.getBitmap());
                    mTessBaseAPI.getHOCRText(0);
                    List<OcrChar> ocrChars = processOcrIterator(mTessBaseAPI.getResultIterator());
                    mTessBaseAPI.clear();

                    if (ocrChars.size() > 0)
                    {
                        long ocrTime = System.currentTimeMillis() - startTime;
                        if (mOcrParams.getInstantOcr())
                        {
                            sendOcrResultToContext(new OcrResult(mOcrParams.getOriginalBitmap(), mOcrParams.getBox(), ocrChars, true, mCaptureWindow, ocrTime));
                        }
                        else {
                            sendOcrResultToContext(new OcrResult(mOcrParams.getOriginalBitmap(), mOcrParams.getBox(), ocrChars,false, mCaptureWindow, ocrTime));
                        }
                    } else
                    {
                        sendToastToContext("No Characters Recognized.");
                    }

                    mCaptureWindow.stopLoadingAnimation(mOcrParams.getInstantOcr());

                    mOcrParams = null;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "THREAD STOPPED");
    }

    /**
     * Unblocks the thread and starts OCR
     */
    public void runTess(OcrParams ocrParams)
    {
        synchronized (mOcrLock)
        {
            if (!mThreadRunning || !mTessReady)
            {
                return;
            }

            mOcrParams = ocrParams;
            mTessBaseAPI.stop();
            mOcrLock.notify();

            Log.d(TAG, "NOTIFIED");
        }
    }

    public boolean isReadyForOcr(){
        return mOcrParams == null;
    }

    /**
     * Cancels OCR recognition in progress if Tesseract has been started
     */
    public void cancel()
    {
        mTessBaseAPI.stop();
        Log.d(TAG, "CANCELED");
    }

    /**
     * Cancels any OCR recognition in progress and stops any further OCR attempts
     */
    @Override
    public void stop()
    {
        synchronized (mOcrLock)
        {
            mThreadRunning = false;
            mOcrParams = null;
            if (mTessBaseAPI != null){
                mTessBaseAPI.stop();
            }
            mOcrLock.notify();
        }
    }

    private List<OcrChar> processOcrIterator(ResultIterator iterator){

        List<OcrChar> ocrChars = new ArrayList<>();

        if (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL)){
            iterator.begin();
        }
        else {
            return ocrChars;
        }

        do {
            List<Pair<String, Double>> choices = iterator.getSymbolChoicesAndConfidence();
            int[] pos = iterator.getBoundingBox(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL);
            ocrChars.add(new OcrChar(choices, pos));
        } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL));

        iterator.delete();

        return ocrChars;
    }

    private void sendOcrResultToContext(OcrResult result){
        Message.obtain(mContext.getHandler(), 0, result).sendToTarget();
    }

    private void sendToastToContext(String message){
        Message.obtain(mContext.getHandler(), 0, message).sendToTarget();
    }

    private void saveBitmap(Bitmap bitmap) throws FileNotFoundException {
        saveBitmap(bitmap, "screen");
    }

    private void saveBitmap(Bitmap bitmap, String name) throws FileNotFoundException {
        String fs = String.format("%s/%s/%s_%d.png", mContext.getExternalFilesDir(null).getAbsolutePath(), Constants.SCREENSHOT_FOLDER_NAME, name, System.nanoTime());
        Log.d(TAG, fs);
        FileOutputStream fos = new FileOutputStream(fs);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    }
}
