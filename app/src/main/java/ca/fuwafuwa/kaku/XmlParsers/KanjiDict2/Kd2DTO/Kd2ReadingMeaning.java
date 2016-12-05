package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by Xyresic on 12/1/2016.
 */
public class Kd2ReadingMeaning {

    private static final String XMLTAG = Kd2Consts.READING_MEANING;

    private List<Kd2RmGroup> rmgroup = new ArrayList<>();
    private List<String> nanori = new ArrayList<>();

    public Kd2ReadingMeaning(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case Kd2Consts.RMGROUP:
                    rmgroup.add(new Kd2RmGroup(parser));
                    break;
                case Kd2Consts.NANORI:
                    nanori.add(CommonParser.parseString(parser));
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }
}