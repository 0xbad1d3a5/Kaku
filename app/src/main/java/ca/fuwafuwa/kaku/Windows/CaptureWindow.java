package ca.fuwafuwa.kaku.Windows;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.BoxParams;
import ca.fuwafuwa.kaku.Ocr.OcrRunnable;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;
import ca.fuwafuwa.kaku.XmlParsers.CommonParser;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class CaptureWindow extends Window implements WindowListener {

    private static final String TAG = CaptureWindow.class.getName();

    private OcrRunnable mOcr;
    private Thread mTessThread;
    private View mWindowBox;
    private Animation mFadeRepeat;
    private Drawable mBorderDefault;
    private Drawable mBorderReady;

    private CommonParser commonParser;

    public CaptureWindow(MainService context) {
        super(context, R.layout.capture_window);

        mWindowBox = window.findViewById(R.id.capture_box);

        mFadeRepeat = AnimationUtils.loadAnimation(this.context, R.anim.fade_repeat);
        mBorderDefault = this.context.getResources().getDrawable(R.drawable.bg_translucent_border_0_blue_blue, null);
        mBorderReady = this.context.getResources().getDrawable(R.drawable.bg_transparent_border_0_nil_ready, null);

        this.commonParser = new CommonParser(context);

        mOcr = new OcrRunnable(this.context, this);
        mTessThread = new Thread(mOcr);
        mTessThread.setDaemon(true);
        mTessThread.start();
    }

    @Override
    public boolean onTouch(MotionEvent e) {
        setOpacity(e);
        boolean handled = super.onTouch(e);
        performOcr(e);
        return handled;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        /*
        tessStarted = true;
        int[] viewPos = new int[2];
        mWindowBox.getLocationOnScreen(viewPos);
        mOcr.runTess(new BoxParams(viewPos[0], viewPos[1], params.width, params.height));
        */

//        try {
//            commonParser.parseJmDict();
//            commonParser.parseKanjiDict2();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }

        return true;
    }

    @Override
    public boolean onResize(MotionEvent e) {
        setOpacity(e);
        boolean handled = super.onResize(e);
        performOcr(e);
        return handled;
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

    @Override
    public void stop() {
        mOcr.stop();
        super.stop();
    }

    private void setOpacity(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindowBox.setBackground(mBorderDefault);
                break;
            case MotionEvent.ACTION_UP:
                mWindowBox.setBackground(mBorderReady);
                break;
        }
    }

    private void performOcr(MotionEvent e){
        if (e.getAction() == MotionEvent.ACTION_UP){
            int[] viewPos = new int[2];
            mWindowBox.getLocationOnScreen(viewPos);
            mOcr.runTess(new BoxParams(params.x, params.y + getStatusBarHeight(), params.width, params.height));
        }
    }
}
