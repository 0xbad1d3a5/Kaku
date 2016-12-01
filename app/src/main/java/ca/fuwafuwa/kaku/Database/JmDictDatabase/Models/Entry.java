package ca.fuwafuwa.kaku.Database.JmDictDatabase.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by Xyresic on 5/2/2016.
 */
@DatabaseTable
public class Entry {

    @Expose
    @DatabaseField(id = true)
    private Integer id;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<Kanji> kanjis;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<Reading> readings;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<Meaning> meanings;

    public Entry(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry1 = (Entry) o;

        return id.equals(entry1.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
