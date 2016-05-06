package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.TextView;

import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.R;

/**
 * Created by 0x1bad1d3a on 5/5/2016.
 */
public class KanjiCharacterView extends TextView {

    Context mContext;

    public KanjiCharacterView(Context context) {
        super(context);
        mContext = context;
    }

    public KanjiCharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public KanjiCharacterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public KanjiCharacterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int size = KakuTools.dpToPx(mContext, 40);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setMeasuredDimension(size, size);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        Drawable bg = mContext.getDrawable(R.drawable.border_translucent);
        setBackground(bg);
        return true;
    }
}
