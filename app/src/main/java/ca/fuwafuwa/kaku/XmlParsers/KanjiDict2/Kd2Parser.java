package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2;

import android.util.Log;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Models.CharacterOptimized;
import ca.fuwafuwa.kaku.XmlParsers.Interfaces.DictParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO.Kd2Character;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO.Kd2Meaning;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO.Kd2Reading;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2DTO.Kd2RmGroup;

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

    private void parseKd2Character(XmlPullParser parser) throws IOException, XmlPullParserException, SQLException {

        Kd2Character kd2Character = new Kd2Character(parser);

        parseKd2CharacterOptimized(kd2Character);

        if (++parseCount % 100 == 0){
            Log.d(TAG, String.format("Parsed %d entries", parseCount));
        }
    }

    private void parseKd2CharacterOptimized(Kd2Character character) throws SQLException {

        if (character.getReading_meaning() == null){
            return;
        }

        List<Kd2RmGroup> kd2RmGroups = character.getReading_meaning().getRmGroups();

        for (Kd2RmGroup rmGroup : kd2RmGroups){
            CharacterOptimized co = new CharacterOptimized();

            co.setKanji(character.getLiteral());
            co.setOnyomi(parseKd2CharacterOptimizedOnyomi(rmGroup));
            co.setKunyomi(parseKd2CharacterOptimizedKunyomi(rmGroup));
            co.setMeaning(parseKd2CharacterOptimizedMeaning(rmGroup));

            mDbHelper.getDbDao(CharacterOptimized.class).create(co);
        }
    }

    private String parseKd2CharacterOptimizedOnyomi(Kd2RmGroup rmGroup){

        Collection<Kd2Reading> onReadings = Collections2.filter(rmGroup.getReadings(), new Predicate<Kd2Reading>() {
            @Override
            public boolean apply(Kd2Reading input) {
                return Kd2Consts.R_TYPE_JA_ON.equals(input.getR_type());
            }
        });

        Collection<String> onStringReadings = Collections2.transform(onReadings, new Function<Kd2Reading, String>() {
            @Override
            public String apply(Kd2Reading input) {
                return input.getText();
            }
        });

        return Joiner.on(", ").join(onStringReadings);
    }

    private String parseKd2CharacterOptimizedKunyomi(Kd2RmGroup rmGroup){

        Collection<Kd2Reading> kunReadings = Collections2.filter(rmGroup.getReadings(), new Predicate<Kd2Reading>() {
            @Override
            public boolean apply(Kd2Reading input) {
                return Kd2Consts.R_TYPE_JA_KUN.equals(input.getR_type());
            }
        });

        Collection<String> kunStringReadings = Collections2.transform(kunReadings, new Function<Kd2Reading, String>() {
            @Override
            public String apply(Kd2Reading input) {
                return input.getText();
            }
        });

        return Joiner.on(", ").join(kunStringReadings);
    }

    private String parseKd2CharacterOptimizedMeaning(Kd2RmGroup rmGroup){

        Collection<Kd2Meaning> meanings = Collections2.filter(rmGroup.getMeanings(), new Predicate<Kd2Meaning>() {
            @Override
            public boolean apply(Kd2Meaning input) {
                return input.getM_lang() == null || "en".equals(input.getM_lang());
            }
        });

        Collection<String> stringMeanings = Collections2.transform(meanings, new Function<Kd2Meaning, String>() {
            @Override
            public String apply(Kd2Meaning input) {
                return input.getText();
            }
        });

        return Joiner.on(", ").join(stringMeanings);
    }
}
