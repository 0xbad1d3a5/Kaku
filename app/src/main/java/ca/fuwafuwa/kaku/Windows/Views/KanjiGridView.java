package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.KakuTools;
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
            KanjiCharacterView kanjiView = new KanjiCharacterView(mContext);
            kanjiView.setKanjiViewCallback(infoWin);
            kanjiView.setText(ocrChar.getBestChoice());
            kanjiView.setOcrChar(ocrChar);
            kanjiView.setCharPos(mKanjiCount);

            addView(kanjiView);
            mKanjiCount++;
        }

        setItemCount(mKanjiCount);
        postInvalidate();
    }

    public void correctText(InformationWindow infoWin){

        List<OcrChar> ocrChars = recomputeOcrChars();
        List<KanjiCharacterView> kanjiViews = getKanjiViewList();
        int ocrCharSize = ocrChars.size();
        int kanjiViewSize = kanjiViews.size();

        if (ocrCharSize > kanjiViewSize){
            addKanjiViews(infoWin, ocrCharSize - kanjiViewSize);
        }
        else if (ocrCharSize < kanjiViewSize){
            removeKanjiViews(kanjiViewSize - ocrCharSize);
        }

        mKanjiCount = 0;
        for (OcrChar ocrChar : ocrChars){
            KanjiCharacterView kanjiView = (KanjiCharacterView) getChildAt(mKanjiCount);
            kanjiView.setText(ocrChar.getBestChoice());
            kanjiView.setOcrChar(ocrChar);
            kanjiView.setCharPos(mKanjiCount);
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

    private List<OcrChar> recomputeOcrChars(){

        List<KanjiCharacterView> kanjiViews = getKanjiViewList();
        List<OcrChar> ocrChars = new ArrayList<>();

        for (KanjiCharacterView kcView : kanjiViews){

            if (!kcView.isEdited()){
                ocrChars.add(kcView.getOcrChar());
            }
            else {
                List<String> charList = KakuTools.splitTextByChar(kcView.getText().toString());

                int charListLength = charList.size();
                for (int i = 0; i < charListLength; i++){

                    OcrChar editedOcrChar = i == 0 ? kcView.getOcrChar() : new OcrChar(new ArrayList<Pair<String, Double>>(), null);

                    if (i == 0){
                        editedOcrChar.getAllChoices().add(0, Pair.create(charList.get(0), 100.0));
                    }
                    else {
                        editedOcrChar.getAllChoices().add(Pair.create(charList.get(i), 0.0));
                    }

                    ocrChars.add(editedOcrChar);
                }
            }

            kcView.setEdited(false);
        }

        return ocrChars;
    }

    private void addKanjiViews(InformationWindow infoWin, int count){
        for (int i = 0; i < count; i++){
            KanjiCharacterView kanjiView = new KanjiCharacterView(mContext);
            kanjiView.setKanjiViewCallback(infoWin);
            addView(kanjiView);
        }
    }

    private void removeKanjiViews(int count){

        int childCount = getChildCount();
        for (int i = childCount; i > childCount - count; i--){
            removeViewAt(i - 1);
        }
    }
}
