package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class Reading {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @DatabaseField
    private String reading;

    @DatabaseField
    private String falseReading;

    @ForeignCollectionField()
    private ForeignCollection<ReadingRestriction> readingRestrictions;

    @ForeignCollectionField()
    private ForeignCollection<ReadingIrregularity> readingIrregularities;

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
}
