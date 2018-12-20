package ca.fuwafuwa.kaku.Windows.Views;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by 0xbad1d3a5 on 1/10/2017.
 */

public class ChoiceEditText extends EditText {

    public interface InputDoneListener {
        void onEditTextInputDone(String input);
    }

    private static final String TAG = ChoiceEditText.class.getName();

    private Context mContext;
    private InputMethodManager mImeManager;
    private InputDoneListener mCallback;

    public ChoiceEditText(Context context) {
        super(context);
        Init(context);
    }

    public ChoiceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public ChoiceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public ChoiceEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context);
    }

    private void Init(Context context){

        mContext = context;
        mImeManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        post(new Runnable() {
            @Override
            public void run() {
                String currentKeyboard =  Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
                if (currentKeyboard == null || !currentKeyboard.contains("handwriting")){
                    mImeManager.showInputMethodPicker();
                }
                mImeManager.showSoftInput(ChoiceEditText.this, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    public void setInputDoneCallback(InputDoneListener callback){
        mCallback = callback;
    }

    @Override
    public void onEditorAction(int actionCode) {
        if (actionCode == EditorInfo.IME_ACTION_DONE){
            if (mCallback != null){
                mImeManager.hideSoftInputFromWindow(getWindowToken(), 0);
                mCallback.onEditTextInputDone(getText().toString());
            }
        }
        super.onEditorAction(actionCode);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if (mCallback != null){
                mImeManager.hideSoftInputFromWindow(getWindowToken(), 0);
                mCallback.onEditTextInputDone(getText().toString());
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
