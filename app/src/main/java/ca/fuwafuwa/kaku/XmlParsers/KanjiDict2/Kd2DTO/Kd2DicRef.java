package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0x1bad1d3a on 12/1/2016.
 */
public class Kd2DicRef {

    private static final String XMLTAG = Kd2Consts.DIC_REF;

    private String dr_type = null;
    private String m_vol = null;
    private String m_page = null;
    private String text = null;

    public Kd2DicRef(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        dr_type = attrMap.get(Kd2Consts.DR_TYPE);
        m_vol = attrMap.get(Kd2Consts.M_VOL);
        m_page = attrMap.get(Kd2Consts.M_PAGE);
        text = CommonParser.parseString(parser);
    }
}
