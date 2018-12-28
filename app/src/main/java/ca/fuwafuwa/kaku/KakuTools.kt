@file:JvmName("KakuTools")

package ca.fuwafuwa.kaku

import android.content.Context
import android.util.DisplayMetrics

import com.google.common.base.Joiner
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList

private const val TAG = "KakuTools"
private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create()

fun toJson(obj: Any): String
{
    return gson.toJson(obj)
}

fun dpToPx(context: Context, dp: Int): Int
{
    val displayMetrics = context.resources.displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun pxToDp(context: Context, px: Int): Int
{
    val displayMetrics = context.resources.displayMetrics
    return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

/**
 * Splits `text` into individual unicode characters as a list of strings
 * @param text Text to split
 * @return List of strings with each string representing one unicode character
 */
fun splitTextByChar(text: String): List<String>
{

    val charList = ArrayList<String>()

    val length = text.length
    var offset = 0
    while (offset < length)
    {
        val curr = text.codePointAt(offset)
        val charz = String(intArrayOf(curr), 0, 1)
        charList.add(charz)
        offset += Character.charCount(curr)
    }

    return charList
}