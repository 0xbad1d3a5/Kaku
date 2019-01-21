package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Data.DisplayData;
import ca.fuwafuwa.kaku.Windows.Data.DisplayDataOcr;
import ca.fuwafuwa.kaku.Windows.Data.ISquareChar;
import ca.fuwafuwa.kaku.Windows.Data.SquareCharOcr;
import kotlin.Pair;

/**
 * Created by 0xbad1d3a5 on 5/5/2016.
 */
public class ChoiceGridView extends SquareGridView {

    private static final String TAG = ChoiceGridView.class.getName();

    private Context mContext;
    private List<TextView> mKanjiChoices;

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

    public void onKanjiViewScrollStart(SquareCharOcr squareChar, MotionEvent e)
    {
        mKanjiChoices = new ArrayList<>();
        setItemCount(squareChar.getAllChoices().size() + 1);

        int[] pos = squareChar.getBitmapPos();
        if (pos != null){
            int dp10 = KakuTools.dpToPx(mContext, 10);
            Bitmap orig = squareChar.getDisplayData().getBitmap();
            int width = pos[2] - pos[0];
            int height = pos[3] - pos[1];
            width = width <= 0 ? 1 : width;
            height = height <= 0 ? 1 : height;
            Bitmap bitmapChar = Bitmap.createBitmap(orig, pos[0], pos[1], width, height);
            KanjiImageView charImage = new KanjiImageView(mContext);

            charImage.setSize(90);
            charImage.setPadding(dp10, dp10, dp10, dp10);
            charImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            charImage.setCropToPadding(true);
            charImage.setImageBitmap(bitmapChar);
            charImage.setBackground(R.drawable.bg_translucent_border_0_black_black);
            addView(charImage);
        }

        for (Pair<String, Double> choice : squareChar.getAllChoices()){
            TextView kanjiText = new TextView(mContext);
            kanjiText.setWidth(KakuTools.dpToPx(mContext, 90));
            kanjiText.setTextSize(60);
            kanjiText.setText(choice.getFirst());
            kanjiText.setBackgroundResource(R.drawable.bg_solid_border_0_white_black);
            addView(kanjiText);

            mKanjiChoices.add(kanjiText);
        }

        setY(e.getRawY());
    }

    public void onKanjiViewScroll(MotionEvent e1, MotionEvent e2)
    {
        if (mKanjiChoices == null){
            return;
        }

        for (TextView k : mKanjiChoices){
            if (checkForSelection(k, e2)){
                k.setBackgroundResource(R.drawable.bg_solid_border_0_blue_black);
            }
            else {
                k.setBackgroundResource(R.drawable.bg_solid_border_0_white_black);
            }
        }
    }

    public void onKanjiViewScrollEnd(SquareCharOcr squareChar, MotionEvent e){

        if (mKanjiChoices == null){
            return;
        }

        for (TextView k : mKanjiChoices){
            if (checkForSelection(k, e)){
                squareChar.setChar(k.getText().toString());
            }
        }

        removeAllViews();
        mKanjiChoices = null;
    }

    private boolean checkForSelection(TextView kanjiView, MotionEvent e){

        int[] pos = new int[2];
        kanjiView.getLocationInWindow(pos);

        if (pos[0] < e.getRawX() && e.getRawX() < pos[0] + kanjiView.getWidth() &&
            pos[1] < e.getRawY() && e.getRawY() < pos[1] + kanjiView.getHeight()){
            return true;
        }
        else {
            return false;
        }
    }
}
