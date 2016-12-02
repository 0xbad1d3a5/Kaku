package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0x1bad1d3a on 12/1/2016.
 */
public class Kd2Codepoint {

    private static final String XMLTAG = Kd2Consts.CODEPOINT;

    private List<Kd2CpValue> cp_value = new ArrayList<>();

    public Kd2Codepoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }
}
