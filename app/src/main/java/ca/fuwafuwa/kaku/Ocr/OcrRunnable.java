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
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr;
import ca.fuwafuwa.kaku.Windows.Data.SquareCharOcr;

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
    private OcrParams mOcrParams;
    private Object mOcrLock = new Object();

    public OcrRunnable(Context context, CaptureWindow captureWindow){
        mContext = (MainService) context;
        mCaptureWindow = captureWindow;
        mOcrParams = null;
    }

    @Override
    public void run()
    {
        mTessBaseAPI = new TessBaseAPI();
        String storagePath = mContext.getFilesDir().getAbsolutePath();
        mTessBaseAPI.init(storagePath, "jpn");

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

                    switch (mOcrParams.getTextDirection())
                    {
                        case HORIZONTAL:
                            mTessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
                            break;
                        case VERTICAL:
                            mTessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT);
                            break;
                    }

                    saveBitmap(mOcrParams.getBitmap());

                    mCaptureWindow.showLoadingAnimation();

                    mTessBaseAPI.setImage(mOcrParams.getBitmap());
                    mTessBaseAPI.getHOCRText(0);
                    DisplayDataOcr displayData = getDisplayData(mOcrParams, mTessBaseAPI.getResultIterator());
                    mTessBaseAPI.clear();

                    if (displayData.getText().length() > 0)
                    {
                        long ocrTime = System.currentTimeMillis() - startTime;
                        sendOcrResultToContext(new OcrResult(displayData, ocrTime));
                    } else
                    {
                        sendToastToContext("No Characters Recognized.");
                    }

                    mCaptureWindow.stopLoadingAnimation(mOcrParams.getInstantMode());

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
            mCaptureWindow = null;
            if (mTessBaseAPI != null){
                mTessBaseAPI.stop();
                mTessBaseAPI.end();
            }
            mOcrLock.notify();
        }
    }

    private DisplayDataOcr getDisplayData(OcrParams ocrParams, ResultIterator iterator)
    {
        Bitmap bitmap = mOcrParams.getOriginalBitmap();
        BoxParams boxParams = mOcrParams.getBox();

        List<SquareCharOcr> ocrChars = new ArrayList<>();
        DisplayDataOcr displayData = new DisplayDataOcr(bitmap, boxParams, ocrParams.getInstantMode(), ocrChars);

        if (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL)){
            iterator.begin();
        }
        else {
            return displayData;
        }

        do {
            List<Pair<String, Double>> c = iterator.getSymbolChoicesAndConfidence();
            List<kotlin.Pair<String, Double>> choices = new ArrayList<>();
            for (Pair p : c) choices.add(new kotlin.Pair<>((String)p.first, (Double)p.second));
            int[] pos = iterator.getBoundingBox(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL);

            ocrChars.add(new SquareCharOcr(displayData, choices, pos));
        } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL));

        iterator.delete();

        displayData.assignIndicies();

        return displayData;
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
        String fs = String.format("%s/%s/%s_%d.png", mContext.getFilesDir().getAbsolutePath(), Constants.SCREENSHOT_FOLDER_NAME, name, System.nanoTime());
        Log.d(TAG, fs);
        FileOutputStream fos = new FileOutputStream(fs);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    }
}
