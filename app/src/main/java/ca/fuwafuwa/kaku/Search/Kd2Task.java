package ca.fuwafuwa.kaku.Search;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Kd2DatabaseHelper;
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Models.CharacterOptimized;

/**
 * Created by 0x1bad1d3a on 12/16/2016.
 */

public class Kd2Task extends AsyncTask<Void, Void, List<CharacterOptimized>> {

    public interface SearchKd2TaskDone {
        void kd2TaskCallback(List<CharacterOptimized> results, SearchInfo searchInfo);
    }

    private SearchInfo mSearchInfo;
    private Kd2DatabaseHelper mKd2DbHelper;
    private Dao<CharacterOptimized, Integer> mCharacterOptimizedDao;
    private SearchKd2TaskDone mSearchKd2TaskDone;

    public Kd2Task(SearchInfo searchInfo, SearchKd2TaskDone taskDone, Context context) throws SQLException {
        this.mSearchInfo = searchInfo;
        this.mSearchKd2TaskDone = taskDone;
        this.mKd2DbHelper = Kd2DatabaseHelper.instance(context);
        this.mCharacterOptimizedDao = mKd2DbHelper.getDbDao(CharacterOptimized.class);
    }

    @Override
    protected List<CharacterOptimized> doInBackground(Void... params) {

        try {

            String character = new String(new int[]{mSearchInfo.getText().codePointAt(mSearchInfo.getTextOffset())}, 0, 1);
            List<CharacterOptimized> characters = mCharacterOptimizedDao.queryBuilder().where().like("kanji", character + "%").query();
            return characters;

        } catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<CharacterOptimized> result){
        mSearchKd2TaskDone.kd2TaskCallback(result, mSearchInfo);
    }

}
