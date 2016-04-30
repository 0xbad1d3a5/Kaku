package ca.fuwafuwa.kaku.XmlParsers.JmDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.JmConsts;

/**
 * Created by Xyresic on 4/30/2016.
 */
public class JmLsource {

    private static final String JMTAG = JmConsts.LSOURCE;

    private String lang = null;
    private String ls_type = null;
    private String ls_wasei = null;

    public JmLsource(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, JMTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        lang = attrMap.get(JmConsts.XML_LANG);
        ls_type = attrMap.get(JmConsts.LS_TYPE);
        ls_wasei = attrMap.get(JmConsts.LS_WASEI);
    }
}