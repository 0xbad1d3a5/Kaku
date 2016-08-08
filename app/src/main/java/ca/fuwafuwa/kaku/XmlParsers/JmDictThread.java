package ca.fuwafuwa.kaku.XmlParsers;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.j256.ormlite.misc.TransactionManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.Database.Models.Entry;
import ca.fuwafuwa.kaku.Database.Models.Kanji;
import ca.fuwafuwa.kaku.Database.Models.KanjiIrregularity;
import ca.fuwafuwa.kaku.Database.Models.KanjiPriority;
import ca.fuwafuwa.kaku.Database.Models.Meaning;
import ca.fuwafuwa.kaku.Database.Models.MeaningAdditionalInfo;
import ca.fuwafuwa.kaku.Database.Models.MeaningAntonym;
import ca.fuwafuwa.kaku.Database.Models.MeaningCrossReference;
import ca.fuwafuwa.kaku.Database.Models.MeaningDialect;
import ca.fuwafuwa.kaku.Database.Models.MeaningField;
import ca.fuwafuwa.kaku.Database.Models.MeaningGloss;
import ca.fuwafuwa.kaku.Database.Models.MeaningKanjiRestriction;
import ca.fuwafuwa.kaku.Database.Models.MeaningLoanSource;
import ca.fuwafuwa.kaku.Database.Models.MeaningMisc;
import ca.fuwafuwa.kaku.Database.Models.MeaningPartOfSpeech;
import ca.fuwafuwa.kaku.Database.Models.MeaningReadingRestriction;
import ca.fuwafuwa.kaku.Database.Models.Reading;
import ca.fuwafuwa.kaku.Database.Models.ReadingIrregularity;
import ca.fuwafuwa.kaku.Database.Models.ReadingPriority;
import ca.fuwafuwa.kaku.Database.Models.ReadingRestriction;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmEntry;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmGloss;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmKEle;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmLsource;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmREle;
import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmSense;

/**
 * Created by Xyresic on 4/30/2016.
 */
public class JmDictThread implements Runnable {

    private static final String TAG = JmDictThread.class.getName();

    private Context mContext;
    private FileInputStream mJmDictXml;
    private XmlPullParser mParser;
    private DatabaseHelper dbHelper;

    public JmDictThread(Context context) {
        mContext = context;
        dbHelper = DatabaseHelper.getHelper(mContext);
    }

