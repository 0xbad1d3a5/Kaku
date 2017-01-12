package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.R;

/**
 * Created by Xyresic on 5/5/2016.
 */
public class ChoiceGridView extends SquareGridView {

    private static final String TAG = ChoiceGridView.class.getName();

    private Context mContext;
    private List<KanjiCharacterView> mKanjiChoices;

    public ChoiceGridView(Context context) {
        super(context);
        Init(context);
    }

    public ChoiceGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public ChoiceGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public ChoiceGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context);
    }

    private void Init(Context context){
        mContext = context;
        setCellSize(100);
    }

    public void onKanjiViewScrollStart(OcrResult ocrResult, KanjiCharacterView kanjiView, MotionEvent e){

        mKanjiChoices = new ArrayList<>();
        setItemCount(kanjiView.getOcrChar().getAllChoices().size() + 1);

        int[] pos = kanjiView.getOcrChar().getPos();
        int dp10 = KakuTools.dpToPx(mContext, 10);
        Bitmap orig = ocrResult.getBitmap();
        Bitmap bitmapChar = Bitmap.createBitmap(orig, pos[0], pos[1], pos[2] - pos[0], pos[3] - pos[1]);
        KanjiImageView charImage = new KanjiImageView(mContext);

        charImage.setSize(90);
        charImage.setPadding(dp10, dp10, dp10, dp10);
        charImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        charImage.setCropToPadding(true);
        charImage.setImageBitmap(bitmapChar);
        charImage.setBackground(R.drawable.border_black_translucent_bg);
        addView(charImage);

        for (Pair<String, Double> choice : kanjiView.getOcrChar().getAllChoices()){
            KanjiCharacterView kanji_view = new KanjiCharacterView(mContext);
            kanji_view.setSize(90);
            kanji_view.setTextSize(60);
            kanji_view.setText(choice.first);
            kanji_view.setBackground(R.drawable.border_black_white_bg);
            addView(kanji_view);

            mKanjiChoices.add(kanji_view);
        }

        setY(e.getRawY());
    }

    public void onKanjiViewScroll(KanjiCharacterView kanjiView, MotionEvent e1, MotionEvent e2){

        if (mKanjiChoices == null){
            return;
        }

        for (KanjiCharacterView k : mKanjiChoices){
            if (checkForSelection(k, e2)){
                k.setBackground(R.drawable.border_black_blue_bg);
            }
            else {
                k.setBackground(R.drawable.border_black_white_bg);
            }
        }
    }

    public void onKanjiViewScrollEnd(KanjiCharacterView kanjiView, MotionEvent e){

        if (mKanjiChoices == null){
            return;
        }

        for (KanjiCharacterView k : mKanjiChoices){
            if (checkForSelection(k, e)){
                kanjiView.setText(k.getText());
            }
        }

        removeAllViews();
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
