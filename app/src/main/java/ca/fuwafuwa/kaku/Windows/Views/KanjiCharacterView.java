package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.IKanjiViewCallback;

/**
 * Created by 0x1bad1d3a on 5/5/2016.
 */
public class KanjiCharacterView extends TextView implements GestureDetector.OnGestureListener {

    private static final String TAG = KanjiCharacterView.class.getName();

    private int mCharPos;
    private Point mOffset;
    private Context mContext;
    private IKanjiViewCallback mCallback;
    private GestureDetector mGestureDetector;

    public KanjiCharacterView(Context context) {
        super(context);
        Init(context);
    }

    public KanjiCharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public KanjiCharacterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public KanjiCharacterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context);
    }

    private void Init(Context context){
        mContext = context;
        mOffset = new Point(0, 0);
        mGestureDetector = new GestureDetector(mContext, this);
    }

    public void setKanjiViewCallback(IKanjiViewCallback callback){
        this.mCallback = callback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int size = KakuTools.dpToPx(mContext, 35);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setBackground(mContext.getDrawable(R.color.black));
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
        setMeasuredDimension(size, size);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        mGestureDetector.onTouchEvent(e);

        if (e.getAction() == MotionEvent.ACTION_UP){
            mOffset.x = (int) getX();
            mOffset.y = (int) getY();
        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d(TAG, String.format("onDown: %f x %f", getX(), getY()));
        mOffset.x = (int) getX();
        mOffset.y = (int) getY();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        Drawable bg = mContext.getDrawable(R.drawable.border_translucent);
        setBackground(bg);
        mCallback.onKanjiViewTouch(this, e);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        setX(motionEvent1.getRawX() - motionEvent.getRawX() + mOffset.x);
        setY(motionEvent1.getRawY() - motionEvent.getRawY() + mOffset.y);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onFling");
        return false;
    }
}
