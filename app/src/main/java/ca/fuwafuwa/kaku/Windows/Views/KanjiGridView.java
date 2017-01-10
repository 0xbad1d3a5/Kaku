package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.Windows.InformationWindow;

/**
 * Created by Xyresic on 5/5/2016.
 */
public class KanjiGridView extends SquareGridView {

    private static final String TAG = KanjiGridView.class.getName();

    private int mKanjiCount = 0;

    public KanjiGridView(Context context) {
        super(context);
    }

    public KanjiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KanjiGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KanjiGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setText(InformationWindow infoWin, OcrResult ocrResult){

        mKanjiCount = 0;
        for (List<Pair<String, Double>> choices : ocrResult.getOcrChoices()){
            KanjiCharacterView kanji_view = new KanjiCharacterView(mContext);
            kanji_view.setKanjiViewCallback(infoWin);
            kanji_view.setText(choices.get(0).first);
            kanji_view.setChoices(choices);
            kanji_view.setCharPos(mKanjiCount);

            addView(kanji_view);
            mKanjiCount++;
        }

        setItemCount(mKanjiCount);
        postInvalidate();
    }

    public List<KanjiCharacterView> getKanjiViewList(){

        int count = getChildCount();
        List<KanjiCharacterView> kanjiViewList = new ArrayList<>();

        for (int i = 0; i < count; i++){
            kanjiViewList.add((KanjiCharacterView) getChildAt(i));
        }

        return kanjiViewList;
    }
}
