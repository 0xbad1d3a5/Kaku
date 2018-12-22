package ca.fuwafuwa.kaku.Search;

import android.content.Context;
import android.os.AsyncTask;

import com.atilika.kuromoji.TokenizerBase;
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized;

/**
 * Created by 0xbad1d3a5 on 12/16/2016.
 */

public class JmTask extends AsyncTask<Void, Void, List<EntryOptimized>> {

    public interface SearchJmTaskDone {
        void jmTaskCallback(List<EntryOptimized> results, SearchInfo searchInfo);
    }

    private SearchInfo mSearchInfo;
    private JmDatabaseHelper mJmDbHelper;
    private Dao<EntryOptimized, Integer> mEntryOptimizedDao;
    private SearchJmTaskDone mSearchJmTaskDone;
    private static Tokenizer tokenizer = new Tokenizer.Builder().mode(TokenizerBase.Mode.NORMAL).build();

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

            EntryOptimized a = getDeinflictedFormIfExists(mText);
            if (a != null){
                matchedEntries.add(0, a);
            }

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

    private EntryOptimized getDeinflictedFormIfExists(String text) throws SQLException {

        List<Token> tokens = tokenizer.tokenize(text);

        if (tokens.size() <= 1){
            return null;
        }

        if (tokens.get(1).getConjugationType() != "*"){
            return mEntryOptimizedDao.queryBuilder().where().eq("kanji", tokens.get(0).getBaseForm()).queryForFirst();
        }

        return null;
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
