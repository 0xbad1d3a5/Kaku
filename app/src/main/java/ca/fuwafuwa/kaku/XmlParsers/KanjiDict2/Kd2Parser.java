package ca.fuwafuwa.kaku.XmlParsers.KanjiDict2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;

import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.XmlParsers.Interfaces.DictParser;

/**
 * Created by 0x1bad1d3a on 12/1/2016.
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

    }
}
