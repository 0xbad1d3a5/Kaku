package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 0x1bad1d3a on 5/2/2016.
 */
@DatabaseTable
public class Entry {

    @DatabaseField(id = true)
    private String id;

    @ForeignCollectionField()
    private ForeignCollection<Kanji> kanjis;

    @ForeignCollectionField()
    private ForeignCollection<Reading> readings;

    @ForeignCollectionField()
    private ForeignCollection<Meaning> meanings;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
