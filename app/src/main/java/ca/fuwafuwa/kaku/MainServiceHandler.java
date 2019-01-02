package ca.fuwafuwa.kaku;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.time.Instant;

import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.Windows.InformationWindow;
import ca.fuwafuwa.kaku.Windows.InstantWindow;

/**
 * Created by 0xbad1d3a5 on 4/15/2016.
 */
public class MainServiceHandler extends Handler {

    private static final String TAG = MainServiceHandler.class.getName();

    MainService mContext;
    public MainServiceHandler(MainService context){
        this.mContext = context;
    }

    @Override
    public void handleMessage(Message message){

        if (message.obj instanceof String){
            Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
        }
        else if (message.obj instanceof OcrResult)
        {
            OcrResult result = (OcrResult) message.obj;

            Log.e(TAG, result.toString());
            Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_SHORT).show();

            if (result.getInstant())
            {
                result.getCaptureWindow().setInstantWindow(new InstantWindow(mContext, result));
            }
            else {
                new InformationWindow(mContext, result);
            }
        }
        else {
            Toast.makeText(mContext, String.format("Unable to handle type: %s", message.obj.getClass().getName()), Toast.LENGTH_SHORT).show();
        }
    }
}