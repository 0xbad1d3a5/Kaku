package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.R;

/**
 * Created by 0x1bad1d3a on 5/5/2016.
 */
public class ChoiceGridView extends SquareGridView {

    private static final String TAG = ChoiceGridView.class.getName();

    private KanjiCharacterView mKanjiView;
    private List<KanjiCharacterView> mKanjiChoices;

    public ChoiceGridView(Context context) {
        super(context);
        Init();
    }

    public ChoiceGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public ChoiceGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    public ChoiceGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init();
    }

    private void Init(){
        setCellSize(100);
    }

    public void onKanjiViewScroll(KanjiCharacterView kanjiView, MotionEvent e1, MotionEvent e2){

        // No need to update layout as we are in the same onScroll, just check collision
        if (mKanjiView != null && mKanjiView == kanjiView){

            for (KanjiCharacterView k : mKanjiChoices){
                if (checkForSelection(k, e2)){
                    k.setBackground(R.drawable.border_black_blue_bg);
                }
                else {
                    k.setBackground(R.drawable.border_black_white_bg);
                }
            }

            return;
        }

        mKanjiView = kanjiView;
        mKanjiChoices = new ArrayList<>();
        setItemCount(mKanjiView.getChoices().size());

        for (Pair<String, Double> choice : kanjiView.getChoices()){
            KanjiCharacterView kanji_view = new KanjiCharacterView(mContext);
            kanji_view.setSize(90);
            kanji_view.setTextSize(60);
            kanji_view.setText(choice.first);
            kanji_view.setBackground(R.drawable.border_black_white_bg);
            addView(kanji_view);

            mKanjiChoices.add(kanji_view);
        }

        setY(e1.getRawY());

    }

    public void onKanjiViewScrollEnd(KanjiCharacterView kanjiView, MotionEvent e){

        if (mKanjiView == null){
            return;
        }

        for (KanjiCharacterView k : mKanjiChoices){
            if (checkForSelection(k, e)){
                mKanjiView.setText(k.getText());
            }
        }

        if (mKanjiView == kanjiView){
            mKanjiView = null;
            removeAllViews();
        }
    }

    private boolean checkForSelection(KanjiCharacterView kanjiView, MotionEvent e){

        int[] pos = kanjiView.getOrigPosRaw();

        if (pos[0] < e.getRawX() && e.getRawX() < pos[0] + kanjiView.getWidth() &&
                pos[1] < e.getRawY() && e.getRawY() < pos[1] + kanjiView.getHeight()){
            return true;
        }
        else {
            return false;
        }
    }
}
