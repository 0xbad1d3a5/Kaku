package ca.fuwafuwa.kaku.Database.JmDictDatabase.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0xbad1d3a5 on 7/25/2016.
 */
@DatabaseTable
public class ReadingIrregularity {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose(serialize = false)
    @DatabaseField(foreign = true)
    private Reading fkReading;

    @Expose
    @DatabaseField
    private String readingIrregularity;

    public String getReadingIrregularity() {
        return readingIrregularity;
    }

    public void setReadingIrregularity(String readingIrregularity) {
        this.readingIrregularity = readingIrregularity;
    }

    public Reading getFkReading() {
        return fkReading;
    }

    public void setFkReading(Reading fkReading) {
        this.fkReading = fkReading;
    }

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }
}
