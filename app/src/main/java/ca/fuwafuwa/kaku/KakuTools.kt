@file:JvmName("KakuTools")

package ca.fuwafuwa.kaku

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics

import com.google.gson.GsonBuilder

import java.util.ArrayList

private const val TAG = "KakuTools"
private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create()

enum class TextDirection(val value: Int) {
    AUTO(0),
    HORIZONTAL(1),
    VERTICAL(2);

    companion object {
        private val values = values();
        fun getByValue(value: Int) = values.firstOrNull { it.value == value }
    }
}

data class Prefs(val textDirectionSetting: TextDirection,
                 val imageFilterSetting: Boolean,
                 val instantModeSetting: Boolean);

fun getPrefs(context: Context): Prefs
{
    val prefs = context.getSharedPreferences(KAKU_PREF_FILE, Context.MODE_PRIVATE)

    return Prefs(TextDirection.valueOf(
            prefs.getString(KAKU_PREF_TEXT_DIRECTION, TextDirection.AUTO.toString())),
            prefs.getBoolean(KAKU_PREF_IMAGE_FILTER, true),
            prefs.getBoolean(KAKU_PREF_INSTANT_MODE, true))
}

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

fun startKakuService(context: Context, i: Intent)
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    {
        context.startForegroundService(i)
    }
    else
    {
        context.startService(i)
    }
}