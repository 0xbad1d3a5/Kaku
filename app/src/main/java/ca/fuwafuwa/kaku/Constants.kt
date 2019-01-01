@file:JvmName("Constants")

package ca.fuwafuwa.kaku

// Thanks to the fact that SqliteOpenHelper.onUpgrade() doesn't work (due to multi-threading and getDao() being called before onUpgrade()),
// we version/upgrade the DBs by changing the name. Lol. Should probably fix this if this becomes an issue in the future.
const val JMDICT_DATABASE_NAME = "DB_JmDict.db"
const val KANJI_DATABASE_NAME = "DB_KanjiDict.db"
const val TESS_FOLDER_NAME = "tessdata"
const val TESS_DATA_NAME = "jpn.traineddata"
const val SCREENSHOT_FOLDER_NAME = "screenshots"

const val KAKU_PREF_FILE = "ca.fuwafuwa.kaku"
const val KAKU_PREF_SHOW_PREVIEW_IMAGE = "ShowPreviewImage"
const val KAKU_PREF_HORIZONTAL_TEXT = "HorizontalText"

const val EXTRA_PROJECTION_RESULT_CODE = "ca.fuwafuwa.kaku.PROJECTION_RESULT_CODE"
const val EXTRA_PROJECTION_RESULT_INTENT = "ca.fuwafuwa.kaku.PROJECTION_RESULT_INTENT"
const val EXTRA_TOGGLE_IMAGE_PREVIEW = "ca.fuwafuwa.kaku.TOGGLE_IMAGE_PREVIEW"
const val EXTRA_TOGGLE_PAGE_MODE = "ca.fuwafuwa.kaku.TOGGLE_PAGE_MODE"

const val REQUEST_SCREENSHOT = 100
const val REQUEST_DRAW_ON_TOP = 200
const val REQUEST_SERVICE_TOGGLE_IMAGE_PREVIEW = 300
const val REQUEST_SERVICE_TOGGLE_PAGE_MODE = 400
const val REQUEST_SERVICE_SHUTDOWN = 500