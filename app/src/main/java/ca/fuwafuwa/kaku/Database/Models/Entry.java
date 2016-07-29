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
    private String entry;

    @ForeignCollectionField()
    private ForeignCollection<Kanji> kanjis;

    @ForeignCollectionField()
    private ForeignCollection<Reading> readings;

    @ForeignCollectionField()
    private ForeignCollection<Meaning> meanings;

    public Entry(){
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }
}
