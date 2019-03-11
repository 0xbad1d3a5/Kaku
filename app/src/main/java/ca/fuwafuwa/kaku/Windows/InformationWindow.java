package ca.fuwafuwa.kaku.Windows;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.Constants;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized;
import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.LangUtils;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Search.JmSearchResult;
import ca.fuwafuwa.kaku.Search.SearchInfo;
import ca.fuwafuwa.kaku.Search.Searcher;
import ca.fuwafuwa.kaku.Windows.Data.DisplayData;
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar;
import ca.fuwafuwa.kaku.Windows.Data.SquareChar;
import ca.fuwafuwa.kaku.Windows.Interfaces.ICopyText;
import ca.fuwafuwa.kaku.Windows.Interfaces.IRecalculateKanjiViews;
import ca.fuwafuwa.kaku.Windows.Interfaces.ISearchPerformer;
import ca.fuwafuwa.kaku.Windows.Views.KanjiGridView;

/**
 * Created by 0xbad1d3a5 on 4/23/2016.
 */
public class InformationWindow extends Window implements Searcher.SearchDictDone, IRecalculateKanjiViews, ISearchPerformer, ICopyText
{

    private static final String TAG = InformationWindow.class.getName();
    private static final float FLICK_THRESHOLD = -0.05f;

    private GestureDetector mGestureDetector;
    private float mMaxFlingVelocity;
    private LinearLayout mInfoWindow;
    private KanjiGridView mKanjiGrid;
    private TextSwitcher mDictResults;
    private Searcher mSearcher;
    private boolean mTextOnlyLookup;
    private ArrayList<ISquareChar> mSearchedChars = new ArrayList<>();

