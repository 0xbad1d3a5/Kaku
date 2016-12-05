package ca.fuwafuwa.kaku.Database.KanjiDict2Database.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Xyresic on 12/3/2016.
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
}
