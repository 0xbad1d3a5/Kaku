package ca.fuwafuwa.kaku.XmlParsers;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmDict;

/**
 * Created by 0x1bad1d3a on 4/30/2016.
 */
public class JmDictThread implements Runnable {

    private static final String TAG = JmDictThread.class.getName();

    private XmlPullParser parser;
    private JmDict dict;

    public JmDictThread(XmlPullParser parser){
        this.parser = parser;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            while (!JmConsts.JMDICT.equals(parser.getName())){
                    parser.nextToken();
            }
            dict = new JmDict(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, String.format("FINISHED, TOOK %d", System.currentTimeMillis() - startTime));
    }
}