    public InformationWindow(Context context, WindowCoordinator windowCoordinator)
    {
        super(context, windowCoordinator, R.layout.window_info);

        mMaxFlingVelocity = ViewConfiguration.get(this.context).getScaledMaximumFlingVelocity();
        mGestureDetector = new GestureDetector(this.context, this);
        mInfoWindow = window.findViewById(R.id.info_window);
        mKanjiGrid = window.findViewById(R.id.kanji_grid);
        mDictResults = window.findViewById(R.id.dict_results);

        mKanjiGrid.setDependencies(windowCoordinator, this);

        try {
            mSearcher = new Searcher(context);
            mSearcher.registerCallback(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        show();
    }

    public void setResult(DisplayData displayData)
    {
        mSearchedChars = new ArrayList<>();

        mKanjiGrid.removeAllViews();
        mKanjiGrid.setText(displayData);
    }

    public void setResult(String textResult)
    {
        List<String> charList = KakuTools.splitTextByChar(textResult);
        List<ISquareChar> squareCharList = new ArrayList<>();
        DisplayData displayData = new DisplayData(squareCharList);
        for (String c : charList) squareCharList.add(new SquareChar(displayData, c));
        displayData.assignIndicies();

        mKanjiGrid.setText(displayData);
        performSearch(displayData.getSquareChars().get(0));

        mTextOnlyLookup = true;
    }

    public void copyText()
    {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null, mKanjiGrid.getText());
        clipboard.setPrimaryClip(clip);

        hide();
    }

    @Override
    public void performSearch(@NotNull ISquareChar squareChar)
    {
        Log.d(TAG, squareChar.getChar());

        mKanjiGrid.unhighlightAll(squareChar);
        mSearcher.search(new SearchInfo(squareChar));
    }

    /**
     * InformationWindow does not need to reInit layout as its getDefaultParams() are all relative. Re-initing will cause bugs.
     */
    @Override
    public void reInit(ReinitOptions options)
    {
        options.reinitViewLayout = false;
        super.reInit(options);
    }

    @Override
    protected WindowManager.LayoutParams getDefaultParams()
    {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT > 25 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        return params;
    }

    @Override
    public boolean onTouch(MotionEvent e)
    {
        mGestureDetector.onTouchEvent(e);

        if (e.getAction() == MotionEvent.ACTION_UP){
            params.y = 0;
            windowManager.updateViewLayout(window, params);
            return true;
        }

        return false;
    }

    @Override
    public void show()
    {
        mDictResults.setText("");

        window.setVisibility(View.VISIBLE);
        params.y = 0; // onScroll changes this value
        super.show();
        window.setY(0); // translationY changes this value
    }

    @Override
    public void hide()
    {
        window.animate().translationY(-getRealDisplaySize().y).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                window.setVisibility(View.INVISIBLE);

                if (!mTextOnlyLookup)
                {
                    InformationWindow.super.hide();
                }
                else
                {
                    windowCoordinator.stopAllWindows(); // TODO: NRE here
                }
            }
        });
    }

    @Override
    public void stop()
    {
        mSearcher.unregisterCallback();
        mGestureDetector = null;
        mKanjiGrid  = null;
        mSearcher = null;
        super.stop();
    }

    @Override
    public boolean onResize(MotionEvent e)
    {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent)
    {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e)
    {
        if (mInfoWindow.getX() < e.getX() && e.getX() < (mInfoWindow.getX() + mInfoWindow.getWidth()) &&
            mInfoWindow.getY() < e.getY() && e.getY() < (mInfoWindow.getY() + mInfoWindow.getHeight()))
        {
            // Do nothing
        }
        else
        {
            hide();
        }

        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        if (e.getY() < mKanjiGrid.getY() + mKanjiGrid.getHeight())
        {
            int triggerAreaSize = getViewWidth() / 8;
            if (e.getX() > getViewWidth() - triggerAreaSize)
            {
                mKanjiGrid.scrollNext();
            }
            else if (e.getX() < triggerAreaSize)
            {
                mKanjiGrid.scrollPrev();
            }
        }
        else if (mInfoWindow.getX() < e.getX() && e.getX() < (mInfoWindow.getX() + mInfoWindow.getWidth()) &&
                 mInfoWindow.getY() < e.getY() && e.getY() < (mInfoWindow.getY() + mInfoWindow.getHeight()))
        {
            // Do nothing
        }
        else
        {
            hide();
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1)
    {
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
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1)
    {
        if (motionEvent == null || motionEvent1 == null){
            return false;
        }

        float distanceMoved = motionEvent.getRawY() - motionEvent1.getRawY();

        Log.d(TAG, String.format("Fling strength: %f", v1 / mMaxFlingVelocity));
        Log.d(TAG, String.format("Distance moved: %f", distanceMoved));

        if ((v1 / mMaxFlingVelocity) < FLICK_THRESHOLD)
        {
            hide();
            return true;
        }

        return false;
    }

    @Override
    public void jmResultsCallback(List<JmSearchResult> results, SearchInfo search)
    {
        windowCoordinator.getWindow(Constants.WINDOW_INSTANT_KANJI).hide();

        if (results.size() > 0)
        {
            displayResults(results);

            if (search.getSquareChar().getUserTouched() && !mSearchedChars.contains(search.getSquareChar()))
            {
                //windowCoordinator.<HistoryWindow>getWindowOfType(Constants.WINDOW_HISTORY).addResult(search.getSquareChar(), results);
                mSearchedChars.add(search.getSquareChar());
            }
        }

        // Highlights words in the window as long as they match
        int start = search.getIndex() - mKanjiGrid.getOffset();
        if (results.size() > 0){
            String kanji = results.get(0).getWord();
            for (int i = start; i < start + kanji.codePointCount(0, kanji.length()); i++){
                if (i >= mKanjiGrid.getKanjiViewList().size()){
                    break;
                }
                mKanjiGrid.getKanjiViewList().get(i).highlight();
            }
        }
        else {
            mKanjiGrid.getKanjiViewList().get(start).highlight();
        }
    }

    @Override
    public void recalculateKanjiViews()
    {
        mKanjiGrid.recalculateKanjiViews();
    }

    private void displayResults(List<JmSearchResult> jmResults)
    {
        StringBuilder sb = new StringBuilder();

        for (JmSearchResult jmSearchResult : jmResults)
        {
            sb.append(jmSearchResult.getEntry().getKanji());

            if (!jmSearchResult.getEntry().getReadings().isEmpty()){
                if (Constants.DB_JMDICT_NAME.equals(jmSearchResult.getEntry().getDictionary()))
                {
                    sb.append(" (");
                }
                else {
                    sb.append(" ");
                }
                sb.append(jmSearchResult.getEntry().getReadings());
                if (Constants.DB_JMDICT_NAME.equals(jmSearchResult.getEntry().getDictionary())) sb.append(")");
            }

            String deinfReason = jmSearchResult.getDeinfInfo().getReason();
            if (deinfReason != null && !deinfReason.isEmpty()){
                sb.append(String.format(" %s", deinfReason));
            }

            sb.append("\n");
            sb.append(getMeaning(jmSearchResult.getEntry()));
            sb.append("\n\n");
        }

        if (sb.length() > 2)
        {
            sb.setLength(sb.length() - 2);
        }

        mDictResults.setText(sb.toString());
    }

    private String getMeaning(EntryOptimized entry)
    {
        String[] meanings = entry.getMeanings().split("\ufffc", -1);
        String[] pos = entry.getPos().split("\ufffc", -1);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < meanings.length; i++){
            if (i != 0){
                sb.append(" ");
            }
            sb.append(LangUtils.Companion.ConvertIntToCircledNum(i + 1));
            sb.append(" ");
            if (Constants.DB_JMDICT_NAME.equals(entry.getDictionary()) && !pos[i].isEmpty()){
                sb.append(String.format("(%s) ", pos[i]));
            }
            sb.append(meanings[i]);
        }

        return sb.toString();
    }
}