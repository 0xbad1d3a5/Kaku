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
public class Kd2QCode {

    private static final String XMLTAG = Kd2Consts.Q_CODE;

    private String qc_type = null;
    private String skip_misclass = null;
    private String text = null;

    public Kd2QCode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);

        HashMap<String, String> attrMap = CommonParser.parseAttributes(parser);
        qc_type = attrMap.get(Kd2Consts.QC_TYPE);
        skip_misclass = attrMap.get(Kd2Consts.SKIP_MISCLASS);
        text = CommonParser.parseString(parser);
    }
}
