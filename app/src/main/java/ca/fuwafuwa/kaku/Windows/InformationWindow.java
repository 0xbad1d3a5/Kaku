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
import android.widget.LinearLayout;
import android.widget.TextSwitcher;

import java.sql.SQLException;
import java.util.List;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized;
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Models.CharacterOptimized;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Search.SearchInfo;
import ca.fuwafuwa.kaku.Search.Searcher;
import ca.fuwafuwa.kaku.Windows.Enums.ChoiceType;
import ca.fuwafuwa.kaku.Windows.Views.ChoiceGridView;
import ca.fuwafuwa.kaku.Windows.Views.ChoiceIconView;
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView;
import ca.fuwafuwa.kaku.Windows.Views.SquareGridView;

/**
 * Created by 0x1bad1d3a on 4/23/2016.
 */
public class InformationWindow extends Window implements SquareGridView.SquareViewListener, Searcher.SearchDictDone, EditWindow.InputDoneListener{

    private static final String TAG = InformationWindow.class.getName();
    private static final float FLICK_THRESHOLD = -0.05f;

    private GestureDetector mGestureDetector;
    private float mMaxFlingVelocity;
    private KanjiGridView mKanjiGrid;
    private LinearLayout mLinearLayout;
    private Searcher mSearcher;
    private OcrResult mOcrResult;
    private String mText;

