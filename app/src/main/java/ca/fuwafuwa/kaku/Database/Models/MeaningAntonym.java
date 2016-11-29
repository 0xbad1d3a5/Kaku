package ca.fuwafuwa.kaku.Database.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class MeaningAntonym {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose(serialize = false)
    @DatabaseField(foreign = true)
    private Meaning fkMeaning;

    @Expose
    @DatabaseField
    private String antonym;

    public String getAntonym() {
        return antonym;
    }

    public void setAntonym(String antonym) {
        this.antonym = antonym;
    }

    public Meaning getFkMeaning() {
        return fkMeaning;
    }

    public void setFkMeaning(Meaning fkMeaning) {
        this.fkMeaning = fkMeaning;
    }

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }
}
