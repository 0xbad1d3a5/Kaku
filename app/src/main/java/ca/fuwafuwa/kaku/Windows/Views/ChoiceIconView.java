package ca.fuwafuwa.kaku.Windows.Views;

import android.annotation.SuppressLint;
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
 * Created by 0xbad1d3a5 on 1/9/2017.
 */

@SuppressLint("AppCompatCustomView")
public class ChoiceIconView extends ImageView {

    private Context mContext;
    private int mStatusBarHeight;

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

    public void onKanjiViewScrollStart(int statusBarHeight, KanjiCharacterView kanjiView, MotionEvent e)
    {
        mStatusBarHeight = statusBarHeight;
        setVisibility(View.VISIBLE);
    }

    public void onKanjiViewScroll(MotionEvent e1, MotionEvent e2)
    {
        switch (getChoiceType(e2)){
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

    public ChoiceType onKanjiViewScrollEnd(MotionEvent e){

        setVisibility(INVISIBLE);
        return getChoiceType(e);
    }

    private ChoiceType getChoiceType(MotionEvent e){

        int[] pos = new int[2];
        getLocationInWindow(pos);

        if (e.getRawY() < pos[1] + mStatusBarHeight){
            int mid =  pos[0] + getWidth() / 2;
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
