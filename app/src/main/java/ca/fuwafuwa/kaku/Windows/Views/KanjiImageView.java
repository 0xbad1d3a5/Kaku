package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0x1bad1d3a on 1/11/2017.
 */

public class KanjiImageView extends ImageView {

    private static final String TAG = KanjiImageView.class.getName();

    private Context mContext;
    private int mSizePx;

    public KanjiImageView(Context context) {
        super(context);
        Init(context);
    }

    public KanjiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public KanjiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public KanjiImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context);
    }

    private void Init(Context context){
        mContext = context;
    }

    public void setSize(int dp){
        mSizePx = KakuTools.dpToPx(mContext, dp);
        Log.d(TAG, String.format("setSize: X: %d Y: %d", getWidth(), getHeight()));
    }

    public void setBackground(int id){
        Drawable bg = mContext.getDrawable(id);
        setBackground(bg);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, String.format("mSizePx: %d", mSizePx));
        setMeasuredDimension(mSizePx, mSizePx);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Log.d(TAG, String.format("onLayout: X: %d Y: %d", getWidth(), getHeight()));
    }
}