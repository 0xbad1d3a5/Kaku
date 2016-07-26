package ca.fuwafuwa.kaku.Database.Models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class Meaning {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @ForeignCollectionField()
    private ForeignCollection<MeaningKanjiRestriction> kanjiRestrictions;

    @ForeignCollectionField()
    private ForeignCollection<MeaningReadingRestriction> readingRestrictions;

    @ForeignCollectionField()
    private ForeignCollection<MeaningPartOfSpeech> partsOfSpeech;

    @ForeignCollectionField()
    private ForeignCollection<MeaningCrossReference> crossReferences;

    @ForeignCollectionField()
    private ForeignCollection<MeaningAntonym> antonyms;

    @ForeignCollectionField()
    private ForeignCollection<MeaningField> fields;

    @ForeignCollectionField()
    private ForeignCollection<MeaningMisc> miscs;

    @ForeignCollectionField()
    private ForeignCollection<MeaningAdditionalInfo> additionalInfos;

    @ForeignCollectionField()
    private ForeignCollection<MeaningLoanSource> loanSources;

    @ForeignCollectionField()
    private ForeignCollection<MeaningDialect> dialects;

    @ForeignCollectionField()
    private ForeignCollection<MeaningGloss> glosses;
}
