package ca.fuwafuwa.kaku;

import android.content.Context;
import android.util.DisplayMetrics;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 0x1bad1d3a on 5/5/2016.
 */
public class KakuTools {

    private static final String TAG = KakuTools.class.getName();

    private static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();

    public static String toJson(Object obj){
        return gson.toJson(obj);
    }

    public static int dpToPx(Context context, int dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    /**
     * Splits {@code text} into individual unicode characters as a list of strings
     * @param text Text to split
     * @return List of strings with each string representing one unicode character
     */
    public static List<String> splitTextByChar(String text){

        List<String> charList = new ArrayList<>();

        int length = text.length();
        for (int offset = 0; offset < length;){
            int curr = text.codePointAt(offset);
            String charz = new String(new int[] { curr }, 0, 1);
            charList.add(charz);
            offset += Character.charCount(curr);
        }

        return charList;
    }

    public static String join(Iterable<?> parts){
        if (parts == null){
            return "";
        }
        else {
            return Joiner.on(", ").join(parts);
        }
    }
}
