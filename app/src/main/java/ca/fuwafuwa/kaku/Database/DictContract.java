package ca.fuwafuwa.kaku.Database;

import android.provider.BaseColumns;

/**
 * Created by 0x1bad1d3a on 5/2/2016.
 */
public final class DictContract {

    private DictContract(){}

    public static abstract class JmEntry implements BaseColumns {
        public static final String TABLE_NAME = "ENTRY";
        public static final String COLUMN_KANJI = "KANJI";
        public static final String COLUMN_READING = "READING";
        public static final String COLUMN_SENSE = "SENSE";
    }
}
