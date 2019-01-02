package ca.fuwafuwa.kaku.Ocr

import android.graphics.Bitmap
import ca.fuwafuwa.kaku.Windows.CaptureWindow

/**
 * Created by 0xbad1d3a5 on 5/2/2016.
 */
class OcrResult(val bitmap: Bitmap,
                val boxParams: BoxParams,
                val ocrChars: List<OcrChar>,
                val instant: Boolean,
                val captureWindow: CaptureWindow,
                private val mOcrTime: Long)
{
    val text: String
        get()
        {
            val sb = StringBuilder()

            for (ocrChar in ocrChars)
            {
                sb.append(ocrChar.bestChoice)
            }

            return sb.toString()
        }

    val message: String
        get() = String.format("OCR Time: %.2fs", mOcrTime / 1000.0)

    override fun toString(): String
    {
        return String.format("%s\nOcrTime: %d\nInstant: %b", text, mOcrTime, instant)
    }
}
