package ca.fuwafuwa.kaku.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.fuwafuwa.kaku.Database.Models.Entry;

/**
 * Created by 0x1bad1d3a on 5/2/2016.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "JmDict.db";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DictContract.JmEntry.TABLE_NAME + " (" +
            DictContract.JmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DictContract.JmEntry.COLUMN_KANJI + " TEXT," +
            DictContract.JmEntry.COLUMN_READING + " TEXT," +
            DictContract.JmEntry.COLUMN_SENSE + " TEXT" +
            ")";

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void createEntry(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DictContract.JmEntry.COLUMN_KANJI, entry.getKanji());
        values.put(DictContract.JmEntry.COLUMN_READING, entry.getReading());
        values.put(DictContract.JmEntry.COLUMN_SENSE, entry.getSense());

        db.insert(DictContract.JmEntry.TABLE_NAME, null, values);
    }

    public Entry getEntry(String kanji){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(DictContract.JmEntry.TABLE_NAME, null,
                String.format("%s LIKE ?", DictContract.JmEntry.COLUMN_KANJI),
                new String[] { String.format("%s%%", kanji) }, null, null, null);

        Entry entry = new Entry();

        if (c != null && c.getCount() != 0){
            c.moveToFirst();

            entry.setKanji(c.getString(c.getColumnIndex(DictContract.JmEntry.COLUMN_KANJI)));
            entry.setReading(c.getString(c.getColumnIndex(DictContract.JmEntry.COLUMN_READING)));
            entry.setSense(c.getString(c.getColumnIndex(DictContract.JmEntry.COLUMN_SENSE)));
        }

        return entry;
    }
}
