package ca.fuwafuwa.kaku;

/**
 * Created by Xyresic on 5/2/2016.
 */
public class OcrResult {

    private String mText;
    private long mScreenshotTime;
    private long mOcrTime;

    public OcrResult(String text, long screenshotTime, long ocrTime){
        this.mText = text;
        this.mScreenshotTime = screenshotTime;
        this.mOcrTime = ocrTime;
    }

    public String getText(){
        return mText;
    }

    public long getScreenshotTime(){
        return mScreenshotTime;
    }

    public long getOcrTime(){
        return mOcrTime;
    }

    public String toString(){
        return String.format("%s\nScreenshot Time: %d\nOcrTime: %d", mText, mScreenshotTime, mOcrTime);
    }
}
