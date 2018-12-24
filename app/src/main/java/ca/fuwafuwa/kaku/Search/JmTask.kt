package ca.fuwafuwa.kaku.Search

import android.content.Context
import android.os.AsyncTask
import android.util.Log

import com.j256.ormlite.dao.Dao

import java.sql.SQLException
import java.util.ArrayList

import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized
import ca.fuwafuwa.kaku.Deinflictor.DeinflectionInfo
import ca.fuwafuwa.kaku.Deinflictor.Deinflector

/**
 * Created by 0xbad1d3a5 on 12/16/2016.
 */

class JmTask @Throws(SQLException::class)
constructor(private val mSearchInfo: SearchInfo, private val mSearchJmTaskDone: SearchJmTaskDone, context: Context) : AsyncTask<Void, Void, List<JmSearchResult>>()
{
    companion object
    {
        private val TAG = JmTask::class.java.getName()
    }

    private val mJmDbHelper: JmDatabaseHelper
    private val mEntryOptimizedDao: Dao<EntryOptimized, Int>
    private val mDeinflector: Deinflector

    interface SearchJmTaskDone
    {
        fun jmTaskCallback(results: List<JmSearchResult>, searchInfo: SearchInfo)
    }

    init
    {
        this.mJmDbHelper = JmDatabaseHelper.instance(context)
        this.mEntryOptimizedDao = mJmDbHelper.getDbDao(EntryOptimized::class.java)
        this.mDeinflector = Deinflector(context)
    }

    override fun doInBackground(vararg params: Void): List<JmSearchResult>
    {

        val mText = mSearchInfo.text
        val mTextOffset = mSearchInfo.textOffset

        try
        {
            val startDictTime = System.currentTimeMillis()
            val character = String(intArrayOf(mText.codePointAt(mTextOffset)), 0, 1)
            val entries: Map<String, EntryOptimized> = mEntryOptimizedDao.queryBuilder().where().like("kanji", "$character%").query().associate { it.kanji to it }
            val matchedEntries = getMatchedEntries(mText, mTextOffset, entries)
            Log.d(TAG, "Dict lookup time: ${System.currentTimeMillis() - startDictTime}")

            return matchedEntries

        } catch (e: SQLException)
        {
            e.printStackTrace()
        }

        return ArrayList()
    }

    override fun onPostExecute(result: List<JmSearchResult>)
    {
        mSearchJmTaskDone.jmTaskCallback(result, mSearchInfo)
    }

    @Throws(SQLException::class)
    private fun getMatchedEntries(text: String, textOffset: Int, entries: Map<String, EntryOptimized>): List<JmSearchResult>
    {
        val end = if (textOffset + 80 >= text.length) text.length else textOffset + 80
        var word = text.substring(textOffset, end)
        val seenEntries = HashSet<EntryOptimized>()
        val results = ArrayList<JmSearchResult>()

        while (word.isNotEmpty())
        {
            val deinfResultsList: List<DeinflectionInfo> = mDeinflector.getPotentialDeinflections(word)

            var count = 0
            for (deinfInfo in deinfResultsList)
            {
                val entry: EntryOptimized? = entries[deinfInfo.word]

                if (entry == null || seenEntries.contains(entry))
                {
                    continue
                }

                var valid = true
                if (count > 0)
                {
                    valid = (deinfInfo.type and 1 != 0) && (entry.pos.contains("v1")) ||
                            (deinfInfo.type and 2 != 0) && (entry.pos.contains("v5")) ||
                            (deinfInfo.type and 4 != 0) && (entry.pos.contains("adj-i")) ||
                            (deinfInfo.type and 8 != 0) && (entry.pos.contains("vk")) ||
                            (deinfInfo.type and 16 != 0) && (entry.pos.contains("vs-"))
                }

                if (valid){
                    results.add(JmSearchResult(entry, deinfInfo, word))
                    seenEntries.add(entry)
                }

                count++
            }
            word = word.substring(0, word.length - 1)
        }

        return results
    }
}
