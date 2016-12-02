package ca.fuwafuwa.kaku.Database.JmDictDatabase.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 0x1bad1d3a on 12/1/2016.
 */

public class EntryOptimized implements Comparable<EntryOptimized> {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose
    @DatabaseField
    private String kanji;

    @Expose
    @DatabaseField
    private String readings;

    @Expose
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String meanings;

    private boolean onlyKana = false;

    public EntryOptimized(){
    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getReadings() {
        return readings;
    }

    public void setReadings(String readings) {
        this.readings = readings;
    }

    public String getMeanings() {
        return meanings;
    }

    public void setMeanings(String meanings) {
        this.meanings = meanings;
    }

    public boolean isOnlyKana() {
        return onlyKana;
    }

    public void setOnlyKana(boolean onlyKana) {
        this.onlyKana = onlyKana;
    }

    @Override
    public int compareTo(EntryOptimized another) {
        if (this.kanji.length() > another.getKanji().length()){
            return -1;
        }
        else if (this.kanji.length() == another.getKanji().length()){
            return 0;
        }
        else {
            return 1;
        }
    }
}
