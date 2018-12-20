package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import ca.fuwafuwa.kaku.XmlParsers.CommonParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Consts;

/**
 * Created by 0xbad1d3a5 on 11/30/2016.
 */

public class Kd2Character {

    private static final String XMLTAG = Kd2Consts.CHARACTER;

    private String literal =  null;
    private Kd2Codepoint codepoint = null;
    private Kd2Radical radical = null;
    private Kd2Misc misc = null;
    private Kd2DicNumber dic_number = null;
    private Kd2QueryCode query_code = null;
    private Kd2ReadingMeaning reading_meaning = null;

    public Kd2Character(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, XMLTAG);
        parser.nextToken();

        while (!XMLTAG.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch(name){
                case Kd2Consts.LITERAL:
                    literal = CommonParser.parseString(parser);
                    break;
                case Kd2Consts.CODEPOINT:
                    codepoint = new Kd2Codepoint(parser);
                    break;
                case Kd2Consts.RADICAL:
                    radical = new Kd2Radical(parser);
                    break;
                case Kd2Consts.MISC:
                    misc = new Kd2Misc(parser);
                    break;
                case Kd2Consts.DIC_NUMBER:
                    dic_number = new Kd2DicNumber(parser);
                    break;
                case Kd2Consts.QUERY_CODE:
                    query_code = new Kd2QueryCode(parser);
                    break;
                case Kd2Consts.READING_MEANING:
                    reading_meaning = new Kd2ReadingMeaning(parser);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, XMLTAG);
    }

    public String getLiteral() {
        return literal;
    }

    public Kd2Codepoint getCodepoint() {
        return codepoint;
    }

    public Kd2Radical getRadical() {
        return radical;
    }

    public Kd2Misc getMisc() {
        return misc;
    }

    public Kd2DicNumber getDic_number() {
        return dic_number;
    }

    public Kd2QueryCode getQuery_code() {
        return query_code;
    }

    public Kd2ReadingMeaning getReading_meaning() {
        return reading_meaning;
    }
}