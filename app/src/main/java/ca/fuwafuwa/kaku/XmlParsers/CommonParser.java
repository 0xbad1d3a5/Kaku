package ca.fuwafuwa.kaku.XmlParsers;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Xyresic on 4/25/2016.
 */
public class CommonParser {

    private interface IParse<T> {
        T parse(XmlPullParser parser) throws IOException, XmlPullParserException;
    }

    private static final String TAG = CommonParser.class.getName();

    private Context mContext;

    public CommonParser(Context mContext) {
        this.mContext = mContext;
    }

    public void parseDict() throws Exception{

        Log.d(TAG, "INITIALIZING DICTIONARY");

        JmDictThread mJmDictThread = new JmDictThread(mContext);
        Thread dictThread = new Thread(mJmDictThread);
        dictThread.setDaemon(true);
        dictThread.start();
    }

    public static String parseString(XmlPullParser parser) throws IOException, XmlPullParserException {

        if (parser.isEmptyElementTag()){
            parser.nextToken();
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String JMTAG = parser.getName();
        parser.require(XmlPullParser.START_TAG, null, JMTAG);
        parser.nextToken();

        while (!JMTAG.equals(parser.getName())) {
            switch (parser.getEventType()) {
                case XmlPullParser.TEXT:
                    sb.append(parser.getText());
                    break;
                case XmlPullParser.ENTITY_REF:
                    sb.append(parser.getText().trim());
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, JMTAG);
        return sb.toString();
    }

    public static HashMap<String, String> parseAttributes(XmlPullParser parser){
        HashMap<String, String> attrMap = new HashMap<>();
        int numAttr = parser.getAttributeCount();
        for (int i = 0; i < numAttr; i++){
            attrMap.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        return attrMap;
    }
}
