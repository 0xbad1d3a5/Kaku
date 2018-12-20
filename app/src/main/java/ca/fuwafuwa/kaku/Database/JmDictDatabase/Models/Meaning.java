package ca.fuwafuwa.kaku.Database.JmDictDatabase.Models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0xbad1d3a5 on 7/25/2016.
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
    public ForeignCollection<MeaningKanjiRestriction> getKanjiRestrictions() {
        return kanjiRestrictions;
    }

    public ForeignCollection<MeaningReadingRestriction> getReadingRestrictions() {
        return readingRestrictions;
    }

    public ForeignCollection<MeaningPartOfSpeech> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public ForeignCollection<MeaningCrossReference> getCrossReferences() {
        return crossReferences;
    }

    public ForeignCollection<MeaningAntonym> getAntonyms() {
        return antonyms;
    }

    public ForeignCollection<MeaningField> getFields() {
        return fields;
    }

    public ForeignCollection<MeaningMisc> getMiscs() {
        return miscs;
    }

    public ForeignCollection<MeaningAdditionalInfo> getAdditionalInfos() {
        return additionalInfos;
    }

    public ForeignCollection<MeaningLoanSource> getLoanSources() {
        return loanSources;
    }

    public ForeignCollection<MeaningDialect> getDialects() {
        return dialects;
    }

    public ForeignCollection<MeaningGloss> getGlosses() {
        return glosses;
    }

    @Override
    public String toString() {
        return KakuTools.toJson(this);
    }
}