    public InformationWindow(MainService context) {

        super(context, R.layout.info_window);

        mMaxFlingVelocity = ViewConfiguration.get(this.context).getScaledMaximumFlingVelocity();
        mGestureDetector = new GestureDetector(this.context, this);
        mKanjiGrid = (KanjiGridView) window.findViewById(R.id.kanji_grid);
        mLinearLayout = (LinearLayout) window.findViewById(R.id.info_text);

        try {
            mSearcher = new Searcher(context);
            mSearcher.registerCallback(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setOcrResults(OcrResult ocrResult){
        this.mOcrResult = ocrResult;
        this.mText = ocrResult.getText();
        mKanjiGrid.setText(this, ocrResult);
    }

    @Override
    public void onSquareScrollStart(KanjiCharacterView kanjiView, MotionEvent e) {

        Log.d(TAG, "onSquareScrollStart");

        ChoiceIconView civ = (ChoiceIconView) window.findViewById(R.id.kanji_choice_edit);
        civ.onKanjiViewScrollStart(getStatusBarHeight(), kanjiView, e);

        ChoiceGridView cgv = (ChoiceGridView) window.findViewById(R.id.kanji_choice_grid);
        cgv.onKanjiViewScrollStart(mOcrResult, kanjiView, e);
    }

    @Override
    public void onSquareScroll(KanjiCharacterView kanjiView, MotionEvent e1, MotionEvent e2){

        Log.d(TAG, "onSquareScroll");

        ChoiceIconView civ = (ChoiceIconView) window.findViewById(R.id.kanji_choice_edit);
        civ.onKanjiViewScroll(kanjiView, e1, e2);

        ChoiceGridView cgv = (ChoiceGridView) window.findViewById(R.id.kanji_choice_grid);
        cgv.onKanjiViewScroll(kanjiView, e1, e2);
    }

    @Override
    public void onSquareScrollEnd(KanjiCharacterView kanjiView, MotionEvent e) {

        Log.d(TAG, "onSquareScrollEnd");

        ChoiceIconView civ = (ChoiceIconView) window.findViewById(R.id.kanji_choice_edit);
        ChoiceType editChoice = civ.onKanjiViewScrollEnd(kanjiView, e);

        ChoiceGridView cgv = (ChoiceGridView) window.findViewById(R.id.kanji_choice_grid);
        cgv.onKanjiViewScrollEnd(kanjiView, e);

        switch (editChoice){
            case DELETE:
                kanjiView.setText("");
                kanjiView.setEdited(true);
                onInputDone();
                break;
            case EDIT:
                kanjiView.setEdited(true);
                EditWindow editWindow = new EditWindow(context);
                editWindow.setInfo(mOcrResult, kanjiView);
                editWindow.setInputDoneCallback(this);
                break;
            case NONE:
                updateInternalText();
                break;
        }
    }

    @Override
    public void onSquareTouch(KanjiCharacterView kanjiView) {

        Log.d(TAG, "onSquareScrollTouch");

        Log.d(TAG, kanjiView.getText().toString());

        List<KanjiCharacterView> kanjiViewList = mKanjiGrid.getKanjiViewList();
        for (KanjiCharacterView k : kanjiViewList){
            k.removeBackground();
        }

        mSearcher.search(new SearchInfo(mText, kanjiView.getCharPos(), kanjiView));
    }

    @Override
    protected WindowManager.LayoutParams getDefaultParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        return params;
    }

    @Override
    public boolean onTouch(MotionEvent e){

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
    public boolean onResize(MotionEvent e){
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        stop();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        if (motionEvent == null || motionEvent1 == null){
            return false;
        }

        params.y = (int) (motionEvent1.getRawY() - motionEvent.getRawY());
        if (params.y > 0){
            params.y = 0;
        }
        windowManager.updateViewLayout(window, params);

        return true;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        if (motionEvent == null || motionEvent1 == null){
            return false;
        }

        float distanceMoved = motionEvent.getRawY() - motionEvent1.getRawY();

        Log.d(TAG, String.format("Fling strength: %f", v1 / mMaxFlingVelocity));
        Log.d(TAG, String.format("Distance moved: %f", distanceMoved));

        if ((v1 / mMaxFlingVelocity) < FLICK_THRESHOLD){
            stop();
            return true;
        }

        return false;
    }

    @Override
    public void jmResultsCallback(List<EntryOptimized> results, SearchInfo search) {

        StringBuilder sb = new StringBuilder();

        for (EntryOptimized eo : results){
            sb.append(eo.getKanji());
            if (!eo.getReadings().isEmpty()){
                sb.append(" (");
                sb.append(eo.getReadings());
                sb.append(")");
            }
            sb.append("\n");
            sb.append(eo.getMeanings());
            sb.append("\n\n");
        }

        if (sb.length() > 2){
            sb.setLength(sb.length() - 1);
        }

        TextSwitcher tv = (TextSwitcher) mLinearLayout.findViewById(R.id.jm_results);
        tv.setText(sb.toString());

        int start = mKanjiGrid.getKanjiViewList().indexOf(search.getKanjiCharacterView());
        if (results.size() > 0){
            String kanji = results.get(0).getKanji();
            for (int i = start; i < start + kanji.codePointCount(0, kanji.length()); i++){
                mKanjiGrid.getKanjiViewList().get(i).setBackground();
            }
        }
        else {
            mKanjiGrid.getKanjiViewList().get(start).setBackground();
        }
    }

    @Override
    public void kd2ResultsCallback(List<CharacterOptimized> results, SearchInfo search) {

        StringBuilder sb = new StringBuilder();

        for (CharacterOptimized co : results){
            sb.append(co.getKanji());
            if (!co.getOnyomi().isEmpty()){
                sb.append(" (");
                sb.append(co.getOnyomi());
                sb.append(") ");
            }
            if (!co.getKunyomi().isEmpty()){
                sb.append(" [");
                sb.append(co.getKunyomi());
                sb.append("] ");
            }
            sb.append("\n");
            sb.append(co.getMeaning());
            sb.append("\n\n");
        }

        if (sb.length() > 2){
            sb.setLength(sb.length() - 2);
        }

        TextSwitcher tv = (TextSwitcher) mLinearLayout.findViewById(R.id.kd2_results);
        tv.setText(sb.toString());
    }

    @Override
    public void onInputDone() {
        mKanjiGrid.correctText(this);
        updateInternalText();
    }

    private void updateInternalText(){
        StringBuilder sb = new StringBuilder();
        for (KanjiCharacterView k : mKanjiGrid.getKanjiViewList()){
            sb.append(k.getText());
        }
        mText = sb.toString();
    }
}