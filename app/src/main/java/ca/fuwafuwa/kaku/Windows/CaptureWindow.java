package ca.fuwafuwa.kaku.Windows;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.BoxParams;
import ca.fuwafuwa.kaku.Ocr.OcrRunnable;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;
import ca.fuwafuwa.kaku.XmlParsers.CommonParser;

/**
 * Created by 0x1bad1d3a on 4/13/2016.
 */
public class CaptureWindow extends Window implements WindowListener {

    private static final String TAG = CaptureWindow.class.getName();

    private OcrRunnable mOcr;
    private View mWindowBox;
    private Animation mFadeRepeat;
    private Drawable mBorderDefault;
    private Drawable mBorderReady;
    private boolean mAllowOcr;

    private CommonParser mCommonParser;

    public CaptureWindow(final MainService context) {
        super(context, R.layout.capture_window);

        this.mCommonParser = new CommonParser(context);

        mFadeRepeat = AnimationUtils.loadAnimation(this.context, R.anim.fade_repeat);
        mBorderDefault = this.context.getResources().getDrawable(R.drawable.bg_translucent_border_0_blue_blue, null);
        mBorderReady = this.context.getResources().getDrawable(R.drawable.bg_transparent_border_0_nil_ready, null);
        mAllowOcr = false;

        mOcr = new OcrRunnable(this.context, this);
        Thread tessThread = new Thread(mOcr);
        tessThread.setDaemon(true);
        tessThread.start();

        windowManager.getDefaultDisplay().getRotation();

        // Need to wait for the view to finish updating before we try to determine it's location
        mWindowBox = window.findViewById(R.id.capture_box);
        mWindowBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mAllowOcr){
                    performOcr();
                }
            }
        });

        mWindowBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                context.onCaptureWindowFinishedInitializing();
                mWindowBox.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public boolean onTouch(MotionEvent e) {
        setOpacityAndOcr(e);
        return super.onTouch(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

//        try {
//            mCommonParser.parseJmDict();
//            mCommonParser.parseKanjiDict2();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }

        return true;
    }

    @Override
    public boolean onResize(MotionEvent e) {
        setOpacityAndOcr(e);
        return super.onResize(e);
    }

    @Override
    public void stop() {
        mOcr.stop();
        super.stop();
    }

    public void showLoadingAnimation(){
        context.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mWindowBox.setBackground(mBorderDefault);
                mWindowBox.setAnimation(mFadeRepeat);
                mWindowBox.startAnimation(mFadeRepeat);
            }
        });
    }

    public void stopLoadingAnimation(){
        context.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mWindowBox.setBackground(mBorderReady);
                mWindowBox.clearAnimation();
            }
        });
    }

    private void setOpacityAndOcr(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindowBox.setBackground(mBorderDefault);
                break;
            case MotionEvent.ACTION_UP:
                mWindowBox.setBackground(mBorderReady);
                mAllowOcr = true;
                break;
        }
    }

    private void performOcr(){
        int[] viewPos = new int[2];
        mWindowBox.getLocationOnScreen(viewPos);
        mOcr.runTess(new BoxParams(viewPos[0], viewPos[1], params.width, params.height));
        mAllowOcr = false;
    }
}
