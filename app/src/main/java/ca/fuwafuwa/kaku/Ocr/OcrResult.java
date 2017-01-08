package ca.fuwafuwa.kaku.Ocr;

import android.util.Pair;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 0x1bad1d3a on 5/2/2016.
 */
public class OcrResult {

    private List<List<Pair<String, Double>>> ocrChoices;
    private long mScreenshotTime;
    private long mOcrTime;

    public OcrResult(List<List<Pair<String, Double>>> ocrChoices, long screenshotTime, long ocrTime){
        this.ocrChoices = ocrChoices;
        this.mScreenshotTime = screenshotTime;
        this.mOcrTime = ocrTime;

        sortChoices();
    }

    public List<List<Pair<String, Double>>> getOcrChoices(){
        return ocrChoices;
    }

    public String getText(){

        StringBuilder sb = new StringBuilder();

        for (List<Pair<String, Double>> choices : ocrChoices){
            sb.append(choices.get(0).first);
            continue;
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

    private void sortChoices(){

        for (List<Pair<String, Double>> choices : ocrChoices){
            Collections.sort(choices, new Comparator<Pair<String, Double>>() {
                @Override
                public int compare(Pair<String, Double> lhs, Pair<String, Double> rhs) {
                    if (lhs.second > rhs.second){
                        return -1;
                    }
                    else if (lhs.second == rhs.second){
                        return 0;
                    }
                    else {
                        return 1;
                    }
                }
            });
        }
    }
}
