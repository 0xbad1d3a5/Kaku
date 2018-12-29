@file:JvmName("Constants")

package ca.fuwafuwa.kaku

// Thanks to the fact that SqliteOpenHelper.onUpgrade() doesn't work (due to multi-threading and getDao() being called before onUpgrade()),
// we version/upgrade the DBs by changing the name. Lol. Should probably fix this if this becomes an issue in the future.
const val JMDICT_DATABASE_NAME = "DB_JmDict.db"
const val KANJI_DATABASE_NAME = "DB_KanjiDict.db"
const val TESS_FOLDER_NAME = "tessdata"
const val TESS_DATA_NAME = "jpn.traineddata"
const val SCREENSHOT_FOLDER_NAME = "screenshots"

const val REQUEST_SCREENSHOT = 100
const val REQUEST_DRAW_ON_TOP = 200