package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0xbad1d3a5 on 12/1/2016.
 */
public class Kd2Misc {

    private static final String XMLTAG = Kd2Consts.MISC;

    private String grade = null;
    private List<String> stroke_count = new ArrayList<>();
    private List<Kd2Variant> variant = new ArrayList<>();
    private String freq = null;
    private List<String> rad_name = new ArrayList<>();
    private String jlpt = null;

    public Kd2Misc(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case Kd2Consts.GRADE:
                    grade = CommonParser.parseString(parser);
                    break;
                case Kd2Consts.STROKE_COUNT:
                    stroke_count.add(CommonParser.parseString(parser));
                    break;
                case Kd2Consts.VARIANT:
                    variant.add(new Kd2Variant(parser));
                    break;
                case Kd2Consts.FREQ:
                    freq = CommonParser.parseString(parser);
                    break;
                case Kd2Consts.RAD_NAME:
                    rad_name.add(CommonParser.parseString(parser));
                    break;
                case Kd2Consts.JLPT:
                    jlpt = CommonParser.parseString(parser);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }
}
