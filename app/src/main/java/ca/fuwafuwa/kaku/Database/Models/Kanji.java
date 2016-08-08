package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class Kanji {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @DatabaseField
    private String kanji;

    @ForeignCollectionField()
    private ForeignCollection<KanjiIrregularity> kanjiIrregularities;

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
}
