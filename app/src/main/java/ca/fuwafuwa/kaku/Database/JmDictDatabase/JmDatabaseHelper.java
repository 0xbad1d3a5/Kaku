package ca.fuwafuwa.kaku.Database.JmDictDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ca.fuwafuwa.kaku.Constants;
import ca.fuwafuwa.kaku.Database.DatabaseHelper;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Entry;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.EntryOptimized;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Kanji;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.KanjiIrregularity;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.KanjiPriority;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Meaning;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningAdditionalInfo;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningAntonym;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningCrossReference;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningDialect;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningField;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningGloss;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningKanjiRestriction;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningLoanSource;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningMisc;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningPartOfSpeech;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.MeaningReadingRestriction;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.Reading;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.ReadingIrregularity;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.ReadingPriority;
import ca.fuwafuwa.kaku.Database.JmDictDatabase.Models.ReadingRestriction;
import ca.fuwafuwa.kaku.Exceptions.NotImplementedException;
import ca.fuwafuwa.kaku.KakuTools;

/**
 * Created by 0xbad1d3a5 on 7/26/2016.
 */
public class JmDatabaseHelper extends DatabaseHelper {

    private static final String TAG = JmDatabaseHelper.class.getName();

    private static final String DATABASE_NAME = Constants.JMDICT_DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    private static JmDatabaseHelper instance;

    private Context mContext;

    private JmDatabaseHelper(Context context){
        super(context, String.format("%s/%s", context.getFilesDir().getAbsolutePath(), DATABASE_NAME), null, DATABASE_VERSION);
        Log.d(TAG, "JmDatabaseHelper Constructor");
        mContext = context;
    }

    public static synchronized JmDatabaseHelper instance(Context context){
        if (instance == null){
            instance = new JmDatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.d(TAG, "JmDatabaseHelper onCreate");
        try {
            TableUtils.createTable(connectionSource, EntryOptimized.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // Can't use onUpgrade, because getDbDao() will sometimes run first due to being on another thread, opening a DB connection and causing issues when we try to delete the DB
        throw new NotImplementedException();
    }

    public void deleteDatabase(){
        mContext.deleteDatabase(String.format("%s/%s", mContext.getFilesDir().getAbsolutePath(), DATABASE_NAME));
    }

    public <T> Dao<T, Integer> getDbDao(Class clazz) throws SQLException {
        return getDao(clazz);
    }
}
