package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.Ocr.OcrChar;
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
        for (OcrChar ocrChar : ocrResult.getOcrChars()){
            KanjiCharacterView kanji_view = new KanjiCharacterView(mContext);
            kanji_view.setKanjiViewCallback(infoWin);
            kanji_view.setText(ocrChar.getBestChoice());
            kanji_view.setOcrChar(ocrChar);
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
