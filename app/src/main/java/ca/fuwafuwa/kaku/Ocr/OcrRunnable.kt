package ca.fuwafuwa.kaku.Ocr

import android.content.Context
import android.graphics.Bitmap
import android.os.Message
import android.util.Log

import com.googlecode.tesseract.android.ResultIterator
import com.googlecode.tesseract.android.TessBaseAPI

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.HashMap

import ca.fuwafuwa.kaku.*
import ca.fuwafuwa.kaku.Interfaces.Stoppable
import ca.fuwafuwa.kaku.MainService
import ca.fuwafuwa.kaku.Windows.CaptureWindow
import ca.fuwafuwa.kaku.Windows.Data.ChoiceCertainty
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar
import ca.fuwafuwa.kaku.Windows.Data.SquareCharOcr

/**
 * Created by 0xbad1d3a5 on 4/16/2016.
 */
class OcrRunnable(context: Context, private var mCaptureWindow: CaptureWindow?) : Runnable, Stoppable
{
    private val mContext: MainService = context as MainService
    private val mOcrLock = java.lang.Object()
    private val mSimilarChars = loadSimilarChars()
    private val mCommonMistakes = loadCommonMistakes()

    private var mTessBaseAPI: TessBaseAPI? = null
    private var mThreadRunning = true
    private var mTessReady = false
    private var mOcrParams: OcrParams? = null

    val isReadyForOcr: Boolean
        get() = mOcrParams == null

    init
    {
        mOcrParams = null
    }

    override fun run()
    {
        mTessBaseAPI = TessBaseAPI()
        val storagePath = mContext.filesDir.absolutePath
        mTessBaseAPI!!.init(storagePath, "jpn")

        mTessReady = true

        while (mThreadRunning)
        {
            Log.d(TAG, "THREAD STARTING NEW LOOP")

            try
            {
                 synchronized(mOcrLock)
                 {
                    if (!mThreadRunning)
                    {
                        return@synchronized
                    }

                    Log.d(TAG, "WAITING")
                    mOcrLock.wait()
                    Log.d(TAG, "THREAD STOPPED WAITING")

                    if (mOcrParams == null)
                    {
                        Log.d(TAG, "OcrRunnable - OcrParams null")
                        return@synchronized
                    }

                    Log.d(TAG, "Processing OCR with params " + mOcrParams!!.toString())

                    val startTime = System.currentTimeMillis()

                    when (mOcrParams!!.textDirection)
                    {
                        TextDirection.HORIZONTAL -> mTessBaseAPI!!.pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK
                        TextDirection.VERTICAL -> mTessBaseAPI!!.pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT
                    }

                    saveBitmap(mOcrParams!!.bitmap)

                    mCaptureWindow!!.showLoadingAnimation()

                    mTessBaseAPI!!.setImage(mOcrParams!!.bitmap)
                    mTessBaseAPI!!.getHOCRText(0)
                    val displayData = getDisplayData(mOcrParams!!, mTessBaseAPI!!.resultIterator)
                    processDisplayData(displayData)
                    mTessBaseAPI!!.clear()

                    if (displayData.text.length > 0)
                    {
                        val ocrTime = System.currentTimeMillis() - startTime
                        sendOcrResultToContext(OcrResult(displayData, ocrTime))
                    } else
                    {
                        sendToastToContext("No Characters Recognized.")
                    }

                    mCaptureWindow!!.stopLoadingAnimation(mOcrParams!!.instantMode)

                    mOcrParams = null
                }
            } catch (e: Exception)
            {
                e.printStackTrace()
            }
        }

        Log.d(TAG, "THREAD STOPPED")
    }

    /**
     * Unblocks the thread and starts OCR
     */
    fun runTess(ocrParams: OcrParams)
    {
        synchronized(mOcrLock)
        {
            if (!mThreadRunning || !mTessReady)
            {
                return
            }

            mOcrParams = ocrParams
            mTessBaseAPI!!.stop()
            mOcrLock.notify()

            Log.d(TAG, "NOTIFIED")
        }
    }

    /**
     * Cancels OCR recognition in progress if Tesseract has been started
     */
    fun cancel()
    {
        mTessBaseAPI!!.stop()
        Log.d(TAG, "CANCELED")
    }

    /**
     * Cancels any OCR recognition in progress and stops any further OCR attempts
     */
    override fun stop()
    {
        synchronized(mOcrLock)
        {
            mThreadRunning = false
            mOcrParams = null
            mCaptureWindow = null

            if (mTessBaseAPI != null)
            {
                mTessBaseAPI!!.stop()
                mTessBaseAPI!!.end()
            }

            mOcrLock.notify()
        }
    }

