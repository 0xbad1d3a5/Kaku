package ca.fuwafuwa.kaku.XmlParsers.JmDict;

import android.util.Log;

import com.google.common.base.Joiner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.fuwafuwa.kaku.Constants;
import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.Database.IDatabaseHelper;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Entry;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Kanji;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.KanjiIrregularity;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.KanjiPriority;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Meaning;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningAdditionalInfo;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningAntonym;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningCrossReference;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningDialect;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningField;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningGloss;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningKanjiRestriction;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningLoanSource;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningMisc;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningPartOfSpeech;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningReadingRestriction;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Reading;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.ReadingIrregularity;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.ReadingPriority;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.ReadingRestriction;
import ca.fuwafuwa.kaku.LangUtils;
import ca.fuwafuwa.kaku.XmlParsers.Interfaces.DictParser;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO.JmEntry;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO.JmGloss;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO.JmKEle;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO.JmLsource;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO.JmREle;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO.JmSense;

/**
 * Created by 0xbad1d3a5 on 4/30/2016.
 */
public class JmParser implements DictParser {

    private static final String TAG = JmParser.class.getName();

    private IDatabaseHelper mDbHelper;
    private int parseCount = 0;

    public JmParser(IDatabaseHelper dbHelper){
        mDbHelper = dbHelper;
    }

    @Override
    public void parseDict(XmlPullParser parser) throws IOException, XmlPullParserException, SQLException {

        while (!JmConsts.JMDICT.equals(parser.getName())){
            parser.nextToken();
        }

        parser.require(XmlPullParser.START_TAG, null, JmConsts.JMDICT);
        parser.nextToken();

        while (!JmConsts.JMDICT.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case JmConsts.ENTRY:
                    parseJmEntry(parser);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JmConsts.JMDICT);
    }

    public void parseJmEntryOptimized(JmEntry jmEntry) throws SQLException {

        List<EntryOptimized> optimizedEntries = new ArrayList<>();

        // May be multiple kanji entries
        for (JmKEle kEle : jmEntry.getKEle())
        {
            EntryOptimized entryOptimized = new EntryOptimized();
            entryOptimized.setKanji(kEle.getKeb());
            entryOptimized.setPriorities(Joiner.on(",").join(kEle.getKePri()));
            optimizedEntries.add(entryOptimized);
        }

        // If no kanji entries, the reading entry is the kanji
        if (optimizedEntries.isEmpty())
        {
            for (JmREle rEle : jmEntry.getREle())
            {
                EntryOptimized entryOptimized = new EntryOptimized();
                entryOptimized.setKanji(rEle.getReb());
                entryOptimized.setOnlyKana(true);
                entryOptimized.setPriorities(Joiner.on(",").join(rEle.getRePri()));
                optimizedEntries.add(entryOptimized);
            }
        }

        for (EntryOptimized entryOptimized : optimizedEntries){

            String kanji = entryOptimized.getKanji();
            List<String> eoReadings = new ArrayList<>();
            List<String> eoMeanings = new ArrayList<>();
            Set eoPriorities = new HashSet<>(Arrays.asList(entryOptimized.getPriorities().split(",")));
            List<String> eoPos = new ArrayList<>();

            if (!entryOptimized.isOnlyKana())
            {
                for (JmREle rEle : jmEntry.getREle())
                {
                    List<String> readingRestriction = rEle.getReRestr();

                    if (!readingRestriction.isEmpty() && readingRestriction.contains(kanji))
                    {
                        eoReadings.add(rEle.getReb());
                        eoPriorities.addAll(rEle.getRePri());
                    }
                    else if (readingRestriction.isEmpty())
                    {
                        eoReadings.add(rEle.getReb());
                        eoPriorities.addAll(rEle.getRePri());
                    }
                }
            }

            for (JmSense sense : jmEntry.getSense())
            {
                List<String> kanjiRestriction = sense.getStagk();
                List<String> meaningSense = new ArrayList<>();

                StringBuilder fullSense = new StringBuilder();

                if (!kanjiRestriction.isEmpty() && kanjiRestriction.contains(kanji))
                {
                    for (JmGloss gloss : sense.getGloss())
                    {
                        if (gloss.isEnglish())
                        {
                            meaningSense.add(gloss.getText());
                        }
                    }
                }
                else if (kanjiRestriction.isEmpty())
                {
                    for (JmGloss gloss : sense.getGloss())
                    {
                        if (gloss.isEnglish())
                        {
                            meaningSense.add(gloss.getText());
                        }
                    }
                }

                fullSense.append(Joiner.on(", ").join(meaningSense));

                eoMeanings.add(fullSense.toString());
                eoPos.add(Joiner.on(",").join(sense.getPos()));
            }

            eoPriorities.remove(null);
            eoPriorities.remove("");

            entryOptimized.setReadings(Joiner.on(", ").join(eoReadings));
            entryOptimized.setMeanings(Joiner.on(Constants.DB_SPLIT_CHAR).join(eoMeanings));
            entryOptimized.setPos(Joiner.on(Constants.DB_SPLIT_CHAR).join(eoPos));
            entryOptimized.setPriorities(Joiner.on(",").join(eoPriorities));
            entryOptimized.setDictionary(Constants.DB_JMDICT_NAME);

            if (eoMeanings.size() != eoPos.size()){
                throw new RuntimeException();
            }

            mDbHelper.getDbDao(EntryOptimized.class).create(entryOptimized);
        }
    }

