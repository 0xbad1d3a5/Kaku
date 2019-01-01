package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import androidx.core.content.ContextCompat;
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

import ca.fuwafuwa.kaku.Constants;
import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.BoxParams;
import ca.fuwafuwa.kaku.Ocr.OcrRunnable;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;
import ca.fuwafuwa.kaku.XmlParsers.CommonParser;

/**
 * Created by 0xbad1d3a5 on 4/13/2016.
 */
public class CaptureWindow extends Window implements WindowListener {

    private class CroppedScreenshot {

        public final Bitmap bitmap;
        public final BoxParams params;

        public CroppedScreenshot(Bitmap bitmap, BoxParams params){
            this.bitmap = bitmap;
            this.params = params;
        }
    }

    private static final String TAG = CaptureWindow.class.getName();

    private OcrRunnable mOcr;
    private View mWindowBox;
    private ImageView mImageView;
    private Animation mFadeRepeat;
    private Drawable mBorderDefault;
    private Drawable mBorderReady;
    private boolean mAllowOcr;

    private boolean mShowPreviewImage;
    private int mThreshold;
    private boolean mInLongPress;
    private boolean mProcessingPreview;
    private boolean mProcessingOcr;
    private CroppedScreenshot mPreviewImage;

    private CommonParser mCommonParser;

