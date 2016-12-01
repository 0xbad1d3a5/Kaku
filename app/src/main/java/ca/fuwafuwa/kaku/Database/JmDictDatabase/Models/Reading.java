package ca.fuwafuwa.kaku.Database.JmDictDatabase.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class Reading {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose(serialize = false)
    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @Expose
    @DatabaseField
    private String reading;

    @Expose
    @DatabaseField
    private String falseReading;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<ReadingRestriction> readingRestrictions;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<ReadingIrregularity> readingIrregularities;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<ReadingPriority> readingPriorities;

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getFalseReading() {
        return falseReading;
    }

    public void setFalseReading(String falseReading) {
        this.falseReading = falseReading;
    }

    public Entry getFkEntry() {
        return fkEntry;
    }

    public void setFkEntry(Entry fkEntry) {
        this.fkEntry = fkEntry;
    }

    public ForeignCollection<ReadingRestriction> getReadingRestrictions() {
        return readingRestrictions;
    }

    public ForeignCollection<ReadingIrregularity> getReadingIrregularities() {
        return readingIrregularities;
    }

    public ForeignCollection<ReadingPriority> getReadingPriorities() {
        return readingPriorities;
    }

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }
}
