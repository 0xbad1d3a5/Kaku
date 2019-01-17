package ca.fuwafuwa.kaku.XmlParsers;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper;
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Kd2DatabaseHelper;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Parser;

/**
 * Created by 0xbad1d3a5 on 4/25/2016.
 */
public class CommonParser {

    private static final String TAG = CommonParser.class.getName();

    private Context mContext;

    public CommonParser(Context mContext) {
        this.mContext = mContext;
    }

    public void parseJmDict() throws Exception {
        Log.d(TAG, "INITIALIZING DICTIONARY");
        ParserRunnable dictRunnable = new ParserRunnable(mContext);
        Thread dictThread = new Thread(dictRunnable);
        dictThread.setDaemon(true);
        dictThread.start();
    }

    public static String parseString(XmlPullParser parser) throws IOException, XmlPullParserException {

        if (parser.isEmptyElementTag()){
            parser.nextToken();
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String XMLTAG = parser.getName();
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())) {
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

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
        return sb.toString();
    }

    public static String parseOnlyEntityRef(XmlPullParser parser) throws IOException, XmlPullParserException {

        if (parser.isEmptyElementTag()){
            parser.nextToken();
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String XMLTAG = parser.getName();
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())) {
            switch (parser.getEventType()) {
                case XmlPullParser.ENTITY_REF:
                    sb.append(parser.getName());
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
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
