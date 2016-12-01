package ca.fuwafuwa.kaku.Windows;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Search.Match;
import ca.fuwafuwa.kaku.Search.Searcher;
import ca.fuwafuwa.kaku.Windows.Interfaces.KanjiViewListener;
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView;

/**
 * Created by 0x1bad1d3a on 4/23/2016.
 */
public class InformationWindow extends Window implements GestureDetector.OnGestureListener, KanjiViewListener {

    private static final String TAG = InformationWindow.class.getName();
    private static final float FLICK_THRESHOLD = -0.05f;

    private GestureDetector mGestureDetector;
    private float mMaxFlingVelocity;
    private KanjiGridView mKanjiGrid;
    private Searcher mSearcher;
    private String mText;

    public InformationWindow(MainService context) {
        super(context, R.layout.info_window);

        mMaxFlingVelocity = ViewConfiguration.get(this.context).getScaledMaximumFlingVelocity();
        mGestureDetector = new GestureDetector(this.context, this);
        mKanjiGrid = (KanjiGridView) window.findViewById(R.id.kanji_grid);
        mSearcher = Searcher.instance(context);
    }

    public void setText(String text){
        this.mText = text;
        mKanjiGrid.setText(this, text);
    }

    public void onKanjiViewScroll(KanjiCharacterView kanjiView, MotionEvent e){
        KanjiGridView kanjiGrid = (KanjiGridView) window.findViewById(R.id.kanji_grid);
        List<KanjiCharacterView> kanjiViewList = kanjiGrid.getKanjiViewList();
        for (KanjiCharacterView k : kanjiViewList){
        }
    }

    @Override
    public void onKanjiViewTouch(KanjiCharacterView kanjiView, MotionEvent e) {

        long startTime = System.currentTimeMillis();

        ScrollView sv = (ScrollView) window.findViewById(R.id.info_text);
        sv.removeAllViews();
        TextView tv = new TextView(context);

        try {
            tv.setText(searchDict(mText, kanjiView.getCharPos()));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        tv.setTextColor(Color.BLACK);
        sv.addView(tv);

        String timeTaken = String.format("Search Time: %d", System.currentTimeMillis() - startTime);

        Log.d(TAG, timeTaken);
        Toast.makeText(context, timeTaken, Toast.LENGTH_SHORT).show();
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
            windowManager.updateViewLayout(window, params);
            return true;
        }

        return false;
    }

    @Override
    public void stop() {
        window.animate().translationY(-getRealDisplaySize().y).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                window.setVisibility(View.INVISIBLE);
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
        params.y = (int) (motionEvent1.getRawY() - motionEvent.getRawY());
        if (params.y > 0){
            params.y = 0;
        }
        windowManager.updateViewLayout(window, params);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        float distanceMoved = motionEvent.getRawY() - motionEvent1.getRawY();

        Log.d(TAG, String.format("Fling strength: %f", v1 / mMaxFlingVelocity));
        Log.d(TAG, String.format("Distance moved: %f", distanceMoved));

        if ((v1 / mMaxFlingVelocity) < FLICK_THRESHOLD){
            stop();
            return true;
        }

        return false;
    }

    @NonNull
    private String searchDict(String text, int textOffset) throws SQLException {

        List<Match> matches = mSearcher.search(text, textOffset);

        StringBuilder sb = new StringBuilder();
        for (Match match : matches){
            sb.append(match.toString());
            sb.append("\n\n");
        }
        return sb.toString();
    }
}
