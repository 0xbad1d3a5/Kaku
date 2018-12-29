package ca.fuwafuwa.kaku.Ocr;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by 0xbad1d3a5 on 5/2/2016.
 */
public class OcrResult {

    private Bitmap mImage;
    private List<OcrChar> mOcrChars;
    private long mOcrTime;

    public OcrResult(Bitmap image, List<OcrChar> ocrChoices, long ocrTime){
        this.mImage = image;
        this.mOcrChars = ocrChoices;
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

    public String getMessage() { return String.format("OCR Time: %.2fs", mOcrTime/1000.0); }

    public String toString(){
        return String.format("%s\nOcrTime: %d", getText(), mOcrTime);
    }
}
