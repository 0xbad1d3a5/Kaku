package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by Xyresic on 12/2/2016.
 */
public class Kd2RmGroup {

    private static final String XMLTAG = Kd2Consts.RMGROUP;

    private List<Kd2Reading> reading = new ArrayList<>();
    private List<Kd2Meaning> meaning = new ArrayList<>();

    public Kd2RmGroup(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case Kd2Consts.READING:
                    reading.add(new Kd2Reading(parser));
                    break;
                case Kd2Consts.MEANING:
                    meaning.add(new Kd2Meaning(parser));
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }
}
