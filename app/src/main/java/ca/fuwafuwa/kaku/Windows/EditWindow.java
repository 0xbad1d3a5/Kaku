package ca.fuwafuwa.kaku.Windows;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import ca.fuwafuwa.kaku.MainService;
import ca.fuwafuwa.kaku.Ocr.OcrChar;
import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.R;
import ca.fuwafuwa.kaku.Windows.Views.ChoiceEditText;
import ca.fuwafuwa.kaku.Windows.Views.KanjiCharacterView;

/**
 * Created by 0xbad1d3a5 on 4/23/2016.
 */
public class EditWindow extends Window implements ChoiceEditText.InputDoneListener {

    public interface InputDoneListener {
        void onInputDone();
    }

    public static final String TAG = EditWindow.class.getName();

    private InputDoneListener mCallback;
    private KanjiCharacterView mKanjiView;

    public EditWindow(Context context)
    {
        super(context, R.layout.edit_window);
        show();

        ((ChoiceEditText) window.findViewById(R.id.edit_text)).setInputDoneCallback(this);
        ((ChoiceEditText) window.findViewById(R.id.edit_text)).requestFocus();
    }

    public void setInfo(OcrResult ocrResult, KanjiCharacterView kanjiView){

        mKanjiView = kanjiView;

        OcrChar ocrChar = kanjiView.getOcrChar();

        if (ocrChar == null){
            ImageView iv = (ImageView) window.findViewById(R.id.edit_kanji_image);
            iv.setBackgroundColor(0x44000000);
            return;
        }

        int[] pos = ocrChar.getPos();
        if (pos != null){
            Bitmap orig = ocrResult.getBitmap();
            orig = orig.copy(orig.getConfig(), true);

            int width = pos[2] - pos[0] - 1;
            int height = pos[3] - pos[1] - 1;
            int xPos = pos[0];
            int yPos = pos[1];

            for (int xTop = pos[0]; xTop < width + xPos; xTop++){
                orig.setPixel(xTop, yPos, Color.RED);
            }
            for (int xBottom = pos[0]; xBottom < width + xPos; xBottom++){
                orig.setPixel(xBottom, yPos + height, Color.RED);
            }
            for (int yLeft = pos[1]; yLeft < height + yPos; yLeft++){
                orig.setPixel(xPos, yLeft, Color.RED);
            }
            for (int yRight = pos[1]; yRight < height + yPos; yRight++){
                orig.setPixel(xPos + width, yRight, Color.RED);
            }
            orig.setPixel(xPos + width, yPos + height, Color.RED);

            xPos = pos[0] - width * 6;
            yPos = pos[1] - height * 6;
            width = width + width * 12;
            height = height + height * 12;

            if (xPos < 0) xPos = 0;
            if (yPos < 0) yPos = 0;
            if (width + xPos > orig.getWidth()) width = orig.getWidth() - xPos;
            if (height + yPos > orig.getHeight()) height = orig.getHeight() - yPos;

            Bitmap bitmapChar = Bitmap.createBitmap(orig, xPos, yPos, width, height);

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
                Build.VERSION.SDK_INT > 25 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
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
        if (input != null && !input.trim().isEmpty()){
            mKanjiView.setText(input);
            mCallback.onInputDone();
        }
        stop();
    }
}
