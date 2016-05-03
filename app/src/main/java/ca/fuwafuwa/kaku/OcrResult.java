package ca.fuwafuwa.kaku;

/**
 * Created by Xyresic on 5/2/2016.
 */
public class OcrResult {

    private String text;
    private long screenshotTime;
    private long ocrTime;

    public OcrResult(String text, long screenshotTime, long ocrTime){
        this.text = text;
        this.screenshotTime = screenshotTime;
        this.ocrTime = ocrTime;
    }

    public String getText(){
        return text;
    }

    public long getScreenshotTime(){
        return screenshotTime;
    }

    public long getOcrTime(){
        return ocrTime;
    }

    public String toString(){
        return String.format("%s\nScreenshot Time: %d\nOcrTime: %d", text, screenshotTime, ocrTime);
    }
}
