package ca.fuwafuwa.kaku.Search

import android.content.Context
import android.os.AsyncTask

import com.j256.ormlite.dao.Dao

import java.sql.SQLException

import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Kd2DatabaseHelper
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Models.CharacterOptimized

/**
 * Created by 0xbad1d3a5 on 12/16/2016.
 */

class Kd2Task @Throws(SQLException::class)
constructor(private val mSearchInfo: SearchInfo, private val mSearchKd2TaskDone: SearchKd2TaskDone, context: Context) : AsyncTask<Void, Void, List<CharacterOptimized>>()
{
    companion object
    {
        private val TAG = Kd2Task::class.java.name
    }

    private val mKd2DbHelper: Kd2DatabaseHelper
    private val mCharacterOptimizedDao: Dao<CharacterOptimized, Int>

    interface SearchKd2TaskDone
    {
        fun kd2TaskCallback(results: List<CharacterOptimized>, searchInfo: SearchInfo)
    }

    init
    {
        this.mKd2DbHelper = Kd2DatabaseHelper.instance(context)
        this.mCharacterOptimizedDao = mKd2DbHelper.getDbDao(CharacterOptimized::class.java)
    }

    override fun doInBackground(vararg params: Void): List<CharacterOptimized>
    {
        val character = String(intArrayOf(mSearchInfo.text.codePointAt(mSearchInfo.textOffset)), 0, 1).replace("%", "\\%")
        return mCharacterOptimizedDao.queryBuilder().where().like("kanji", "$character%").query().toList()
    }

    override fun onPostExecute(result: List<CharacterOptimized>)
    {
        mSearchKd2TaskDone.kd2TaskCallback(result, mSearchInfo)
    }
}
