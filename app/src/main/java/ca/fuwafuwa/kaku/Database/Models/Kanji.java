package ca.fuwafuwa.kaku.Database.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class Kanji {

    @Expose(serialize = false)
    public static final String KANJI_FIELD = "kanji";

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose(serialize = false)
    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @Expose
    @DatabaseField
    private String kanji;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<KanjiIrregularity> kanjiIrregularities;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<KanjiPriority> kanjiPriorities;

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public Entry getFkEntry() {
        return fkEntry;
    }

    public void setFkEntry(Entry fkEntry) {
        this.fkEntry = fkEntry;
    }

    public ForeignCollection<KanjiIrregularity> getKanjiIrregularities() {
        return kanjiIrregularities;
    }

    public ForeignCollection<KanjiPriority> getKanjiPriorities() {
        return kanjiPriorities;
    }

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }
}
