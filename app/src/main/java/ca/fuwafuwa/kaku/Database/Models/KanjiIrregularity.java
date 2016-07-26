package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class KanjiIrregularity {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Kanji fkKanji;

    @DatabaseField
    private String kanjiInf;
}
