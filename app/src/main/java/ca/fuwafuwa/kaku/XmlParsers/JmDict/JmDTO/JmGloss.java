package ca.fuwafuwa.kaku.XmlParsers.JmDict.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmConsts;

/**
 * Created by Xyresic on 4/30/2016.
 */
public class JmGloss {

    private static final String XMLTAG = JmConsts.GLOSS;

    private String text = null;
    private String lang = null;
    private String g_gend = null;

    private boolean isEnglish;

    public JmGloss(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        g_gend = attrMap.get(JmConsts.G_GEND);
        if (attrMap.get(JmConsts.XML_LANG) == null){
            lang = "eng";
            isEnglish = true;
        }
        else {
            lang = attrMap.get(JmConsts.XML_LANG);
            isEnglish = "eng".equals(lang);
        }

        text = CommonParser.parseString(parser);
    }

    public String toString(){
        if ("eng".equals(lang)){
            return text;
        }
        return "";
    }

    public String getText(){
        return text;
    }

    public String getLang(){
        return lang;
    }

    public String getGender(){
        return g_gend;
    }

    public boolean isEnglish() {
        return isEnglish;
    }
}
