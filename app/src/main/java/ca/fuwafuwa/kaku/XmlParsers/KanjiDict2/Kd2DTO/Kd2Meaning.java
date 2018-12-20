package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0xbad1d3a5 on 12/2/2016.
 */
public class Kd2Meaning {

    private static final String XMLTAG = Kd2Consts.MEANING;

    private String m_lang = null;
    private String text = null;

    public Kd2Meaning(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        m_lang = attrMap.get(Kd2Consts.M_LANG);
        text = CommonParser.parseString(parser);
    }

    public String getM_lang() {
        return m_lang;
    }

    public String getText() {
        return text;
    }
}
