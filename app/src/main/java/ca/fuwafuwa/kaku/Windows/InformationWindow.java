package ca.fuwafuwa.kaku.Windows;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import ca.fuwafuwa.kaku.Database.DbOpenHelper;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;

/**
 * Created by Xyresic on 4/23/2016.
 */
public class InformationWindow extends Window {

    private static final String TAG = InformationWindow.class.getName();

    public InformationWindow(MainService context) {
        super(context, R.layout.info_window);
    }

    private String searchDict(String text){

        DbOpenHelper db = new DbOpenHelper(mContext);
        StringBuilder sb = new StringBuilder();
        sb.append(text);
        sb.append("\n");

        int length = text.length();
        for (int offset = 0; offset < length; ){
            int curr = text.codePointAt(offset);

            String kanji = new String(new int[] { curr }, 0, 1);
            sb.append(db.getEntry(kanji).toString());
            sb.append("\n");

            offset += Character.charCount(curr);
        }

        return sb.toString();
    }

    public void setText(String text){
        TextView tv = (TextView) mWindow.findViewById(R.id.info_text);
        long startTime = System.currentTimeMillis();
        tv.setText(searchDict(text));
        String timeTaken = String.format("Search Time: %d", System.currentTimeMillis() - startTime);
        tv.postInvalidate();

        Log.d(TAG, timeTaken);
        Toast.makeText(mContext, timeTaken, Toast.LENGTH_LONG).show();
    }

    @Override
    protected WindowManager.LayoutParams getDefaultParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            -1,
            -1,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    @Override
    public boolean onMoveEvent(MotionEvent e){
        stop();
        return true;
    }

    @Override
    protected void cleanup() {
    }
}
