package ca.fuwafuwa.kaku.Database.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class ReadingPriority {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose(serialize = false)
    @DatabaseField(foreign = true)
    private Reading fkReading;

    @Expose
    @DatabaseField
    private String readingPriority;

    public String getReadingPriority() {
        return readingPriority;
    }

    public void setReadingPriority(String readingPriority) {
        this.readingPriority = readingPriority;
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
