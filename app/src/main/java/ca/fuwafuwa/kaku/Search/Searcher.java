package ca.fuwafuwa.kaku.Search;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Entry;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Kanji;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Meaning;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Reading;

/**
 * Created by Xyresic on 8/28/2016.
 */
public class Searcher {

    private static Searcher instance;

    private JmDatabaseHelper mJmDbHelper;
    Dao<Kanji, Integer> mKanjiDao;
    Dao<Entry, Integer> mEntryDao;
    Dao<Meaning, Integer> mMeaningDao;
    Dao<Reading, Integer> mReadingDao;

    private Searcher(Context context) throws SQLException {
        mJmDbHelper = JmDatabaseHelper.instance(context);
        mKanjiDao = mJmDbHelper.getDbDao(Kanji.class);
        mEntryDao = mJmDbHelper.getDbDao(Entry.class);
        mMeaningDao = mJmDbHelper.getDbDao(Meaning.class);
        mReadingDao = mJmDbHelper.getDbDao(Reading.class);
    }

    public static synchronized Searcher instance(Context context){
        if (instance == null){
            try {
                instance = new Searcher(context);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public List<Match> search(String text, int textOffset) throws SQLException {
        
        String character = new String(new int[] { text.codePointAt(textOffset)}, 0, 1);
        List<Kanji> kanjis = mKanjiDao.queryBuilder().where().like(Kanji.KANJI_FIELD, character + "_%").query();
        List<Match> matches = new ArrayList<>();

        for (Kanji kanji : kanjis){
            Match match = findExactMatches(text, textOffset, kanji);
            if (match != null){
                matches.add(match);
            }
        }

        Collections.sort(matches);
        return matches;
    }

    private Match findExactMatches(String text, int textOffset, Kanji kanji) throws SQLException {

        String kanjiText = kanji.getKanji();

        int length = kanjiText.length();
        for (int offset = 0; offset < length;){

            int kanjiCodePoint = kanjiText.codePointAt(offset);
            int textCodePoint;

            try {
                textCodePoint = text.codePointAt(textOffset);
            }
            catch (IndexOutOfBoundsException e){
                return null;
            }

            if (kanjiCodePoint != textCodePoint){
                return findPotentialMatches(text, textOffset, kanji);
            }

            int characterOffset = Character.charCount(kanjiCodePoint);
            offset += characterOffset;
            textOffset += characterOffset;
        }

        mEntryDao.refresh(kanji.getFkEntry());
        //List<Reading> r = mReadingDao.queryBuilder().where().eq("fkEntry_id", kanji.getFkEntry().getId()).query();
        //List<Meaning> m = mMeaningDao.queryBuilder().where().eq("fkEntry_id", kanji.getFkEntry().getId()).query();
        return new Match(kanjiText, kanji.getFkEntry(), length);
    }

    private Match findPotentialMatches(String text, int textOffset, Kanji kanji){
        return null;
    }
}
