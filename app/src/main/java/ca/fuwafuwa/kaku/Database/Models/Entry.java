package ca.fuwafuwa.kaku.Database.Models;

/**
 * Created by Xyresic on 5/2/2016.
 */
public class Entry {

    private String mKanji;
    private String mReading;
    private String mSense;

    public Entry(){
    }

    public Entry(String kanji, String reading, String sense){
        this.mKanji = kanji;
        this.mReading = reading;
        this.mSense = sense;
    }

    public String getKanji() {
        return mKanji;
    }

    public String getReading() {
        return mReading;
    }

    public String getSense() {
        return mSense;
    }

    public void setKanji(String kanji) {
        this.mKanji = kanji;
    }

    public void setReading(String reading) {
        this.mReading = reading;
    }

    public void setSense(String sense) {
        this.mSense = sense;
    }

    public String toString(){
        if (mKanji != null){
            return String.format("Kanji: %s\nReading: %s\nSense: %s\n", mKanji, mReading, mSense);
        }
        else {
            return null;
        }
    }
}
