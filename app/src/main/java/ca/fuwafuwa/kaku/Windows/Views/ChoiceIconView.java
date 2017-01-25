package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Enums.ChoiceType;

/**
 * Created by Xyresic on 1/9/2017.
 */

public class ChoiceIconView extends ImageView {

    private Context mContext;

    public ChoiceIconView(Context context) {
        super(context);
        Init(context);
    }

    public ChoiceIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public ChoiceIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public ChoiceIconView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context);
    }

    private void Init(Context context){
        setLayoutParams(new RelativeLayout.LayoutParams(KakuTools.dpToPx(context, 35), KakuTools.dpToPx(context, 35)));
    }

    public void onKanjiViewScrollStart(int statusBarHeight, KanjiCharacterView kanjiView, MotionEvent e){

        int[] pos = kanjiView.getOrigPosRaw();
        setX(pos[0]);
        setY(pos[1] - statusBarHeight);
        setVisibility(View.VISIBLE);
    }

    public void onKanjiViewScroll(KanjiCharacterView kanjiView, MotionEvent e1, MotionEvent e2){

        switch (getChoiceType(kanjiView, e2)){
            case EDIT:
                setImageResource(R.drawable.icon_edit);
                break;
            case DELETE:
                setImageResource(R.drawable.icon_delete);
                break;
            case NONE:
                setImageResource(R.drawable.icon_swap);
                break;
        }
    }

    public ChoiceType onKanjiViewScrollEnd(KanjiCharacterView kanjiView, MotionEvent e){

        setVisibility(INVISIBLE);
        return getChoiceType(kanjiView, e);
    }

    private ChoiceType getChoiceType(KanjiCharacterView kanjiView, MotionEvent e){

        int[] pos = kanjiView.getOrigPosRaw();

        if (e.getRawY() < pos[1]){
            int mid =  pos[0] + kanjiView.getWidth() / 2;
            if (e.getRawX() < mid){
                return ChoiceType.EDIT;
            }
            else {
                return ChoiceType.DELETE;
            }
        }

        return ChoiceType.NONE;
    }
}
