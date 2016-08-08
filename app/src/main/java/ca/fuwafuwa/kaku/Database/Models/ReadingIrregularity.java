package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class ReadingIrregularity {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true)
    private Reading fkReading;

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
}
