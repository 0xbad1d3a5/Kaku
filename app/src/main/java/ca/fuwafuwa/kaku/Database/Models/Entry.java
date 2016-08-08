package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 5/2/2016.
 */
@DatabaseTable
public class Entry {

    @DatabaseField(id = true)
    private Integer entry;

    @ForeignCollectionField()
    private ForeignCollection<Kanji> kanjis;

    @ForeignCollectionField()
    private ForeignCollection<Reading> readings;

    @ForeignCollectionField()
    private ForeignCollection<Meaning> meanings;

    public Entry(){
    }

    public Integer getEntry() {
        return entry;
    }

    public void setEntry(Integer entry) {
        this.entry = entry;
    }

    public ForeignCollection<Kanji> getKanjis() {
        return kanjis;
    }

    public void setKanjis(ForeignCollection<Kanji> kanjis) {
        this.kanjis = kanjis;
    }

    public ForeignCollection<Reading> getReadings() {
        return readings;
    }

    public void setReadings(ForeignCollection<Reading> readings) {
        this.readings = readings;
    }

    public ForeignCollection<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(ForeignCollection<Meaning> meanings) {
        this.meanings = meanings;
    }
}
