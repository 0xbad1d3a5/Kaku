package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by Xyresic on 12/1/2016.
 */

public class Kd2RadValue {

    private static final String XMLTAG = Kd2Consts.RAD_VALUE;

    private String rad_type = null;
    private String text = null;

    public Kd2RadValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        rad_type = attrMap.get(Kd2Consts.RAD_TYPE);
        text = CommonParser.parseString(parser);
    }
}
