package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class MeaningKanjiRestriction {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Meaning fkMeaning;

    @DatabaseField
    private String kanjiRestriction;

    public String getKanjiRestriction() {
        return kanjiRestriction;
    }

    public void setKanjiRestriction(String kanjiRestriction) {
        this.kanjiRestriction = kanjiRestriction;
    }
}