    private void parseJmEntry(XmlPullParser parser) throws IOException, XmlPullParserException, SQLException {

        JmEntry jmEntry = new JmEntry(parser);

        // Unneeded for now due to optimized entry
        /*
        Entry newEntry = new Entry();
        newEntry.setId(jmEntry.getEntSeq());
        mDbHelper.getDbDao(Entry.class).create(newEntry);

        parseJmKanji(jmEntry, newEntry);
        parseJmMeaning(jmEntry, newEntry);
        parseJmReading(jmEntry, newEntry);
        */

        parseJmEntryOptimized(jmEntry);

        if (++parseCount % 100 == 0){
            Log.d(TAG, String.format("Parsed %d entries", parseCount));
        }
    }

    private void parseJmKanji(JmEntry jmEntry, Entry entry) throws SQLException {
        for (JmKEle jmKanji : jmEntry.getKEle()){
            Kanji newKanji = new Kanji();
            newKanji.setFkEntry(entry);
            newKanji.setKanji(jmKanji.getKeb());
            mDbHelper.getDbDao(Kanji.class).create(newKanji);

            parseJmKanjiIrregularity(jmKanji, newKanji);
            parseJmKanjiPriority(jmKanji, newKanji);
        }
    }

    private void parseJmKanjiIrregularity(JmKEle jmKEle, Kanji kanji) throws SQLException {
        for (String jmKeInf : jmKEle.getKeInf()){
            KanjiIrregularity newKanjiIrregularity = new KanjiIrregularity();
            newKanjiIrregularity.setFkKanji(kanji);
            newKanjiIrregularity.setKanjiIrregularity(jmKeInf);
            mDbHelper.getDbDao(KanjiIrregularity.class).create(newKanjiIrregularity);
        }
    }

    private void parseJmKanjiPriority(JmKEle jmKEle, Kanji kanji) throws SQLException {
        for (String jmKePri : jmKEle.getKePri()){
            KanjiPriority newKanjiPriority = new KanjiPriority();
            newKanjiPriority.setFkKanji(kanji);
            newKanjiPriority.setKanjiPriority(jmKePri);
            mDbHelper.getDbDao(KanjiPriority.class).create(newKanjiPriority);
        }
    }

