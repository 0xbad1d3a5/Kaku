package ca.fuwafuwa.kaku;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by Xyresic on 4/15/2016.
 */
public class MainServiceHandler extends Handler {

    MainService mContext;

    public MainServiceHandler(MainService context){
        this.mContext = context;
    }

    @Override
    public void handleMessage(Message message){
        String text = (String) message.obj;
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }
}