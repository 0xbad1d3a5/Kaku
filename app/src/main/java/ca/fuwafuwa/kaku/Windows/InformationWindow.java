package ca.fuwafuwa.kaku.Windows;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Joiner;

import ca.fuwafuwa.kaku.Database.DbOpenHelper;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;

/**
 * Created by Xyresic on 4/23/2016.
 */
public class InformationWindow extends Window implements GestureDetector.OnGestureListener{

    private static final String TAG = InformationWindow.class.getName();

    private static final float FLICK_THRESHOLD = -0.05f;
    private GestureDetector mGestureDetector = new GestureDetector(mContext, this);
    private float maxFlingVelocity;

    public InformationWindow(MainService context) {
        super(context, R.layout.info_window);

        maxFlingVelocity = ViewConfiguration.get(mContext).getScaledMaximumFlingVelocity();
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
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.gravity = Gravity.TOP | Gravity.CENTER;
        return params;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        if (mGestureDetector.onTouchEvent(e)){
            return true;
        }

        if (e.getAction() == MotionEvent.ACTION_UP){
            params.y = 0;
            mWindowManager.updateViewLayout(mWindow, params);
            return true;
        }

        return false;
    }

    @Override
    public void stop() {
        mWindow.animate().translationY(-getDisplaySize().y).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mWindow.setVisibility(View.INVISIBLE);
                InformationWindow.super.stop();
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        stop();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        Log.d(TAG, "onScroll");

        params.y = (int) (motionEvent1.getRawY() - motionEvent.getRawY());
        if (params.y > 0){
            params.y = 0;
        }
        mWindowManager.updateViewLayout(mWindow, params);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        float distanceMoved = motionEvent.getRawY() - motionEvent1.getRawY();

        Log.d(TAG, String.format("Fling strength: %f", v1 / maxFlingVelocity));
        Log.d(TAG, String.format("Distance moved: %f", distanceMoved));

        if ((v1 / maxFlingVelocity) < FLICK_THRESHOLD){
            stop();
            return true;
        }

        return false;
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
            sb.append(Joiner.on("\n").join(db.getEntries(kanji)));
            sb.append("\n");

            offset += Character.charCount(curr);
        }

        return sb.toString();
    }
}
