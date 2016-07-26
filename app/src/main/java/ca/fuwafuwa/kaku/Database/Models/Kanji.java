package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xyresic on 7/25/2016.
 */
@DatabaseTable
public class Kanji {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @DatabaseField
    private String kanji;

    @ForeignCollectionField()
    private ForeignCollection<KanjiIrregularity> kanjiIrregularities;

    @ForeignCollectionField()
    private ForeignCollection<KanjiPriority> kanjiPriorities;
}
