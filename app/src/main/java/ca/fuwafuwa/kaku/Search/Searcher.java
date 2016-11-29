package ca.fuwafuwa.kaku.Search;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.Database.Models.Entry;
import ca.fuwafuwa.kaku.Database.Models.Kanji;

/**
 * Created by 0x1bad1d3a on 8/28/2016.
 */
public class Searcher {

    private static Searcher instance;
    private DatabaseHelper dbHelper;

    private Searcher(Context context){
        dbHelper = DatabaseHelper.instance(context);
    }

    public static synchronized Searcher instance(Context context){
        if (instance == null){
            instance = new Searcher(context);
        }
        return instance;
    }

    public List<Entry> search(String text, int offset) throws SQLException {
        
        String character = new String(new int[] { text.codePointAt(offset)}, 0, 1);

        Dao<Kanji, Integer> kanjiDao = dbHelper.getJmDao(Kanji.class);
        Dao<Entry, Integer> entryDao = dbHelper.getJmDao(Entry.class);
        List<Kanji> kanjis = kanjiDao.queryBuilder().where().like(Kanji.KANJI_FIELD, character + "%").query();
        List<Entry> entries = new ArrayList<>();

        for (Kanji kanji : kanjis){
            if(isMatch(text, kanji.getKanji(), offset)){
                Entry entry = kanji.getFkEntry();
                entryDao.refresh(entry);
                entries.add(entry);
            }
        }

        return entries;
    }

    private boolean isMatch(String text, String kanji, int textOffset){

        int length = kanji.length();
        for (int offset = 0; offset < length;){

            int kanjiCodePoint = kanji.codePointAt(offset);
            int textCodePoint;

            try {
                textCodePoint = text.codePointAt(textOffset);
            }
            catch (IndexOutOfBoundsException e){
                return false;
            }

            if (kanjiCodePoint != textCodePoint){
                return false;
            }

            int characterOffset = Character.charCount(kanjiCodePoint);
            offset += characterOffset;
            textOffset += characterOffset;
        }

        return true;
    }
}
