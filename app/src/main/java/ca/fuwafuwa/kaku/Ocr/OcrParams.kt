package ca.fuwafuwa.kaku.Ocr

import android.graphics.Bitmap
import ca.fuwafuwa.kaku.TextDirection

data class OcrParams(val bitmap: Bitmap,
                     val originalBitmap: Bitmap,
                     val box: BoxParams,
                     val textDirection: TextDirection,
                     val instantMode: Boolean)
{
    override fun toString() : String {
        return "Box: $box InstantOCR: $instantMode TextDirection: $textDirection"
    }
}

