package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class ReadingRestriction {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true)
    private Reading fkReading;

    @DatabaseField
    private String readingRestriction;

    public String getReadingRestriction() {
        return readingRestriction;
    }

    public void setReadingRestriction(String readingRestriction) {
        this.readingRestriction = readingRestriction;
    }

    public Reading getFkReading() {
        return fkReading;
    }

    public void setFkReading(Reading fkReading) {
        this.fkReading = fkReading;
    }
}
