package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0xbad1d3a5 on 12/1/2016.
 */
public class Kd2CpValue {

    private static final String XMLTAG = Kd2Consts.CP_VALUE;

    private String cp_type = null;
    private String text = null;

    public Kd2CpValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        cp_type = attrMap.get(Kd2Consts.CP_TYPE);
        text = CommonParser.parseString(parser);
    }
}
