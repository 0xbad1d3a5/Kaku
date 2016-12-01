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
import ca.fuwafuwa.kaku.XmlParsers.Interfaces.DictParser;

/**
 * Created by Xyresic on 12/1/2016.
 */

public class ParserThread implements Runnable {

    private static final String TAG = ParserThread.class.getName();

    private Context mContext;
    private FileInputStream mDictXml;
    private DatabaseHelper mDbHelper;
    private DictParser mDictParser;

    public ParserThread(Context context, Class dbHelperClass, Class dictParserClass, String fileName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException {
        mContext = context;
        DbHelperFactory mDbHelperFactory = new DbHelperFactory(context);
        mDbHelper = mDbHelperFactory.instance(dbHelperClass);
        mDictParser = (DictParser) dictParserClass.getConstructor(DatabaseHelper.class).newInstance(mDbHelper);
        mDictXml = new FileInputStream(new File(mContext.getExternalFilesDir(null), fileName));
    }

    @Override
    public void run() {

        long startTime = System.currentTimeMillis();

        try {

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

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, String.format("FINISHED DICT, TOOK %d", System.currentTimeMillis() - startTime));
    }
}
