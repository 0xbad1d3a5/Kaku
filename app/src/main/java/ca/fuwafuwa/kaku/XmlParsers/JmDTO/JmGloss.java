package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

/**
 * Created by 0x1bad1d3a on 4/30/2016.
 */
public class JmGloss {

    private static final String JMTAG = JmConsts.GLOSS;

    private String text = null;
    private String lang = null;
    private String g_gend = null;

    public JmGloss(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        lang = attrMap.get(JmConsts.XML_LANG);
        g_gend = attrMap.get(JmConsts.G_GEND);
        text = CommonParser.parseString(parser);
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
}
