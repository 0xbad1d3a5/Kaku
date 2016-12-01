package ca.fuwafuwa.kaku.Database;

import android.content.Context;

import ca.fuwafuwa.kaku.Database.JmDictDatabase.JmDatabaseHelper;
import ca.fuwafuwa.kaku.Database.KanjiDict2Database.Kd2DatabaseHelper;

/**
 * Created by 0x1bad1d3a on 12/1/2016.
 */

public class DbHelperFactory {

    private static Context mContext;

    public DbHelperFactory(Context context){
        mContext = context;
    }

    public DatabaseHelper instance(Class clazz){
        if (clazz == JmDatabaseHelper.class) {
            return JmDatabaseHelper.instance(mContext);
        }
        else if (clazz == Kd2DatabaseHelper.class){
            return Kd2DatabaseHelper.instance(mContext);
        }
        else {
            return null;
        }
    }
}
