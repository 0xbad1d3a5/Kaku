package ca.fuwafuwa.kaku.Database.JmDictDatabase.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class KanjiIrregularity {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose(serialize = false)
    @DatabaseField(foreign = true)
    private Kanji fkKanji;

    @Expose
    @DatabaseField
    private String kanjiIrregularity;

    public String getKanjiIrregularity() {
        return kanjiIrregularity;
    }

    public void setKanjiIrregularity(String kanjiIrregularity) {
        this.kanjiIrregularity = kanjiIrregularity;
    }

    public Kanji getFkKanji() {
        return fkKanji;
    }

    public void setFkKanji(Kanji fkKanji) {
        this.fkKanji = fkKanji;
    }

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }
}
