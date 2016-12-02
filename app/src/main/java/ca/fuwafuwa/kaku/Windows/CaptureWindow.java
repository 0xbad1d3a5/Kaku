package ca.fuwafuwa.kaku.Windows;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.BoxParams;
import ca.fuwafuwa.kaku.Ocr.TesseractThread;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Interfaces.WindowListener;
import ca.fuwafuwa.kaku.XmlParsers.CommonParser;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class CaptureWindow extends Window implements WindowListener {

    private static final String TAG = CaptureWindow.class.getName();

    private TesseractThread mTessThread;
    private View mWindowBox;
    private Animation mFadeRepeat;
    private Drawable mBorderTranslucent;
    private Drawable mBorder9PatchTransparent;
    private CommonParser mJmDict;

    public CaptureWindow(MainService context) {
        super(context, R.layout.capture_window);

        mWindowBox = window.findViewById(R.id.capture_box);

        mFadeRepeat = AnimationUtils.loadAnimation(this.context, R.anim.fade_repeat);
        mBorderTranslucent = this.context.getResources().getDrawable(R.drawable.border_translucent, null);
        mBorder9PatchTransparent = this.context.getResources().getDrawable(R.drawable.border9patch_transparent, null);

        this.mJmDict = new CommonParser(context);

        mTessThread = new TesseractThread(this.context, this);
        Thread tessThread = new Thread(mTessThread);
        tessThread.setDaemon(true);
        tessThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        setOpacity(e);
        boolean handled = super.onTouchEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:

                mTessThread.runTess(new BoxParams(params.x, params.y + getStatusBarHeight(), params.width, params.height));

                /*
                try {
                    mJmDict.parseDict();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }*/

                break;
        }
        return handled;
    }

    @Override
    public boolean onResizeEvent(MotionEvent e) {
        setOpacity(e);
        boolean handled = super.onResizeEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                mTessThread.runTess(new BoxParams(params.x, params.y + getStatusBarHeight(), params.width, params.height));
                break;
        }
        return handled;
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
        mTessThread.stop();
        super.stop();
    }

    private void setOpacity(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWindowBox.setBackground(mBorderTranslucent);
                break;
            case MotionEvent.ACTION_UP:
                mWindowBox.setBackground(mBorder9PatchTransparent);
                break;
        }
    }
}
