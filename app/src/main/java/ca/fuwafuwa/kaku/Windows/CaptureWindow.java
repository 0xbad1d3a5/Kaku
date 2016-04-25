package ca.fuwafuwa.kaku.Windows;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.fuwafuwa.kaku.BoxParams;
import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.TesseractThread;

/**
 * Created by Xyresic on 4/13/2016.
 */
public class CaptureWindow extends Window implements CaptureWindowCallback  {

    private static final String TAG = CaptureWindow.class.getName();

    private TesseractThread mTessThread;
    private WindowView windowView;
    private ResizeView resizeView;
    private View windowBox;
    private Animation fadeRepeat;
    private Drawable borderTranslucent;
    private Drawable border9PatchTransparent;

    public CaptureWindow(MainService context) {
        super(context);

        windowView = (WindowView) mWindow.findViewById(R.id.capture_window);
        resizeView = (ResizeView) mWindow.findViewById(R.id.resize_box);
        windowBox = mWindow.findViewById(R.id.capture_box);

        fadeRepeat = AnimationUtils.loadAnimation(mContext, R.anim.fade_repeat);
        borderTranslucent = mContext.getResources().getDrawable(R.drawable.border_translucent, null);
        border9PatchTransparent = mContext.getResources().getDrawable(R.drawable.border9patch_transparent, null);

        windowView.registerCallback(this);
        resizeView.registerCallback(this);

        mTessThread = new TesseractThread(mContext, this);
        Thread tessThread = new Thread(mTessThread);
        tessThread.setDaemon(true);
        tessThread.start();
    }

    @Override
    public boolean onMoveEvent(MotionEvent e) {
        setOpacity(e);
        boolean handled = super.onMoveEvent(e);
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
    protected void cleanup() {
        mTessThread.stop();
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
