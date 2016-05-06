package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by Xyresic on 5/5/2016.
 */
public class KanjiGridView extends ViewGroup {

    private int mCellSize = 100;

    public KanjiGridView(Context context) {
        super(context);
        setCellSize(context);
    }

    public KanjiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCellSize(context);
    }

    public KanjiGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCellSize(context);
    }

    public KanjiGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setCellSize(context);
    }

    private void setCellSize(Context context){
        mCellSize = KakuTools.dpToPx(context, 40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int cellWidthSpec = MeasureSpec.makeMeasureSpec(mCellSize, MeasureSpec.AT_MOST);
        int cellHeightSpec = MeasureSpec.makeMeasureSpec(mCellSize, MeasureSpec.AT_MOST);

        int count = getChildCount();
        for (int index=0; index<count; index++) {
            final View child = getChildAt(index);
            child.measure(cellWidthSpec, cellHeightSpec);
        }

        // Use the size our parents gave us
        //setMeasuredDimension(resolveSize(mCellWidth*count, widthMeasureSpec), resolveSize(mCellHeight*count, heightMeasureSpec));
        int x = resolveSize(mCellSize * count, widthMeasureSpec);
        int y = mCellSize * 3;

        setMeasuredDimension(x, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int columns = (r - l) / mCellSize;
        if (columns < 0) {
            columns = 1;
        }

        int x = 0;
        int y = 0;
        int i = 0;
        int count = getChildCount();
        for (int index=0; index<count; index++) {
            final View child = getChildAt(index);
            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();
            int left = x + ((mCellSize - w) / 2);
            int top = y + ((mCellSize - h) / 2);
            child.layout(left, top, left + w, top + h);
            if (i >= (columns - 1)) {
                // advance to next row
                i = 0;
                x = 0;
                y += mCellSize;
            } else {
                i++;
                x += mCellSize;
            }
        }
    }
}
