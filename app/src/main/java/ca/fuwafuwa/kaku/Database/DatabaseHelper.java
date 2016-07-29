package ca.fuwafuwa.kaku.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ca.fuwafuwa.kaku.Database.Models.Entry;
import ca.fuwafuwa.kaku.Database.Models.Kanji;
import ca.fuwafuwa.kaku.Database.Models.KanjiIrregularity;
import ca.fuwafuwa.kaku.Database.Models.Meaning;
import ca.fuwafuwa.kaku.Database.Models.MeaningAdditionalInfo;
import ca.fuwafuwa.kaku.Database.Models.MeaningAntonym;
import ca.fuwafuwa.kaku.Database.Models.MeaningCrossReference;
import ca.fuwafuwa.kaku.Database.Models.MeaningDialect;
import ca.fuwafuwa.kaku.Database.Models.MeaningField;
import ca.fuwafuwa.kaku.Database.Models.MeaningGloss;
import ca.fuwafuwa.kaku.Database.Models.MeaningKanjiRestriction;
import ca.fuwafuwa.kaku.Database.Models.MeaningLoanSource;
import ca.fuwafuwa.kaku.Database.Models.MeaningMisc;
import ca.fuwafuwa.kaku.Database.Models.MeaningPartOfSpeech;
import ca.fuwafuwa.kaku.Database.Models.MeaningReadingRestriction;
import ca.fuwafuwa.kaku.Database.Models.Reading;
import ca.fuwafuwa.kaku.Database.Models.ReadingIrregularity;
import ca.fuwafuwa.kaku.Database.Models.ReadingPriority;
import ca.fuwafuwa.kaku.Database.Models.ReadingRestriction;

/**
 * Created by Xyresic on 7/26/2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "OrmLiteJmDict.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    private Context mContext;
    private Dao<Entry, String> entryDao;

    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static synchronized DatabaseHelper getHelper(Context context){
        if (instance == null){
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Entry.class);
            TableUtils.createTable(connectionSource, Kanji.class);
            TableUtils.createTable(connectionSource, KanjiIrregularity.class);
            TableUtils.createTable(connectionSource, Meaning.class);
            TableUtils.createTable(connectionSource, MeaningAdditionalInfo.class);
            TableUtils.createTable(connectionSource, MeaningAntonym.class);
            TableUtils.createTable(connectionSource, MeaningCrossReference.class);
            TableUtils.createTable(connectionSource, MeaningDialect.class);
            TableUtils.createTable(connectionSource, MeaningField.class);
            TableUtils.createTable(connectionSource, MeaningGloss.class);
            TableUtils.createTable(connectionSource, MeaningKanjiRestriction.class);
            TableUtils.createTable(connectionSource, MeaningLoanSource.class);
            TableUtils.createTable(connectionSource, MeaningMisc.class);
            TableUtils.createTable(connectionSource, MeaningPartOfSpeech.class);
            TableUtils.createTable(connectionSource, MeaningReadingRestriction.class);
            TableUtils.createTable(connectionSource, Reading.class);
            TableUtils.createTable(connectionSource, ReadingIrregularity.class);
            TableUtils.createTable(connectionSource, ReadingPriority.class);
            TableUtils.createTable(connectionSource, ReadingRestriction.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException();
    }

    public void deleteDatabase(){
        mContext.deleteDatabase("/data/data/ca.fuwafuwa.kaku/databases/" + DATABASE_NAME);
    }

    public Dao<Entry, String> getEntryDao() throws SQLException {
        if (entryDao == null){
            entryDao = getDao(Entry.class);
        }
        return entryDao;
    }
}
