package ca.fuwafuwa.kaku;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import ca.fuwafuwa.kaku.Ocr.OcrResult;
import ca.fuwafuwa.kaku.Windows.InformationWindow;
import ca.fuwafuwa.kaku.Windows.InstantWindow;
import ca.fuwafuwa.kaku.Windows.WindowCoordinator;

/**
 * Created by 0xbad1d3a5 on 4/15/2016.
 */
public class MainServiceHandler extends Handler {

    private static final String TAG = MainServiceHandler.class.getName();

    private MainService mKakuService;
    private WindowCoordinator mWindowCoordinator;

    public MainServiceHandler(MainService mainService, WindowCoordinator windowCoordinator)
    {
        mKakuService = mainService;
        mWindowCoordinator = windowCoordinator;
    }

    @Override
    public void handleMessage(Message message)
    {
        if (message.obj instanceof String){
            Toast.makeText(mKakuService, message.obj.toString(), Toast.LENGTH_SHORT).show();
        }
        else if (message.obj instanceof OcrResult)
        {
            OcrResult result = (OcrResult) message.obj;

            Log.e(TAG, result.toString());
            Toast.makeText(mKakuService, result.getMessage(), Toast.LENGTH_SHORT).show();

            if (result.getDisplayData().getInstantMode())
            {
                InstantWindow instantWindow = (InstantWindow) mWindowCoordinator.getWindow(Constants.WINDOW_INSTANT);
                instantWindow.setResult(result.getDisplayData());
                instantWindow.show();
            }
            else {
                InformationWindow infoWindow = (InformationWindow) mWindowCoordinator.getWindow(Constants.WINDOW_INFO);
                infoWindow.setResult(result.getDisplayData());
                infoWindow.show();
            }
        }
        else {
            Toast.makeText(mKakuService, String.format("Unable to handle type: %s", message.obj.getClass().getName()), Toast.LENGTH_SHORT).show();
        }
    }
}