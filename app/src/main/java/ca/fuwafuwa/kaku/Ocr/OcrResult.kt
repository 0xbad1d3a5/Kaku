package ca.fuwafuwa.kaku.Ocr

import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr

/**
 * Created by 0xbad1d3a5 on 5/2/2016.
 */
class OcrResult(val displayData: DisplayDataOcr,
                private val mOcrTime: Long)
{
    val text: String get() = displayData.text
    val message: String get() = String.format("OCR Time: %.2fs", mOcrTime / 1000.0)

    override fun toString(): String
    {
        return String.format("%s\nOcrTime: %d\nInstant: %b", text, mOcrTime, displayData.instantMode)
    }
}
