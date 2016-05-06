package ca.fuwafuwa.kaku;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Xyresic on 5/5/2016.
 */
public class KakuTools {

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
}
