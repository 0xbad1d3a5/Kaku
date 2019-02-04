package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import androidx.core.content.ContextCompat;
import ca.fuwafuwa.kaku.Constants;
import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.BoxParams;
import ca.fuwafuwa.kaku.Ocr.OcrParams;
import ca.fuwafuwa.kaku.Ocr.OcrRunnable;
import ca.fuwafuwa.kaku.Prefs;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.TextDirection;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;
import ca.fuwafuwa.kaku.XmlParsers.CommonParser;

/**
 * Created by 0xbad1d3a5 on 4/13/2016.
 */
public class CaptureWindow extends Window implements WindowListener
{
    private class ScreenshotForOcr
    {
        public final Bitmap crop;
        public final Bitmap orig;
        public final BoxParams params;

        private Bitmap mCropProcessed;
        private int mSetThreshold;


        public ScreenshotForOcr(Bitmap crop, Bitmap orig, BoxParams params, int defaultThreshold){
            this.crop = crop;
            this.orig = orig;
            this.params = params;
            this.mSetThreshold = defaultThreshold;
            this.mCropProcessed = null;
        }

        public Bitmap getCachedScreenshot()
        {
            if (mCropProcessed == null)
            {
                mCropProcessed = getProcessedScreenshot(mSetThreshold);
            }

            return mCropProcessed;
        }

        public Bitmap getProcessedScreenshot(int threshold)
        {
            Pix pix = ReadFile.readBitmap(crop);
            Pix pixT = GrayQuant.pixThresholdToBinary(pix, threshold);

            Bitmap binarizedBitmap = WriteFile.writeBitmap(pixT);

            pix.recycle();
            pixT.recycle();

            if (mCropProcessed != null) mCropProcessed.recycle();
            mCropProcessed = binarizedBitmap;
            mSetThreshold = threshold;

            return binarizedBitmap;
        }

    }

    private static final String TAG = CaptureWindow.class.getName();

    private OcrRunnable mOcr;
    private View mWindowBox;
    private ImageView mImageView;
    private Animation mFadeRepeat;
    private Drawable mBorderDefault;
    private Drawable mBorderReady;

    private Prefs mPrefs;
    private int mThreshold;
    private long mLastDoubleTapTime;
    private long mLastDoubleTapIgnoreDelay;
    private boolean mInLongPress;
    private boolean mProcessingPreview;
    private boolean mProcessingOcr;
    private ScreenshotForOcr mScreenshotForOcr;

    private CommonParser mCommonParser;

