package ca.fuwafuwa.kaku.Database.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0x1bad1d3a on 7/25/2016.
 */
@DatabaseTable
public class Meaning {

    @Expose(serialize = false)
    @DatabaseField(generatedId = true)
    private Integer id;

    @Expose(serialize = false)
    @DatabaseField(foreign = true)
    private Entry fkEntry;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningKanjiRestriction> kanjiRestrictions;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningReadingRestriction> readingRestrictions;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningPartOfSpeech> partsOfSpeech;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningCrossReference> crossReferences;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningAntonym> antonyms;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningField> fields;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningMisc> miscs;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningAdditionalInfo> additionalInfos;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningLoanSource> loanSources;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningDialect> dialects;

    @Expose
    @ForeignCollectionField()
    private ForeignCollection<MeaningGloss> glosses;

    public Entry getFkEntry() {
        return fkEntry;
    }

    public void setFkEntry(Entry fkEntry) {
        this.fkEntry = fkEntry;
    }

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }
}
