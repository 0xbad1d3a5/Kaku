package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class Reading {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @DatabaseField
    private String reading;

    @DatabaseField
    private String falseReading;

    @ForeignCollectionField()
    private ForeignCollection<ReadingRestriction> readingRestrictions;

    @ForeignCollectionField()
    private ForeignCollection<ReadingIrregularity> readingIrregularities;

    @ForeignCollectionField()
    private ForeignCollection<ReadingPriority> readingPriorities;
}