    public CaptureWindow(final Context context, WindowCoordinator windowCoordinator)
    {
        super(context, windowCoordinator, R.layout.window_capture);
        show();

        this.mCommonParser = new CommonParser(context);

        mImageView = window.findViewById(R.id.capture_image);
        mFadeRepeat = AnimationUtils.loadAnimation(this.context, R.anim.fade_repeat);
        mBorderDefault = this.context.getResources().getDrawable(R.drawable.bg_translucent_border_0_blue_blue, null);
        mBorderReady = this.context.getResources().getDrawable(R.drawable.bg_transparent_border_0_nil_ready, null);

        mThreshold = 128;
        mLastDoubleTapTime = System.currentTimeMillis();
        mLastDoubleTapIgnoreDelay = 500;
        mInLongPress = false;
        mProcessingPreview = false;
        mProcessingOcr = false;
        mScreenshotForOcr = null;

        mPrefs = KakuTools.getPrefs(context);

        mOcr = new OcrRunnable(this.context, this);
        Thread tessThread = new Thread(mOcr);
        tessThread.setName(String.format("TessThread%d", System.nanoTime()));
        tessThread.setDaemon(true);
        tessThread.start();

        windowManager.getDefaultDisplay().getRotation();

        // Need to wait for the view to finish updating before we try to determine it's location
        mWindowBox = window.findViewById(R.id.capture_box);
        mWindowBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((MainService)context).onCaptureWindowFinishedInitializing();
                mWindowBox.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void reInit(ReinitOptions options)
    {
        mPrefs = KakuTools.getPrefs(context);
        super.reInit(options);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e)
    {
        Log.d(TAG, "onDoubleTap");

        mLastDoubleTapTime = System.currentTimeMillis();
        performOcr(false);

//        try {
//            mCommonParser.parseJmDict();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }

        return true;
    }

    @Override
    public boolean onTouch(MotionEvent e)
    {
        hideInstantWindows();

        if (!mInLongPress && !mProcessingOcr){
            mImageView.setImageResource(0);
            setBorderStyle(e);
        }

        switch (e.getAction())
        {
            case MotionEvent.ACTION_MOVE:

                Log.d(TAG, "onTouch - Move");

                if (System.currentTimeMillis() > mLastDoubleTapTime + mLastDoubleTapIgnoreDelay)
                {
                    mOcr.cancel();
                }

                if (mInLongPress && mPrefs.getImageFilterSetting()){
                    setPreviewImageForThreshold(e);
                }
        }

        return super.onTouch(e);
    }

    @Override
    public void onLongPress(MotionEvent e)
    {
        Log.d(TAG, "onLongPress");

        mInLongPress = true;
        setPreviewImageForThreshold(e);
    }

    @Override
    public boolean onResize(MotionEvent e)
    {
        hideInstantWindows();

        mOcr.cancel();
        mImageView.setImageResource(0);
        setBorderStyle(e);
        return super.onResize(e);
    }

    @Override
    public boolean onUp(MotionEvent e){

        Log.d(TAG, String.format("onUp - mImageFilterSetting: %b | mInLongPress: %b | mProcessingPreview: %b | mProcessingOcr: %b", mPrefs.getImageFilterSetting(), mInLongPress, mProcessingPreview, mProcessingOcr));

        if (!mInLongPress && !mProcessingPreview && !mProcessingOcr)
        {
            Log.d(TAG, "onUp - SetPreviewImage");
            setBorderStyle(e);
            mProcessingPreview = true;
            setCroppedScreenshot();
        }

        mInLongPress = false;

        return true;
    }

    @Override
    public void stop() {
        mOcr.stop();
        super.stop();
    }

    public void showLoadingAnimation(){
        ((MainService)context).getHandler().post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "showLoadingAnimation");

                mWindowBox.setBackground(mBorderDefault);
                mImageView.setImageAlpha(0);
                mWindowBox.setAnimation(mFadeRepeat);
                mWindowBox.startAnimation(mFadeRepeat);
            }
        });
    }

    public void stopLoadingAnimation(final boolean instant){
        ((MainService)context).getHandler().post(new Runnable() {
            @Override
            public void run() {
                mProcessingOcr = false;
                mWindowBox.setBackground(mBorderReady);
                mWindowBox.clearAnimation();
                Log.d(TAG, "stopLoadingAnimation - instant: " + instant);
                if (instant)
                {
                    mImageView.setImageAlpha(255);
                }
                else {
                    mImageView.setImageAlpha(255);
                    mImageView.setImageResource(0);
                }
            }
        });
    }

    public void hideInstantWindows()
    {
        windowCoordinator.getWindow(Constants.WINDOW_INSTANT_KANJI).hide();
    }

    private void setPreviewImageForThreshold(MotionEvent e)
    {
        if (mPrefs.getImageFilterSetting() && mScreenshotForOcr != null){
            mThreshold = (int)((e.getRawX() / getRealDisplaySize().x) * 255);
            Bitmap bitmap = mScreenshotForOcr.getProcessedScreenshot(mThreshold);
            mImageView.setImageBitmap(bitmap);
        }
    }

    private void setCroppedScreenshot()
    {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                final ScreenshotForOcr ocrScreenshot = getScreenshotForOcr();

                if (ocrScreenshot == null || ocrScreenshot.crop == null || ocrScreenshot.orig == null || ocrScreenshot.params == null)
                {
                    mProcessingPreview = false;
                    return;
                }

                ((MainService)context).getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mScreenshotForOcr = ocrScreenshot;

                        if (mPrefs.getImageFilterSetting())
                        {
                            Bitmap screen = mScreenshotForOcr.getCachedScreenshot();
                            //mImageView.setImageBitmap(calculateFuriganaPosition(screen));
                            mImageView.setImageBitmap(screen);
                        }

                        if (mPrefs.getInstantModeSetting() && System.currentTimeMillis() > mLastDoubleTapTime + mLastDoubleTapIgnoreDelay)
                        {
                            int sizeForInstant = minSize * 4;
                            if (sizeForInstant >= mScreenshotForOcr.params.width || sizeForInstant >= mScreenshotForOcr.params.height)
                            {
                                performOcr(true);
                            }
                        }
                        mProcessingPreview = false;
                    }
                });
            }
        });
        thread.start();
    }

    private Bitmap calculateFuriganaPosition(Bitmap bitmap)
    {
        Bitmap screen = bitmap.copy(bitmap.getConfig(), true);

        int screenHeight = screen.getHeight();
        float screenHeightHalf = screenHeight / 2;
        int screenWidth = screen.getWidth();

        int[] histogram = new int[screenWidth];
        float[] histogramBoost = new float[screenWidth];

        for (int x = 0; x < screenWidth; x++)
        {
            for (int y = 0; y < screenHeight; y++)
            {
                int pixel = screen.getPixel(x, y);
                int R = (pixel >> 16) & 0xff;
                int G = (pixel >>  8) & 0xff;
                int B = (pixel      ) & 0xff;
                if (!(R < 10 && G < 10 && B < 10))
                {
                    histogram[x]++;
                    histogramBoost[x] += (float)y / screenHeight < 0.5 ? ((screenHeightHalf - y) / screenHeightHalf) : -((y - screenHeightHalf) / screenHeightHalf);
                }
            }
        }

        // Calculate boost
        float boostTotal = 0;
        float boostAvg = 0;
        float boostMax = 0;
        // Find highest boost value
        for (int x = 0; x < screenWidth; x++)
        {
            if (histogramBoost[x] > boostMax) boostMax = histogramBoost[x];
        }
        // Stretch boost by itself (higher boosts will be even higher), and normalize all boost values by boostMax
        for (int x = 0; x < screenWidth; x++)
        {
            histogramBoost[x] = (histogramBoost[x] * histogramBoost[x]) / boostMax;
        }
        // Find highest boost value
        for (int x = 0; x < screenWidth; x++)
        {
            if (histogramBoost[x] > boostMax) boostMax = histogramBoost[x];
        }
        // Normalize all boost values by boostMax again
        for (int x = 0; x < screenWidth; x++)
        {
            histogramBoost[x] = histogramBoost[x] / boostMax;
            boostTotal += histogramBoost[x];
        }
        boostAvg = boostTotal / screenWidth;

        // Calculate histogram average excluding white columns
        int averageTotal = 0;
        int averageNonZero = 0;
        for (int i = 0; i < histogram.length; i++)
        {
            if (histogram[i] != 0)
            {
                averageTotal += histogram[i];
                averageNonZero++;
            }
        }
        int avg = averageNonZero == 0 ? screenHeight : averageTotal / averageNonZero;
        int avgLine = screenHeight - (screenHeight - avg);
        int maxBoostTimes = screenHeight - avg;
        avgLine = avgLine >= screenHeight ? screenHeight - 1 : avgLine;

        // Draw histogram
        for (int x = 0; x < screenWidth; x++)
        {
            if (histogram[x] == 0)
            {
                continue;
            }

            int y;
            for (y = screenHeight - 1; y >= screenHeight - (screenHeight - histogram[x]) ; y--)
            {
                screen.setPixel(x, y, screen.getPixel(x, y) & 0xFFFFC8C8);
            }

            if (histogramBoost[x] > 0)
            {
                int timesToBoost = (int)(histogramBoost[x] * screenHeight);
                for (int i = 0; i < timesToBoost; i++)
                {
                    if (y > 0)
                    {
                        screen.setPixel(x, y, screen.getPixel(x, y) & 0xFFC8C8FF);
                        y--;
                    }
                }
            }

            if (histogram[x] != screenHeight)
            {
                while (y > 0)
                {
                    screen.setPixel(x, y, screen.getPixel(x, y) & 0xFFC8C8C8);
                    y--;
                }
            }
        }

        // Draw average histogram line
        int avgLineM = avgLine - 1 < 0 ? 0 : avgLine - 1;
        int avgLineP = avgLine + 1 > screenHeight - 1 ? screenHeight - 1 : avgLine + 1;
        for (int x = 0; x < screenWidth; x++)
        {
            screen.setPixel(x, avgLineM, Color.GREEN);
            screen.setPixel(x, avgLine, Color.GREEN);
            screen.setPixel(x, avgLineP, Color.GREEN);
        }

        return screen;
    }

    private void setBorderStyle(MotionEvent e)
    {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindowBox.setBackground(mBorderDefault);
                break;
            case MotionEvent.ACTION_UP:
                mWindowBox.setBackground(mBorderReady);
                break;
        }
    }

    private ScreenshotForOcr getScreenshotForOcr()
    {
        int[] viewPos = new int[2];
        mWindowBox.getLocationOnScreen(viewPos);
        BoxParams box = new BoxParams(viewPos[0], viewPos[1], params.width, params.height);

        try
        {
            return getReadyScreenshot(box);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private void performOcr(boolean instant)
    {
        mProcessingOcr = true;

        try
        {
            if (!instant)
            {
                while (!mOcr.isReadyForOcr())
                {
                    mOcr.cancel();
                    Thread.sleep(10);
                }
            }

            // TODO: mScreenshotForOcr can be null here, fix
            Bitmap processedImage = mPrefs.getImageFilterSetting() ? mScreenshotForOcr.getCachedScreenshot() : mScreenshotForOcr.crop;

            TextDirection textDirection = mPrefs.getTextDirectionSetting();
            if (textDirection == TextDirection.AUTO)
            {
                textDirection = mScreenshotForOcr.params.width >= mScreenshotForOcr.params.height ? TextDirection.HORIZONTAL : TextDirection.VERTICAL;
            }

            mOcr.runTess(new OcrParams(processedImage, mScreenshotForOcr.crop, mScreenshotForOcr.params, textDirection, instant));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private ScreenshotForOcr getReadyScreenshot(BoxParams box) throws Exception
    {
        Log.d(TAG, String.format("X:%d Y:%d (%dx%d)", box.x, box.y, box.width, box.height));

        boolean screenshotReady;
        long startTime = System.nanoTime();
        Bitmap screenshot;

        do {

            Image rawScreenshot = ((MainService)context).getScreenshot();
            if (rawScreenshot == null){
                Log.d(TAG, "getReadyScreenshot - rawScreenshot null");
                return null;
            }

            screenshot = convertImageToBitmap(rawScreenshot);
            screenshotReady = checkScreenshotIsReady(screenshot, box);

            int[] viewPos = new int[2];
            mWindowBox.getLocationOnScreen(viewPos);
            box.x = viewPos[0];
            box.y = viewPos[1];
            box.width = params.width;
            box.height = params.height;

        } while (!screenshotReady && System.nanoTime() < startTime + 4000000000L);

        Bitmap croppedBitmap = getCroppedBitmap(screenshot, box);

        //saveBitmap(screenshot, String.format("debug_(%d,%d)_(%d,%d)", box.x, box.y, box.width, box.height));
        if (!screenshotReady){
            saveBitmap(screenshot, String.format("error_(%d,%d)_(%d,%d)", box.x, box.y, box.width, box.height));
            saveBitmap(croppedBitmap, String.format("error_(%d,%d)_(%d,%d)", box.x, box.y, box.width, box.height));
            return null;
        }

        return new ScreenshotForOcr(croppedBitmap, screenshot, box, mThreshold);
    }

    private boolean checkScreenshotIsReady(Bitmap screenshot, BoxParams box)
    {
        int readyColor = ContextCompat.getColor(context, R.color.red_capture_window_ready);
        int screenshotColor = screenshot.getPixel(box.x, box.y);

        if (readyColor != screenshotColor && isAcceptableAlternateReadyColor(screenshotColor)){
            readyColor = screenshotColor;
        }

        for (int x = box.x; x < box.x + box.width; x++){
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(x, box.y))){
                return false;
            }
        }

        for (int x = box.x; x < box.x + box.width; x++){
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(x, box.y + box.height - 1))){
                return false;
            }
        }

        for (int y = box.y; y < box.y + box.height; y++){
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(box.x, y))){
                return false;
            }
        }

        for (int y = box.y; y < box.y + box.height; y++){
            if (!isRGBWithinTolorance(readyColor, screenshot.getPixel(box.x + box.width - 1, y))){
                return false;
            }
        }

        return true;
    }

    /**
     * Looks like sometimes the screenshot just has a color that is 100% totally wrong. Let's just accept any red that's "red enough"
     * @param screenshotColor
     * @return
     */
    private boolean isAcceptableAlternateReadyColor(int screenshotColor)
    {
        int R = (screenshotColor >> 16) & 0xFF;
        int G = (screenshotColor >>  8) & 0xFF;
        int B = (screenshotColor      ) & 0xFF;

        boolean isValid = true;

        if (G * 10 > R){
            isValid = false;
        }

        if (B * 10 > R){
            isValid = false;
        }

        return isValid;
    }

    private boolean isRGBWithinTolorance(int color, int colorToCheck)
    {
        boolean isColorWithinTolorance = true;

        isColorWithinTolorance &= isColorWithinTolorance((color      ) & 0xFF, (colorToCheck      ) & 0xFF);
        isColorWithinTolorance &= isColorWithinTolorance((color >>  8) & 0xFF, (colorToCheck >>  8) & 0xFF);
        isColorWithinTolorance &= isColorWithinTolorance((color >> 16) & 0xFF, (colorToCheck >> 16) & 0xFF);

        return isColorWithinTolorance;
    }

    private boolean isColorWithinTolorance(int color, int colorToCheck)
    {
        return color - 2 <= colorToCheck && colorToCheck <= color + 2;
    }

    private Bitmap convertImageToBitmap(Image image) throws OutOfMemoryError
    {
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

    private Bitmap getCroppedBitmap(Bitmap screenshot, BoxParams box){
        int borderSize = KakuTools.dpToPx(context, 1) + 1; // +1 due to rounding errors
        return Bitmap.createBitmap(screenshot, box.x + borderSize,
                box.y + borderSize,
                box.width - (2 * borderSize),
                box.height - (2 * borderSize));
    }

    private void saveBitmap(Bitmap bitmap, String name) throws IOException
    {
        String fs = String.format("%s/%s/%s_%d.png", context.getExternalFilesDir(null).getAbsolutePath(), Constants.SCREENSHOT_FOLDER_NAME, name, System.nanoTime());
        Log.d(TAG, fs);
        FileOutputStream fos = new FileOutputStream(fs);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
    }
}
