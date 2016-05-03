package ca.fuwafuwa.kaku;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import ca.fuwafuwa.kaku.Windows.InformationWindow;

/**
 * Created by Xyresic on 4/15/2016.
 */
public class MainServiceHandler extends Handler {

    private static final String TAG = MainServiceHandler.class.getName();

    MainService mContext;

    public MainServiceHandler(MainService context){
        this.mContext = context;
    }

    @Override
    public void handleMessage(Message message){
        OcrResult result = (OcrResult) message.obj;
        Log.e(TAG, result.toString());
        Toast.makeText(mContext, result.toString(), Toast.LENGTH_LONG).show();

        (new InformationWindow(mContext)).setText(result.getText());
    }
}