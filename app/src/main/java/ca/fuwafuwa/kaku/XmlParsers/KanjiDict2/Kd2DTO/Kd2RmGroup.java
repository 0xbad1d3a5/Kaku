package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0xbad1d3a5 on 12/2/2016.
 */
public class Kd2RmGroup {

    private static final String XMLTAG = Kd2Consts.RMGROUP;

    private List<Kd2Reading> readings = new ArrayList<>();
    private List<Kd2Meaning> meanings = new ArrayList<>();

    public Kd2RmGroup(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case Kd2Consts.READING:
                    readings.add(new Kd2Reading(parser));
                    break;
                case Kd2Consts.MEANING:
                    meanings.add(new Kd2Meaning(parser));
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }

    public List<Kd2Reading> getReadings() {
        return readings;
    }

    public List<Kd2Meaning> getMeanings() {
        return meanings;
    }
}
