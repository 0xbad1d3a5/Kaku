package ca.fuwafuwa.kaku.Database.KanjiDict2Database.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 0x1bad1d3a on 12/3/2016.
 */

public class CharacterOptimized {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose
    @DatabaseField
    private String kanji;

    @Expose
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String onyomi;

    @Expose
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String kunyomi;

    @Expose
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String meaning;

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getOnyomi() {
        return onyomi;
    }

    public void setOnyomi(String onyomi) {
        this.onyomi = onyomi;
    }

    public String getKunyomi() {
        return kunyomi;
    }

    public void setKunyomi(String kunyomi) {
        this.kunyomi = kunyomi;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
