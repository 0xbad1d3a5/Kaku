package ca.fuwafuwa.kaku.Ocr;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by 0x1bad1d3a on 5/2/2016.
 */
public class OcrResult {

    private Bitmap mImage;
    private List<OcrChar> mOcrChars;
    private long mScreenshotTime;
    private long mOcrTime;

    public OcrResult(Bitmap image, List<OcrChar> ocrChoices, long screenshotTime, long ocrTime){
        this.mImage = image;
        this.mOcrChars = ocrChoices;
        this.mScreenshotTime = screenshotTime;
        this.mOcrTime = ocrTime;
    }

    public Bitmap getBitmap(){
        return mImage;
    }

    public List<OcrChar> getOcrChars(){
        return mOcrChars;
    }

    public String getText(){

        StringBuilder sb = new StringBuilder();

        for (OcrChar ocrChar : mOcrChars){
            sb.append(ocrChar.getBestChoice());
        }

        return sb.toString();
    }

    public long getScreenshotTime(){
        return mScreenshotTime;
    }

    public long getOcrTime(){
        return mOcrTime;
    }

    public String toString(){
        return String.format("%s\nScreenshot Time: %d\nOcrTime: %d", getText(), mScreenshotTime, mOcrTime);
    }
}
