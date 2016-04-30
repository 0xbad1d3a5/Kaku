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

import ca.fuwafuwa.kaku.XmlParsers.JmDTO.JmDict;

/**
 * Created by Xyresic on 4/25/2016.
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

        long startTime = System.currentTimeMillis();

        XmlPullParser parser = Xml.newPullParser();
        File file = new File(mContext.getExternalFilesDir(null), "JMDictOriginal.xml");
        jmDictXml = new FileInputStream(file);

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(jmDictXml, null);

        while (!JmConsts.JMDICT.equals(parser.getName())){
            parser.nextToken();
        }

        JmDict dict = new JmDict(parser);

        Log.d(TAG, String.format("FINISHED, TOOK %d", System.currentTimeMillis() - startTime));
    }

    public static String parseString(XmlPullParser parser) throws IOException, XmlPullParserException {

        if (parser.isEmptyElementTag()){
            parser.nextToken();
            return null;
        }

        String tagName = parser.getName();
        parser.require(XmlPullParser.START_TAG, null, tagName);
        parser.nextToken();

        if (parser.getEventType() == XmlPullParser.ENTITY_REF){
            parser.nextToken();
        }
        String text =  parser.getText();
        Log.d(TAG, text);
        parser.nextToken();

        parser.require(XmlPullParser.END_TAG, null, tagName);
        parser.nextToken();

        return text;
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
