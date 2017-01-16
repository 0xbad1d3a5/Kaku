package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0x1bad1d3a on 5/5/2016.
 */
public class SquareGridView extends ViewGroup {

    public interface SquareViewListener {
        void onSquareScrollStart(KanjiCharacterView kanjiView, MotionEvent e);
        void onSquareScroll(KanjiCharacterView kanjiView, MotionEvent oe, MotionEvent e);
        void onSquareScrollEnd(KanjiCharacterView kanjiView, MotionEvent e);
        void onSquareTouch(KanjiCharacterView kanjiView);
    }

    private static final String TAG = SquareGridView.class.getName();

    protected Context mContext;

    private int mRows = 0;
    private int mColumns = 0;
    private int mItemCount = 0;
    private int mCellSize = 0;

    public SquareGridView(Context context) {
        super(context);
        Init(context);
    }

    public SquareGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public SquareGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public SquareGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context);
    }

    private void Init(Context context){
        mContext = context;
        mCellSize = KakuTools.dpToPx(mContext, 37);
    }

    public void setCellSize(int dp){
        mCellSize = KakuTools.dpToPx(mContext, dp);
    }

    public void setItemCount(int items){
        mItemCount = items;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int cellWidthSpec = MeasureSpec.makeMeasureSpec(mCellSize, MeasureSpec.AT_MOST);
        int cellHeightSpec = MeasureSpec.makeMeasureSpec(mCellSize, MeasureSpec.AT_MOST);

        int count = getChildCount();
        for (int index = 0; index < count; index++) {
            View child = getChildAt(index);
            child.measure(cellWidthSpec, cellHeightSpec);
        }

        // set width to mCellSize * count if width is smaller than screen, and just screen width if larger
        int x = resolveSize(mCellSize * count, widthMeasureSpec);
        mRows = (int) Math.ceil((double) mItemCount / (double) (x / mCellSize));
        mRows = mRows <= 0 ? 1 : mRows;
        mRows = mRows >= 4 ? 4 : mRows;
        int y = mCellSize * mRows;

        setMeasuredDimension(x, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mColumns = (r - l) / mCellSize;
        int xStart = ((r - l)  - (mCellSize * mColumns)) / 2;
        if (mColumns < 0) {
            mColumns = 1;
        }

        int x = xStart;
        int y = 0;
        int i = 0;
        int count = getChildCount();
        for (int index=0; index<count; index++) {
            View child = getChildAt(index);
            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();
            int left = x + ((mCellSize - w) / 2);
            int top = y + ((mCellSize - h) / 2);
            child.layout(left, top, left + w, top + h);
            if (i >= (mColumns - 1)) {
                // advance to next row
                i = 0;
                x = xStart;
                y += mCellSize;
            } else {
                i++;
                x += mCellSize;
            }
        }
    }
}
