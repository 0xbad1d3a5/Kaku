package ca.fuwafuwa.kaku.XmlParsers;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by 0x1bad1d3a on 4/25/2016.
 */
public class CommonParser {

    private interface IParse<T> {
        T parse(XmlPullParser parser) throws IOException, XmlPullParserException;
    }

    private static final String TAG = CommonParser.class.getName();

    private Context mContext;

    FileInputStream jmDictXml;

    public CommonParser(Context mContext) {
        this.mContext = mContext;
    }

    public void parseDict() throws Exception{

        Log.d(TAG, "INITIALIZING DICTIONARY");



        XmlPullParser parser = Xml.newPullParser();
        File file = new File(mContext.getExternalFilesDir(null), "JMDictOriginal.xml");
        jmDictXml = new FileInputStream(file);

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(jmDictXml, null);

        JmDictThread mJmDictThread = new JmDictThread(parser);
        Thread dictThread = new Thread(mJmDictThread);
        dictThread.setDaemon(true);
        dictThread.start();
    }

    /*
    public static String parseString(XmlPullParser parser) throws IOException, XmlPullParserException {

        if (parser.isEmptyElementTag()){
            parser.nextToken();
            return null;
        }

        Log.d(TAG, String.format("LINE NUMBER: %s", parser.getLineNumber()));

        String tagName = parser.getName();
        parser.require(XmlPullParser.START_TAG, null, parser.getName());
        Log.d(TAG, String.format("START TAG: %s", parser.getName()));
        parser.nextToken();
        Log.d(TAG, String.format("AFTER START TAG: %s", parser.getName()));

        if (parser.getEventType() == XmlPullParser.ENTITY_REF){
            Log.d(TAG, String.format("ENTITY TAG: %s", parser.getName()));
            parser.nextToken();
            Log.d(TAG, String.format("AFTER ENTITY TAG: %s", parser.getName()));
        }
        String text =  parser.getText();
        Log.d(TAG, String.format("TEXT: %s", text));
        parser.nextToken();
        Log.d(TAG, String.format("AFTER TEXT TAG: %s", parser.getName()));

        parser.require(XmlPullParser.END_TAG, null, parser.getName());
        Log.d(TAG, String.format("END TAG: %s", parser.getName()));
        parser.nextToken();
        Log.d(TAG, String.format("AFTER END TAG: %s", parser.getName()));

        return text;
    }
    */

    public static String parseString(XmlPullParser parser) throws IOException, XmlPullParserException {

        if (parser.isEmptyElementTag()){
            parser.nextToken();
            return null;
        }

        Log.d(TAG, String.format("LINE NUMBER: %s", parser.getLineNumber()));

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

    public static <T> T parseGeneric(XmlPullParser parser, IParse<T> func) throws IOException, XmlPullParserException {

        String tagName = parser.getName();
        parser.require(XmlPullParser.START_TAG, null, tagName);
        parser.nextToken();
        T returnType = func.parse(parser);
        parser.require(XmlPullParser.END_TAG, null, tagName);
        parser.nextToken();

        return returnType;
    }

    public static HashMap<String, String> parseAttributes(XmlPullParser parser){
        HashMap<String, String> attrMap = new HashMap<>();
        int numAttr = parser.getAttributeCount();
        for (int i = 0; i < numAttr; i++){
            attrMap.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        return attrMap;
    }

    /**
     * For testing purposes only, parses to the end of a element tag
     * @param parser The XmlPullParser
     * @param tag The Tag to parse
     */
    public static void parseToEndOfTag(XmlPullParser parser, final String tag) throws IOException, XmlPullParserException {
        parseGeneric(parser, new IParse<Void>() {
            @Override
            public Void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
                while (!tag.equals(parser.getName())){
                    parser.nextToken();
                }
                return null;
            }
        });
    }
}
