package ca.fuwafuwa.kaku.Windows;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Views.ChoiceEditText;
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;

/**
 * Created by 0x1bad1d3a on 4/23/2016.
 */
public class EditWindow extends Window implements ChoiceEditText.InputDoneListener {

    public interface InputDoneListener {
        void onInputDone();
    }

    public static final String TAG = EditWindow.class.getName();

    private InputDoneListener mCallback;
    private KanjiCharacterView mKanjiView;

    public EditWindow(final MainService context) {
        super(context, R.layout.edit_window);

        ((ChoiceEditText) window.findViewById(R.id.edit_text)).setInputDoneCallback(this);
    }

    public void setInfo(OcrResult ocrResult, KanjiCharacterView kanjiView){

        mKanjiView = kanjiView;

        int[] pos = kanjiView.getOcrChar().getPos();
        if (pos != null){
            Bitmap orig = ocrResult.getBitmap();
            Bitmap bitmapChar = Bitmap.createBitmap(orig, pos[0], pos[1], pos[2] - pos[0], pos[3] - pos[1]);

            ImageView iv = (ImageView) window.findViewById(R.id.edit_kanji_image);
            iv.setImageBitmap(bitmapChar);
        }
    }

    public void setInputDoneCallback(InputDoneListener callback){
        mCallback = callback;
    }

    /**
     * We need to override here because we need cannot have the FLAG_NOT_FOCUSABLE flag set in {@link Window#getDefaultParams()}
     */
    @Override
    protected WindowManager.LayoutParams getDefaultParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                0,
                PixelFormat.TRANSLUCENT);
        params.x = 0;
        params.y = 0;
        return params;
    }

    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onResize(MotionEvent e){
        return false;
    }

    @Override
    public void onEditTextInputDone(String input) {
        mKanjiView.setText(input);
        mCallback.onInputDone();
        stop();
    }
}