    private void parseJmMeaning(JmEntry jmEntry, Entry entry) throws SQLException {
        for (JmSense jmSense : jmEntry.getSense()){
            Meaning newMeaning = new Meaning();
            newMeaning.setFkEntry(entry);
            mDbHelper.getDbDao(Meaning.class).create(newMeaning);

            parseJmMeaningAdditionalInfo(jmSense, newMeaning);
            parseJmMeaningAntonym(jmSense, newMeaning);
            parseJmMeaningCrossReference(jmSense, newMeaning);
            parseJmMeaningDialect(jmSense, newMeaning);
            parseJmMeaningField(jmSense, newMeaning);
            parseJmMeaningGloss(jmSense, newMeaning);
            parseJmMeaningKanjiRestriction(jmSense, newMeaning);
            parseJmMeaningLoanSource(jmSense, newMeaning);
            parseJmMeaningMisc(jmSense, newMeaning);
            parseJmMeaningPartOfSpeech(jmSense, newMeaning);
            parseJmMeaningReadingRestriction(jmSense, newMeaning);
        }
    }

    private void parseJmMeaningAdditionalInfo(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmSInf : jmSense.getSInf()){
            MeaningAdditionalInfo newMeaningAdditionalInfo = new MeaningAdditionalInfo();
            newMeaningAdditionalInfo.setFkMeaning(meaning);
            newMeaningAdditionalInfo.setAdditionalInfo(jmSInf);
            mDbHelper.getDbDao(MeaningAdditionalInfo.class).create(newMeaningAdditionalInfo);
        }
    }

    private void parseJmMeaningAntonym(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmAnt : jmSense.getAnt()){
            MeaningAntonym newMeaningAntonym = new MeaningAntonym();
            newMeaningAntonym.setFkMeaning(meaning);
            newMeaningAntonym.setAntonym(jmAnt);
            mDbHelper.getDbDao(MeaningAntonym.class).create(newMeaningAntonym);
        }
    }

    private void parseJmMeaningCrossReference(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmXRef : jmSense.getXRef()){
            MeaningCrossReference newMeaningCrossReference = new MeaningCrossReference();
            newMeaningCrossReference.setFkMeaning(meaning);
            newMeaningCrossReference.setCrossReference(jmXRef);
            mDbHelper.getDbDao(MeaningCrossReference.class).create(newMeaningCrossReference);
        }
    }

    private void parseJmMeaningDialect(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmDial : jmSense.getDial()){
            MeaningDialect newMeaningDialect = new MeaningDialect();
            newMeaningDialect.setFkMeaning(meaning);
            newMeaningDialect.setDialect(jmDial);
            mDbHelper.getDbDao(MeaningDialect.class).create(newMeaningDialect);
        }
    }

    private void parseJmMeaningField(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmField : jmSense.getField()){
            MeaningField newMeaningField = new MeaningField();
            newMeaningField.setFkMeaning(meaning);
            newMeaningField.setField(jmField);
            mDbHelper.getDbDao(MeaningField.class).create(newMeaningField);
        }
    }

    private void parseJmMeaningGloss(JmSense jmSense, Meaning meaning) throws SQLException {
        for (JmGloss jmGloss : jmSense.getGloss()){
            if (jmGloss.getLang().equals("eng")){
                MeaningGloss newMeaningGloss = new MeaningGloss();
                newMeaningGloss.setFkMeaning(meaning);
                newMeaningGloss.setGender(jmGloss.getGender());
                newMeaningGloss.setGloss(jmGloss.getText());
                newMeaningGloss.setLang(jmGloss.getLang());
                mDbHelper.getDbDao(MeaningGloss.class).create(newMeaningGloss);
            }
        }
    }

    private void parseJmMeaningKanjiRestriction(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmStagk : jmSense.getStagk()){
            MeaningKanjiRestriction newMeaningKanjiRestriction = new MeaningKanjiRestriction();
            newMeaningKanjiRestriction.setFkMeaning(meaning);
            newMeaningKanjiRestriction.setKanjiRestriction(jmStagk);
            mDbHelper.getDbDao(MeaningKanjiRestriction.class).create(newMeaningKanjiRestriction);
        }
    }

    private void parseJmMeaningLoanSource(JmSense jmSense, Meaning meaning) throws SQLException {
        for (JmLsource jmLSource : jmSense.getLSource()){
            MeaningLoanSource newMeaningLoanSource = new MeaningLoanSource();
            newMeaningLoanSource.setFkMeaning(meaning);
            newMeaningLoanSource.setLang(jmLSource.getLang());
            newMeaningLoanSource.setLoanSource(jmLSource.getText());
            newMeaningLoanSource.setType(jmLSource.getLsType());
            newMeaningLoanSource.setWaseieigo(jmLSource.getLsWasei());
            mDbHelper.getDbDao(MeaningLoanSource.class).create(newMeaningLoanSource);
        }
    }

    private void parseJmMeaningMisc(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmMisc : jmSense.getMisc()){
            MeaningMisc newMeaningMisc = new MeaningMisc();
            newMeaningMisc.setFkMeaning(meaning);
            newMeaningMisc.setMisc(jmMisc);
            mDbHelper.getDbDao(MeaningMisc.class).create(newMeaningMisc);
        }
    }

    private void parseJmMeaningPartOfSpeech(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmPos : jmSense.getPos()){
            MeaningPartOfSpeech newMeaningPartOfSpeech = new MeaningPartOfSpeech();
            newMeaningPartOfSpeech.setFkMeaning(meaning);
            newMeaningPartOfSpeech.setPartOfSpeech(jmPos);
            mDbHelper.getDbDao(MeaningPartOfSpeech.class).create(newMeaningPartOfSpeech);
        }
    }

    private void parseJmMeaningReadingRestriction(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmStagr : jmSense.getStagr()){
            MeaningReadingRestriction newMeaningReadingRestriction = new MeaningReadingRestriction();
            newMeaningReadingRestriction.setFkMeaning(meaning);
            newMeaningReadingRestriction.setReadingRestriction(jmStagr);
            mDbHelper.getDbDao(MeaningReadingRestriction.class).create(newMeaningReadingRestriction);
        }
    }

    private void parseJmReading(JmEntry jmEntry, Entry entry) throws SQLException {
        for (JmREle jmREle : jmEntry.getREle()){
            Reading newReading = new Reading();
            newReading.setFkEntry(entry);
            newReading.setReading(jmREle.getReb());
            newReading.setFalseReading(jmREle.getReNoKanji());
            mDbHelper.getDbDao(Reading.class).create(newReading);

            parseJmReadingIrregularity(jmREle, newReading);
            parseJmReadingPriority(jmREle, newReading);
            parseJmReadingRestriction(jmREle, newReading);
        }
    }

    private void parseJmReadingIrregularity(JmREle jmREle, Reading reading) throws SQLException {
        for (String jmReInf : jmREle.getReInf()){
            ReadingIrregularity newReadingIrregularity = new ReadingIrregularity();
            newReadingIrregularity.setFkReading(reading);
            newReadingIrregularity.setReadingIrregularity(jmReInf);
            mDbHelper.getDbDao(ReadingIrregularity.class).create(newReadingIrregularity);
        }
    }

    private void parseJmReadingPriority(JmREle jmREle, Reading reading) throws SQLException {
        for (String jmRePri : jmREle.getRePri()){
            ReadingPriority newReadingPriority = new ReadingPriority();
            newReadingPriority.setFkReading(reading);
            newReadingPriority.setReadingPriority(jmRePri);
            mDbHelper.getDbDao(ReadingPriority.class).create(newReadingPriority);
        }
    }
    private void parseJmReadingRestriction(JmREle jmREle, Reading reading) throws SQLException {
        for (String jmReRestr : jmREle.getReRestr()){
            ReadingRestriction newReadingRestriction = new ReadingRestriction();
            newReadingRestriction.setFkReading(reading);
            newReadingRestriction.setReadingRestriction(jmReRestr);
            mDbHelper.getDbDao(ReadingRestriction.class).create(newReadingRestriction);
        }
    }
}
