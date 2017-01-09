package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ca.fuwafuwa.kaku.KakuTools;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.KanjiViewListener;

/**
 * Created by Xyresic on 5/5/2016.
 */
public class KanjiCharacterView extends TextView implements GestureDetector.OnGestureListener {

    private static final String TAG = KanjiCharacterView.class.getName();

    private int mSizePx;
    private int mTextSizeDp;
    private int mCharPos;
    private boolean mResetOrigPos;
    private Point mOrigPos;
    private Context mContext;
    private KanjiViewListener mCallback;
    private GestureDetector mGestureDetector;
    private List<Pair<String, Double>> mChoices;

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
        mGestureDetector = new GestureDetector(mContext, this);
        mOrigPos = new Point();
        mResetOrigPos = true;
        mSizePx = KakuTools.dpToPx(mContext, 35);
        mTextSizeDp = 20;
    }

    public void setKanjiViewCallback(KanjiViewListener callback){
        this.mCallback = callback;
    }

    public void setChoices(List<Pair<String, Double>> choices){
        this.mChoices = choices;
    }

    public List<Pair<String, Double>> getChoices(){
        return mChoices;
    }

    public int getCharPos() {
        return mCharPos;
    }

    public void setCharPos(int charPos) {
        mCharPos = charPos;
    }

    public void setSize(int dp){
        mSizePx = KakuTools.dpToPx(mContext, dp);
    }

    public void setTextSize(int dp){
        mTextSizeDp = dp;
    }

    public void removeBackground(){
        setBackground(null);
    }

    public void setBackground(int id){
        Drawable bg = mContext.getDrawable(id);
        setBackground(bg);
    }

    public void setBackground(){
        Drawable bg = mContext.getDrawable(R.drawable.border_translucent);
        setBackground(bg);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSizeDp);
        setTextColor(Color.BLACK);
        setBackgroundColor(Color.WHITE);
        setGravity(Gravity.CENTER);
        setMeasuredDimension(mSizePx, mSizePx);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mResetOrigPos){
            mOrigPos.x = (int) getX();
            mOrigPos.y = (int) getY();
            mResetOrigPos = false;
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mResetOrigPos = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        mGestureDetector.onTouchEvent(e);

        if (e.getAction() == MotionEvent.ACTION_UP){
            setX(mOrigPos.x);
            setY(mOrigPos.y);
            setVisibility(View.VISIBLE);
            mCallback.onKanjiViewScrollEnd(this, e);
        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d(TAG, String.format("onDown: %f x %f", getX(), getY()));
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        mCallback.onKanjiViewTouch(this);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        setX(motionEvent1.getRawX() - motionEvent.getRawX() + mOrigPos.x);
        setY(motionEvent1.getRawY() - motionEvent.getRawY() + mOrigPos.y);
        setVisibility(View.INVISIBLE);
        mCallback.onKanjiViewScroll(this, motionEvent, motionEvent1);
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
