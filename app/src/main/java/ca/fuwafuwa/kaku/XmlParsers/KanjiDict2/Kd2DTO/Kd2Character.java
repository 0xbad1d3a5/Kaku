package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by Xyresic on 11/30/2016.
 */

public class Kd2Character {

    private static final String XMLTAG = Kd2Consts.CHARACTER;

    private String literal =  null;
    private List<Kd2Codepoint> codepoint = new ArrayList<>();
    private List<Kd2Radical> radical = new ArrayList<>();
    private List<Kd2Misc> misc = new ArrayList<>();
    private List<Kd2DicNumber> dic_number = new ArrayList<>();
    private List<Kd2QueryCode> query_code = new ArrayList<>();
    private List<Kd2ReadingMeaning> reading_meaning = new ArrayList<>();

    public Kd2Character(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case Kd2Consts.LITERAL:
                    break;
                case Kd2Consts.CODEPOINT:
                    break;
                case Kd2Consts.RADICAL:
                    break;
                case Kd2Consts.MISC:
                    break;
                case Kd2Consts.DIC_NUMBER:
                    break;
                case Kd2Consts.QUERY_CODE:
                    break;
                case Kd2Consts.READING_MEANING:
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }

    public String getLiteral() {
        return literal;
    }

    public List<Kd2Codepoint> getCodepoint() {
        return codepoint;
    }

    public List<Kd2Radical> getRadical() {
        return radical;
    }

    public List<Kd2Misc> getMisc() {
        return misc;
    }

    public List<Kd2DicNumber> getDic_number() {
        return dic_number;
    }

    public List<Kd2QueryCode> getQuery_code() {
        return query_code;
    }

    public List<Kd2ReadingMeaning> getReading_meaning() {
        return reading_meaning;
    }
}