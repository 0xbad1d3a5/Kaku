package ca.fuwafuwa.kaku.Ocr

import android.graphics.Bitmap

data class OcrParams(val bitmap: Bitmap,
                     val originalBitmap: Bitmap,
                     val box: BoxParams,
                     val instantOcr: Boolean)
{
    override fun toString() : String {
        return "Box: $box InstantOCR: $instantOcr"
    }
}

