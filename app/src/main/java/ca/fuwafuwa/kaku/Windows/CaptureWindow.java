package ca.fuwafuwa.kaku.Windows;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.BoxParams;
import ca.fuwafuwa.kaku.Ocr.TesseractRunnable;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowTouchListener;
import ca.fuwafuwa.kaku.XmlParsers.CommonParser;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class CaptureWindow extends Window implements WindowTouchListener {

    private static final String TAG = CaptureWindow.class.getName();

    private TesseractRunnable mTessRunnable;
    private Thread mTessThread;
    private View mWindowBox;
    private Animation mFadeRepeat;
    private Drawable mBorderTranslucent;
    private Drawable mBorder9PatchTransparent;
    private boolean tessStarted = false;

    private CommonParser commonParser;

    public CaptureWindow(MainService context) {
        super(context, R.layout.capture_window);

        mWindowBox = window.findViewById(R.id.capture_box);

        mFadeRepeat = AnimationUtils.loadAnimation(this.context, R.anim.fade_repeat);
        mBorderTranslucent = this.context.getResources().getDrawable(R.drawable.border_translucent, null);
        mBorder9PatchTransparent = this.context.getResources().getDrawable(R.drawable.border9patch_transparent, null);

        this.commonParser = new CommonParser(context);

        mTessRunnable = new TesseractRunnable(this.context, this);
        mTessThread = new Thread(mTessRunnable);
        mTessThread.setDaemon(true);
        mTessThread.start();
    }

    @Override
    public boolean onTouch(MotionEvent e) {
        setOpacity(e);
        return super.onTouch(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        tessStarted = true;
        mTessRunnable.runTess(new BoxParams(params.x, params.y + getStatusBarHeight(), params.width, params.height));

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
        return super.onResize(e);
    }

    public void showLoadingAnimation(){
        context.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mWindowBox.setAnimation(mFadeRepeat);
                mWindowBox.startAnimation(mFadeRepeat);
                mWindowBox.setBackground(mBorderTranslucent);
            }
        });
    }

    public void stopLoadingAnimation(){
        context.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mWindowBox.setBackground(mBorder9PatchTransparent);
                mWindowBox.clearAnimation();
            }
        });
    }

    @Override
    public void stop() {
        mTessRunnable.stop();
        super.stop();
    }

    private void setOpacity(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindowBox.setBackground(mBorderTranslucent);
                break;
            case MotionEvent.ACTION_UP:
                if (!tessStarted) {
                    mTessRunnable.cancel();
                    mWindowBox.setBackground(mBorder9PatchTransparent);
                }
                tessStarted = false;
                break;
        }
    }
}