    private fun processDisplayData(displayData: DisplayDataOcr)
    {
        for (squareChar in displayData.squareChars as List<SquareCharOcr>)
        {
            val similarChars = mSimilarChars[squareChar.char]

            if (similarChars != null)
            {
                for (c in similarChars)
                {
                    squareChar.addChoice(c, ChoiceCertainty.UNCERTAIN)
                }
            }
        }

        for (squareChar in displayData.squareChars as List<SquareCharOcr>)
        {
            correctCommonMistake(squareChar, "く")
            correctCommonMistake(squareChar, "し")
            correctCommonMistake(squareChar, "じ")
            correctCommonMistake(squareChar, "え")
            correctCommonMistake(squareChar, "、")
            correctCommonMistake(squareChar, "。")

            correctKanjiOne(squareChar)
            correctKatakanaDash(squareChar)
        }
    }

    private fun correctCommonMistake(squareChar: SquareCharOcr, char: String)
    {
        if (mCommonMistakes[squareChar.char] == char)
        {
            val prev = squareChar.prev
            val next = squareChar.next

            if (prev?.char != null && LangUtils.IsJapaneseChar(prev.char[0]) ||
                next?.char != null && LangUtils.IsJapaneseChar(next.char[0]))
            {
                squareChar.addChoice(char, ChoiceCertainty.CERTAIN)
            }
        }
    }

    private fun correctKatakanaDash(squareChar: SquareCharOcr)
    {
        if (mCommonMistakes[squareChar.char] != null)
        {
            val prev = squareChar.prev

            if (prev?.char != null && LangUtils.IsKatakana(prev.char[0]))
            {
                squareChar.addChoice("ー", ChoiceCertainty.CERTAIN)
            }
        }
    }

    private fun correctKanjiOne(squareChar: SquareCharOcr)
    {
        if (mCommonMistakes[squareChar.char] != null)
        {
            val next = squareChar.next

            if (next?.char != null && (LangUtils.IsKanji(next.char[0]) || LangUtils.IsHiragana(next.char[0])))
            {
                squareChar.addChoice("一", ChoiceCertainty.CERTAIN)
            }
        }
    }

    private fun getDisplayData(ocrParams: OcrParams, iterator: ResultIterator): DisplayDataOcr
    {
        val bitmap = mOcrParams!!.originalBitmap
        val boxParams = mOcrParams!!.box

        val ocrChars = ArrayList<SquareCharOcr>()
        val displayData = DisplayDataOcr(bitmap, boxParams, ocrParams.instantMode, ocrChars)

        if (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL))
        {
            iterator.begin()
        } else
        {
            return displayData
        }

        do
        {
            val c = iterator.symbolChoicesAndConfidence
            val choices = ArrayList<kotlin.Pair<String, Double>>()
            for (p in c) choices.add(kotlin.Pair(p.first as String, p.second as Double))
            val pos = iterator.getBoundingBox(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL)

            ocrChars.add(SquareCharOcr(displayData, choices, pos))
        } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL))

        iterator.delete()

        displayData.assignIndicies()

        return displayData
    }

    private fun loadSimilarChars(): HashMap<String, List<String>>
    {
        val similarChars = HashMap<String, List<String>>()

        for (list in OcrCorrection.CommonLookalikes)
        {
            for ((index, kana) in list.withIndex())
            {
                if (list.size == 1)
                {
                    continue
                }

                val kanaList: List<String> = when (index)
                {
                    0 -> list.takeLast(list.size - 1)
                    list.size - 1 -> list.take(list.size - 1)
                    else -> list.subList(0, index) + list.subList(index + 1, list.size)
                }

                if (similarChars.containsKey(kana))
                {
                    for (k in kanaList)
                    {
                        if (!similarChars[kana]!!.contains(k))
                        {
                            similarChars[kana] = kanaList + listOf(k)
                        }
                    }
                }
                else
                {
                    similarChars[kana] = kanaList
                }
            }
        }

        return similarChars
    }

    private fun loadCommonMistakes(): HashMap<String, String>
    {
        val commonMistakes = HashMap<String, String>()

        for (pair in OcrCorrection.CommonMistakes)
        {
            for (c in pair.first)
            {
                commonMistakes[c] = pair.second
            }
        }

        return commonMistakes
    }

    private fun sendOcrResultToContext(result: OcrResult)
    {
        Message.obtain(mContext.handler, 0, result).sendToTarget()
    }

    private fun sendToastToContext(message: String)
    {
        Message.obtain(mContext.handler, 0, message).sendToTarget()
    }

    @Throws(FileNotFoundException::class)
    private fun saveBitmap(bitmap: Bitmap, name: String = "screen")
    {
        val fs = String.format("%s/%s/%s_%d.png", mContext.filesDir.absolutePath, SCREENSHOT_FOLDER_NAME, name, System.nanoTime())
        Log.d(TAG, fs)
        val fos = FileOutputStream(fs)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }

    companion object
    {

        private val TAG = OcrRunnable::class.java.name
    }
}
