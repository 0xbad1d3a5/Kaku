package ca.fuwafuwa.kaku.Search;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized;

/**
 * Created by Xyresic on 12/16/2016.
 */

public class JmTask extends AsyncTask<Void, Void, List<EntryOptimized>> {

    public interface SearchJmTaskDone {
        void jmTaskCallback(List<EntryOptimized> results, SearchInfo searchInfo);
    }

    private SearchInfo mSearchInfo;
    private JmDatabaseHelper mJmDbHelper;
    private Dao<EntryOptimized, Integer> mEntryOptimizedDao;
    private SearchJmTaskDone mSearchJmTaskDone;

    public JmTask(SearchInfo searchInfo, SearchJmTaskDone taskDone, Context context) throws SQLException {
        this.mSearchInfo = searchInfo;
        this.mSearchJmTaskDone = taskDone;
        this.mJmDbHelper = JmDatabaseHelper.instance(context);
        this.mEntryOptimizedDao = mJmDbHelper.getDbDao(EntryOptimized.class);
    }

    @Override
    protected List<EntryOptimized> doInBackground(Void... params) {

        String mText = mSearchInfo.getText();
        int mTextOffset = mSearchInfo.getTextOffset();

        try {
            String character = new String(new int[]{mText.codePointAt(mTextOffset)}, 0, 1);
            List<EntryOptimized> entries = mEntryOptimizedDao.queryBuilder().where().like("kanji", character + "%").query();
            List<EntryOptimized> matchedEntries = new ArrayList<>();

            for (EntryOptimized e : entries) {
                if (isMatch(mText, mTextOffset, e.getKanji())) {
                    matchedEntries.add(e);
                }
            }

            Collections.sort(matchedEntries);
            return matchedEntries;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<EntryOptimized> result){
        mSearchJmTaskDone.jmTaskCallback(result, mSearchInfo);
    }

    private boolean isMatch(String text, int textOffset, String kanjiText) throws SQLException {

        int length = kanjiText.length();
        for (int offset = 0; offset < length;){

            int kanjiCodePoint = kanjiText.codePointAt(offset);
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
