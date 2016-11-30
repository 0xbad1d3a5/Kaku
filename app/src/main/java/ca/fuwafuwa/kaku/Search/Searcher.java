package ca.fuwafuwa.kaku.Search;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.Database.Models.Entry;
import ca.fuwafuwa.kaku.Database.Models.Kanji;

/**
 * Created by 0x1bad1d3a on 8/28/2016.
 */
public class Searcher {

    private static Searcher instance;

    private DatabaseHelper mDbHelper;
    Dao<Kanji, Integer> mKanjiDao;
    Dao<Entry, Integer> mEntryDao;

    private Searcher(Context context) throws SQLException {
        mDbHelper = DatabaseHelper.instance(context);
        mKanjiDao = mDbHelper.getJmDao(Kanji.class);
        mEntryDao = mDbHelper.getJmDao(Entry.class);
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
        List<Kanji> kanjis = mKanjiDao.queryBuilder().where().like(Kanji.KANJI_FIELD, character + "%").query();
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
        return new Match(kanjiText, kanji.getFkEntry(), length);
    }

    private Match findPotentialMatches(String text, int textOffset, Kanji kanji){
        return null;
    }
}
