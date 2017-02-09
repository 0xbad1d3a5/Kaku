package ca.fuwafuwa.kaku.Windows;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.fuwafuwa.kaku.KakuTools;
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

    private OcrRunnable mTessRunnable;
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
        mBorderTranslucent = this.context.getResources().getDrawable(R.drawable.bg_translucent_border_blue_blue, null);
        mBorder9PatchTransparent = this.context.getResources().getDrawable(R.drawable.border_transparent_ready, null);

        this.commonParser = new CommonParser(context);

        mTessRunnable = new OcrRunnable(this.context, this);
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

        int[] viewPos = new int[2];
        mWindowBox.getLocationOnScreen(viewPos);

        // TODO: Replace the first 1 with R.drawable.bg_translucent_border_blue_blue.StrokeWidth when I figure out how
        int offset = KakuTools.dpToPx(this.context, 1)+1;
        mTessRunnable.runTess(new BoxParams(viewPos[0]+offset, viewPos[1]+offset, params.width-(2*offset), params.height-(2*offset)));

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
                mWindowBox.setBackground(mBorderTranslucent);
                mWindowBox.setAnimation(mFadeRepeat);
                mWindowBox.startAnimation(mFadeRepeat);
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
