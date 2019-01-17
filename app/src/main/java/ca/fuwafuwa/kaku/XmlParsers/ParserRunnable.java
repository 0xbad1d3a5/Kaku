package ca.fuwafuwa.kaku.XmlParsers;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.j256.ormlite.misc.TransactionManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.Database.DbHelperFactory;
import ca.fuwafuwa.kaku.Database.IDatabaseHelper;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper;
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Kd2DatabaseHelper;
import ca.fuwafuwa.kaku.XmlParsers.Interfaces.DictParser;
import ca.fuwafuwa.kaku.XmlParsers.JmDict.JmParser;
import ca.fuwafuwa.kaku.XmlParsers.KanjiDict2.Kd2Parser;

/**
 * Created by 0xbad1d3a5 on 12/1/2016.
 */

public class ParserRunnable implements Runnable {

    private static final String TAG = ParserRunnable.class.getName();

    Context mContext;
    DbHelperFactory mDbHelperFactory;

    public ParserRunnable(Context context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException
    {
        mContext = context;
        mDbHelperFactory = new DbHelperFactory(context);
    }

    @Override
    public void run()
    {
        try
        {
            parseDictionary(JmDatabaseHelper.class, JmParser.class, "JMdict_e.xml");
            parseDictionary(Kd2DatabaseHelper.class, Kd2Parser.class, "kanjidic2.xml");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void parseDictionary(Class dbHelperClass, Class dictParserClass, String fileName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException, XmlPullParserException, SQLException
    {
        long startTime = System.currentTimeMillis();

        DatabaseHelper mDbHelper = mDbHelperFactory.instance(dbHelperClass);
        final DictParser mDictParser = (DictParser) dictParserClass.getConstructor(IDatabaseHelper.class).newInstance(mDbHelper);
        FileInputStream mDictXml = new FileInputStream(new File(mContext.getExternalFilesDir(null), fileName));

        final XmlPullParser mParser = Xml.newPullParser();
        mDbHelper.deleteDatabase();
        mParser.setInput(mDictXml, null);

        TransactionManager.callInTransaction(mDbHelper.getConnectionSource(), new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDictParser.parseDict(mParser);
                return null;
            }
        });

        Log.d(TAG, String.format("FINISHED DICT, TOOK %d", System.currentTimeMillis() - startTime));
    }
}
