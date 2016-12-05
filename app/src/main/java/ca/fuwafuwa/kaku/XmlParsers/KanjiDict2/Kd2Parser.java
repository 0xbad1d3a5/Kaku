package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.XmlParsers.Interfaces.DictParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO.Kd2Character;

/**
 * Created by Xyresic on 12/1/2016.
 */

public class Kd2Parser implements DictParser {

    private static final String TAG = Kd2Parser.class.getName();

    private DatabaseHelper mDbHelper;
    private int parseCount = 0;

    Kd2Parser(DatabaseHelper dbHelper){
        mDbHelper = dbHelper;
    }

    @Override
    public void parseDict(XmlPullParser parser) throws IOException, XmlPullParserException, SQLException {

        while (!Kd2Consts.KANJIDIC2.equals(parser.getName())){
            parser.nextToken();
        }

        parser.require(XmlPullParser.START_TAG, null, Kd2Consts.KANJIDIC2);
        parser.nextToken();
        parseHeader(parser);

        while (!Kd2Consts.KANJIDIC2.equals(parser.getName())){
            String name = parser.getName() == null ? "" : parser.getName();
            switch (name) {
                case Kd2Consts.CHARACTER:
                    parseKd2Character(parser);
                    break;
            }
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, Kd2Consts.KANJIDIC2);
    }

    // Skip the header, we don't care
    private void parseHeader(XmlPullParser parser) throws IOException, XmlPullParserException {

        while (!Kd2Consts.HEADER.equals(parser.getName())){
            parser.nextToken();
        }

        parser.require(XmlPullParser.START_TAG, null, Kd2Consts.HEADER);
        parser.nextToken();

        while (!Kd2Consts.HEADER.equals(parser.getName())){
            parser.nextToken();
        }

        parser.require(XmlPullParser.END_TAG, null, Kd2Consts.HEADER);
    }

    private void parseKd2Character(XmlPullParser parser) throws IOException, XmlPullParserException {
        Kd2Character kd2Character = new Kd2Character(parser);
    }

    private void parseKd2CharacterOptimized(Kd2Character character){

    }
}
