package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0x1bad1d3a on 12/2/2016.
 */
public class Kd2Reading {

    private static final String XMLTAG = Kd2Consts.READING;

    private String r_type = null;
    private String on_type = null;
    private String r_status = null;
    private String text = null;

    public Kd2Reading(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        r_type = attrMap.get(Kd2Consts.R_TYPE);
        on_type = attrMap.get(Kd2Consts.ON_TYPE);
        r_status = attrMap.get(Kd2Consts.R_STATUS);
        text = CommonParser.parseString(parser);
    }
}
