package ca.fuwafuwa.kaku.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by 0xbad1d3a5 on 12/1/2016.
 */

public abstract class DatabaseHelper extends OrmLiteSqliteOpenHelper implements IDatabaseHelper {

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public abstract void deleteDatabase();
}