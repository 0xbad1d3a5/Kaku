package ca.fuwafuwa.kaku.Search

import android.content.Context
import android.os.AsyncTask

import com.atilika.kuromoji.TokenizerBase
import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer
import com.j256.ormlite.dao.Dao

import java.sql.SQLException
import java.util.ArrayList
import java.util.Collections

import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized
import ca.fuwafuwa.kaku.Deinflictor.DeinflectionInfo
import ca.fuwafuwa.kaku.Deinflictor.Deinflector

/**
 * Created by 0xbad1d3a5 on 12/16/2016.
 */

class JmTask @Throws(SQLException::class)
constructor(private val mSearchInfo: SearchInfo, private val mSearchJmTaskDone: SearchJmTaskDone, context: Context) : AsyncTask<Void, Void, List<EntryOptimized>>()
{
    private val mJmDbHelper: JmDatabaseHelper
    private val mEntryOptimizedDao: Dao<EntryOptimized, Int>
    private val mDeinflector: Deinflector

    interface SearchJmTaskDone
    {
        fun jmTaskCallback(results: List<EntryOptimized>, searchInfo: SearchInfo)
    }

    init
    {
        this.mJmDbHelper = JmDatabaseHelper.instance(context)
        this.mEntryOptimizedDao = mJmDbHelper.getDbDao(EntryOptimized::class.java)
        this.mDeinflector = Deinflector(context)
    }

    override fun doInBackground(vararg params: Void): List<EntryOptimized>?
    {

        val mText = mSearchInfo.text
        val mTextOffset = mSearchInfo.textOffset

        try
        {
            val character = String(intArrayOf(mText.codePointAt(mTextOffset)), 0, 1)
            val entries = mEntryOptimizedDao.queryBuilder().where().like("kanji", "$character%").query()
            val matchedEntries = ArrayList<EntryOptimized>()

            for (e in entries)
            {
                if (isMatch(mText, mTextOffset, e.kanji))
                {
                    matchedEntries.add(e)
                }
            }

            Collections.sort(matchedEntries)

//            matchedEntries.addAll(0, getDeinflictedFormsIfExists(mText, mTextOffset))

            return matchedEntries

        } catch (e: SQLException)
        {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(result: List<EntryOptimized>)
    {
        mSearchJmTaskDone.jmTaskCallback(result, mSearchInfo)
    }

    @Throws(SQLException::class)
    private fun getDeinflictedFormsIfExists(text: String, textOffset: Int): List<EntryOptimized>
    {
        val end = if (textOffset + 10 >= text.length) text.length - 1 else textOffset + 10
        var word = text.substring(textOffset, end)
        val matchedEntries = ArrayList<EntryOptimized>()

        while (word.length > 0)
        {

            val resultsList = mDeinflector.getPotentialDeinflections(word)

            for (result in resultsList)
            {
                val entry = mEntryOptimizedDao.queryBuilder().where().eq("kanji", result.word).queryForFirst()
                if (entry != null)
                {
                    matchedEntries.add(entry)
                }
            }
            word = word.substring(0, word.length - 1)
        }

        return matchedEntries
    }

    @Throws(SQLException::class)
    private fun isMatch(text: String, textOffset: Int, kanjiText: String): Boolean
    {
        var textOffset = textOffset

        val length = kanjiText.length
        var offset = 0
        while (offset < length)
        {

            val kanjiCodePoint = kanjiText.codePointAt(offset)
            val textCodePoint: Int

            try
            {
                textCodePoint = text.codePointAt(textOffset)
            } catch (e: IndexOutOfBoundsException)
            {
                return false
            }

            if (kanjiCodePoint != textCodePoint)
            {
                return false
            }

            val characterOffset = Character.charCount(kanjiCodePoint)
            offset += characterOffset
            textOffset += characterOffset
        }

        return true
    }
}