    @Override
    public void run() {

        long startTime = System.currentTimeMillis();

        try {
            mParser = Xml.newPullParser();
            //mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            File file = new File(mContext.getExternalFilesDir(null), "JMDictOriginal.xml");
            mJmDictXml = new FileInputStream(file);
            mParser.setInput(mJmDictXml, null);

            TransactionManager.callInTransaction(DatabaseHelper.getHelper(mContext).getConnectionSource(), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    parseJmDict(mParser);
                    return null;
                }
            });
        } catch (SQLException e){
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, String.format("FINISHED DICT, TOOK %d", System.currentTimeMillis() - startTime));
    }

    private void parseJmDict(XmlPullParser parser) throws IOException, XmlPullParserException, SQLException {

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

    private void parseJmEntry(XmlPullParser parser) throws IOException, XmlPullParserException, SQLException {

        JmEntry jmEntry = new JmEntry(parser);

        Entry newEntry = new Entry();
        newEntry.setEntry(jmEntry.getEntSeq());
        dbHelper.getJmDao(Entry.class).create(newEntry);

        parseJmKanji(jmEntry, newEntry);
        parseJmMeaning(jmEntry, newEntry);
        parseJmReading(jmEntry, newEntry);

        //Log.d(TAG, String.format("Parsed Entry %d", newEntry.getEntry()));
    }

    private void parseJmKanji(JmEntry jmEntry, Entry entry) throws SQLException {
        for (JmKEle jmKanji : jmEntry.getKEle()){
            Kanji newKanji = new Kanji();
            newKanji.setFkEntry(entry);
            newKanji.setKanji(jmKanji.getKeb());
            dbHelper.getJmDao(Kanji.class).create(newKanji);

            parseJmKanjiIrregularity(jmKanji, newKanji);
            parseJmKanjiPriority(jmKanji, newKanji);
        }
    }

    private void parseJmKanjiIrregularity(JmKEle jmKEle, Kanji kanji) throws SQLException {
        for (String jmKeInf : jmKEle.getKeInf()){
            KanjiIrregularity newKanjiIrregularity = new KanjiIrregularity();
            newKanjiIrregularity.setFkKanji(kanji);
            newKanjiIrregularity.setKanjiIrregularity(jmKeInf);
            dbHelper.getJmDao(KanjiIrregularity.class).create(newKanjiIrregularity);
        }
    }

    private void parseJmKanjiPriority(JmKEle jmKEle, Kanji kanji) throws SQLException {
        for (String jmKePri : jmKEle.getKePri()){
            KanjiPriority newKanjiPriority = new KanjiPriority();
            newKanjiPriority.setFkKanji(kanji);
            newKanjiPriority.setKanjiPriority(jmKePri);
            dbHelper.getJmDao(KanjiPriority.class).create(newKanjiPriority);
        }
    }

    private void parseJmMeaning(JmEntry jmEntry, Entry entry) throws SQLException {
        for (JmSense jmSense : jmEntry.getSense()){
            Meaning newMeaning = new Meaning();
            newMeaning.setFkEntry(entry);
            dbHelper.getJmDao(Meaning.class).create(newMeaning);

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
            dbHelper.getJmDao(MeaningAdditionalInfo.class).create(newMeaningAdditionalInfo);
        }
    }

    private void parseJmMeaningAntonym(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmAnt : jmSense.getAnt()){
            MeaningAntonym newMeaningAntonym = new MeaningAntonym();
            newMeaningAntonym.setFkMeaning(meaning);
            newMeaningAntonym.setAntonym(jmAnt);
            dbHelper.getJmDao(MeaningAntonym.class).create(newMeaningAntonym);
        }
    }

    private void parseJmMeaningCrossReference(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmXRef : jmSense.getXRef()){
            MeaningCrossReference newMeaningCrossReference = new MeaningCrossReference();
            newMeaningCrossReference.setFkMeaning(meaning);
            newMeaningCrossReference.setCrossReference(jmXRef);
            dbHelper.getJmDao(MeaningCrossReference.class).create(newMeaningCrossReference);
        }
    }

    private void parseJmMeaningDialect(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmDial : jmSense.getDial()){
            MeaningDialect newMeaningDialect = new MeaningDialect();
            newMeaningDialect.setFkMeaning(meaning);
            newMeaningDialect.setDialect(jmDial);
            dbHelper.getJmDao(MeaningDialect.class).create(newMeaningDialect);
        }
    }

    private void parseJmMeaningField(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmField : jmSense.getField()){
            MeaningField newMeaningField = new MeaningField();
            newMeaningField.setFkMeaning(meaning);
            newMeaningField.setField(jmField);
            dbHelper.getJmDao(MeaningField.class).create(newMeaningField);
        }
    }

    private void parseJmMeaningGloss(JmSense jmSense, Meaning meaning) throws SQLException {
        for (JmGloss jmGloss : jmSense.getGloss()){
            MeaningGloss newMeaningGloss = new MeaningGloss();
            newMeaningGloss.setFkMeaning(meaning);
            newMeaningGloss.setGender(jmGloss.getGender());
            newMeaningGloss.setGloss(jmGloss.getText());
            newMeaningGloss.setLang(jmGloss.getLang());
            dbHelper.getJmDao(MeaningGloss.class).create(newMeaningGloss);
        }
    }

    private void parseJmMeaningKanjiRestriction(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmStagk : jmSense.getStagk()){
            MeaningKanjiRestriction newMeaningKanjiRestriction = new MeaningKanjiRestriction();
            newMeaningKanjiRestriction.setFkMeaning(meaning);
            newMeaningKanjiRestriction.setKanjiRestriction(jmStagk);
            dbHelper.getJmDao(MeaningKanjiRestriction.class).create(newMeaningKanjiRestriction);
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
            dbHelper.getJmDao(MeaningLoanSource.class).create(newMeaningLoanSource);
        }
    }

    private void parseJmMeaningMisc(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmMisc : jmSense.getMisc()){
            MeaningMisc newMeaningMisc = new MeaningMisc();
            newMeaningMisc.setFkMeaning(meaning);
            newMeaningMisc.setMisc(jmMisc);
            dbHelper.getJmDao(MeaningMisc.class).create(newMeaningMisc);
        }
    }

    private void parseJmMeaningPartOfSpeech(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmPos : jmSense.getPos()){
            MeaningPartOfSpeech newMeaningPartOfSpeech = new MeaningPartOfSpeech();
            newMeaningPartOfSpeech.setFkMeaning(meaning);
            newMeaningPartOfSpeech.setPartOfSpeech(jmPos);
            dbHelper.getJmDao(MeaningPartOfSpeech.class).create(newMeaningPartOfSpeech);
        }
    }

    private void parseJmMeaningReadingRestriction(JmSense jmSense, Meaning meaning) throws SQLException {
        for (String jmStagr : jmSense.getStagr()){
            MeaningReadingRestriction newMeaningReadingRestriction = new MeaningReadingRestriction();
            newMeaningReadingRestriction.setFkMeaning(meaning);
            newMeaningReadingRestriction.setReadingRestriction(jmStagr);
            dbHelper.getJmDao(MeaningReadingRestriction.class).create(newMeaningReadingRestriction);
        }
    }

    private void parseJmReading(JmEntry jmEntry, Entry entry) throws SQLException {
        for (JmREle jmREle : jmEntry.getREle()){
            Reading newReading = new Reading();
            newReading.setFkEntry(entry);
            newReading.setReading(jmREle.getReb());
            newReading.setFalseReading(jmREle.getReNoKanji());
            dbHelper.getJmDao(Reading.class).create(newReading);

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
            dbHelper.getJmDao(ReadingIrregularity.class).create(newReadingIrregularity);
        }
    }

    private void parseJmReadingPriority(JmREle jmREle, Reading reading) throws SQLException {
        for (String jmRePri : jmREle.getRePri()){
            ReadingPriority newReadingPriority = new ReadingPriority();
            newReadingPriority.setFkReading(reading);
            newReadingPriority.setReadingPriority(jmRePri);
            dbHelper.getJmDao(ReadingPriority.class).create(newReadingPriority);
        }
    }
    private void parseJmReadingRestriction(JmREle jmREle, Reading reading) throws SQLException {
        for (String jmReRestr : jmREle.getReRestr()){
            ReadingRestriction newReadingRestriction = new ReadingRestriction();
            newReadingRestriction.setFkReading(reading);
            newReadingRestriction.setReadingRestriction(jmReRestr);
            dbHelper.getJmDao(ReadingRestriction.class).create(newReadingRestriction);
        }
    }
}