    public CaptureWindow(final Context context, boolean showPreviewImage, boolean horizontalText) {
        super(context, R.layout.capture_window);

        this.mCommonParser = new CommonParser(context);

        mImageView = (ImageView) window.findViewById(R.id.capture_image);
        mFadeRepeat = AnimationUtils.loadAnimation(this.context, R.anim.fade_repeat);
        mBorderDefault = this.context.getResources().getDrawable(R.drawable.bg_translucent_border_0_blue_blue, null);
        mBorderReady = this.context.getResources().getDrawable(R.drawable.bg_transparent_border_0_nil_ready, null);
        mAllowOcr = false;

        mShowPreviewImage = showPreviewImage;
        mThreshold = 128;
        mInLongPress = false;
        mProcessingPreview = false;
        mProcessingOcr = false;
        mPreviewImage = null;

        startOcrThread(horizontalText);

        windowManager.getDefaultDisplay().getRotation();

        // Need to wait for the view to finish updating before we try to determine it's location
        mWindowBox = window.findViewById(R.id.capture_box);
        mWindowBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mAllowOcr){
                    performOcr();
                }
            }
        });
        mWindowBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((MainService)context).onCaptureWindowFinishedInitializing();
                mWindowBox.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void reInitOcr(boolean showPreviewImage, boolean horizontalText)
    {
        mShowPreviewImage = showPreviewImage;
        mOcr.stop();
        startOcrThread(horizontalText);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        Log.d(TAG, "onDoubleTap");

        mProcessingOcr = true;
        performOcr();

//        try {
//            mCommonParser.parseJmDict();
//            mCommonParser.parseKanjiDict2();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }

        return true;
    }

    @Override
    public boolean onTouch(MotionEvent e) {

        if (!mInLongPress && !mProcessingOcr){
            mImageView.setImageResource(0);
            setBorderStyle(e);
        }

        if (mInLongPress && mShowPreviewImage){
            switch (e.getAction()){
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "onTouch - Move");
                    setPreviewImageForThreshold(e);
            }
        }

        return super.onTouch(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {

        Log.d(TAG, "onLongPress");

        mInLongPress = true;
        setPreviewImageForThreshold(e);
    }

    @Override
    public boolean onResize(MotionEvent e) {

        Log.d(TAG, "onResize");

        mImageView.setImageResource(0);
        setBorderStyle(e);
        return super.onResize(e);
    }

    @Override
    public boolean onUp(MotionEvent e){

        Log.d(TAG, String.format("onUp - mShowPreviewImage: %b | mInLongPress: %b | mProcessingPreview: %b | mProcessingOcr: %b", mShowPreviewImage, mInLongPress, mProcessingPreview, mProcessingOcr));

        if (!mInLongPress && !mProcessingPreview && !mProcessingOcr){
            Log.d(TAG, "onUp - SetPreviewImage");
            setBorderStyle(e);
            mProcessingPreview = true;
            setPreviewImage();
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
                mWindowBox.setBackground(mBorderDefault);
                mImageView.setImageAlpha(0);
                mWindowBox.setAnimation(mFadeRepeat);
                mWindowBox.startAnimation(mFadeRepeat);
            }
        });
    }

    public void stopLoadingAnimation(){
        ((MainService)context).getHandler().post(new Runnable() {
            @Override
            public void run() {
                mProcessingOcr = false;
                mWindowBox.setBackground(mBorderReady);
                mWindowBox.clearAnimation();
                mImageView.setImageAlpha(255);
                mImageView.setImageResource(0);
            }
        });
    }

    private void setPreviewImageForThreshold(MotionEvent e)
    {
        if (mShowPreviewImage && mPreviewImage != null){
            mThreshold = (int)((e.getRawX() / getRealDisplaySize().x) * 256);
            Bitmap bitmap = getProcessedScreenshot(mPreviewImage.bitmap);
            mImageView.setImageBitmap(bitmap);
        }
    }


    private void startOcrThread(boolean horizontalText)
    {
        mOcr = new OcrRunnable(this.context, this, horizontalText);
        Thread tessThread = new Thread(mOcr);
        tessThread.setName(String.format("TessThread%d", System.nanoTime()));
        tessThread.setDaemon(true);
        tessThread.start();
    }

    private void setPreviewImage()
    {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                CroppedScreenshot screenshot = getCroppedScreenshot();

                if (screenshot == null || screenshot.bitmap == null || screenshot.params == null){
                    mProcessingPreview = false;
                    return;
                }

                mPreviewImage = screenshot;

                ((MainService)context).getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mShowPreviewImage){
                            mImageView.setImageBitmap(getProcessedScreenshot(mPreviewImage.bitmap));
                        }
                        mProcessingPreview = false;
                    }
                });
            }
        });
        thread.start();
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

    private Bitmap getProcessedScreenshot(Bitmap bitmap)
    {
        Pix pix = ReadFile.readBitmap(bitmap).clone();

        //pix = AdaptiveMap.pixContrastNorm(pix, 5, 5, 40, 2, 1);
        //pix = Convert.convertTo8(pix);
        //pix = Binarize.otsuAdaptiveThreshold(pix);
        pix = GrayQuant.pixThresholdToBinary(pix, mThreshold);

        Bitmap returnBitmap = WriteFile.writeBitmap(pix);
        pix.recycle();

        return returnBitmap;
    }

    private CroppedScreenshot getCroppedScreenshot()
    {
        int[] viewPos = new int[2];
        mWindowBox.getLocationOnScreen(viewPos);
        BoxParams box = new BoxParams(viewPos[0], viewPos[1], params.width, params.height);

        try
        {
            return new CroppedScreenshot(getReadyScreenshot(box), box);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private void performOcr()
    {
        try
        {
            Bitmap bitmapToProcess = mShowPreviewImage ? getProcessedScreenshot(mPreviewImage.bitmap) : mPreviewImage.bitmap;
            mOcr.runTess(bitmapToProcess, mPreviewImage.params);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        mAllowOcr = false;
    }

    private Bitmap getReadyScreenshot(BoxParams box) throws Exception
    {
        Log.d(TAG, String.format("X:%d Y:%d (%dx%d)", box.x, box.y, box.width, box.height));

        boolean screenshotReady;
        long startTime = System.nanoTime();
        Bitmap screenshot;

        do {

            Image rawScreenshot = ((MainService)context).getScreenshot();
            if (rawScreenshot == null){
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

        return croppedBitmap;
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
