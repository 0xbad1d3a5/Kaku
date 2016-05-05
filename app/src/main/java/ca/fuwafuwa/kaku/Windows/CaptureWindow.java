package ca.fuwafuwa.kaku.Windows;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.fuwafuwa.kaku.BoxParams;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.TesseractThread;
import ca.fuwafuwa.kaku.XmlParsers.CommonParser;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class CaptureWindow extends Window implements WindowCallback {

    private static final String TAG = CaptureWindow.class.getName();

    private TesseractThread mTessThread;

    private View windowBox;
    private Animation fadeRepeat;
    private Drawable borderTranslucent;
    private Drawable border9PatchTransparent;

    private CommonParser jmDict;

    public CaptureWindow(MainService context) {
        super(context, R.layout.capture_window);

        windowBox = mWindow.findViewById(R.id.capture_box);

        fadeRepeat = AnimationUtils.loadAnimation(mContext, R.anim.fade_repeat);
        borderTranslucent = mContext.getResources().getDrawable(R.drawable.border_translucent, null);
        border9PatchTransparent = mContext.getResources().getDrawable(R.drawable.border9patch_transparent, null);

        this.jmDict = new CommonParser(mContext);

        mTessThread = new TesseractThread(mContext, this);
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
        mContext.getHandler().post(new Runnable() {
            @Override
            public void run() {
                windowBox.setAnimation(fadeRepeat);
                windowBox.startAnimation(fadeRepeat);
                windowBox.setBackground(borderTranslucent);
            }
        });
    }

    public void stopLoadingAnimation(){
        mContext.getHandler().post(new Runnable() {
            @Override
            public void run() {
                windowBox.setBackground(border9PatchTransparent);
                windowBox.clearAnimation();
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
                windowBox.setBackground(borderTranslucent);
                break;
            case MotionEvent.ACTION_UP:
                windowBox.setBackground(border9PatchTransparent);
                break;
        }
    }
}
