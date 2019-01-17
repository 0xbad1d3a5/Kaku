@file:JvmName("Constants")

package ca.fuwafuwa.kaku

// Thanks to the fact that SqliteOpenHelper.onUpgrade() doesn't work (due to multi-threading and getDao() being called before onUpgrade()),
// we version/upgrade the DBs by changing the name. Lol. Should probably fix this if this becomes an issue in the future.
const val JMDICT_DATABASE_NAME = "DB_KakuDict.db"
const val TESS_FOLDER_NAME = "tessdata"
const val TESS_DATA_NAME = "jpn.traineddata"
const val SCREENSHOT_FOLDER_NAME = "screenshots"

const val DB_SPLIT_CHAR = "\ufffc"
const val DB_JMDICT_NAME = "JMDICT"
const val DB_KANJIDICT_NAME = "KANJIDICT"
const val DB_ENAMEDICT_NAME = "ENAMEDICT"

const val KAKU_PREF_FILE = "ca.fuwafuwa.kaku"
const val KAKU_PREF_IMAGE_FILTER = "ImageFilter"
const val KAKU_PREF_TEXT_DIRECTION = "TextDirection"
const val KAKU_PREF_INSTANT_MODE = "InstantMode"

const val EXTRA_PROJECTION_RESULT_CODE = "ca.fuwafuwa.kaku.PROJECTION_RESULT_CODE"
const val EXTRA_PROJECTION_RESULT_INTENT = "ca.fuwafuwa.kaku.PROJECTION_RESULT_INTENT"

const val WINDOW_CAPTURE = "WINDOW_CAPTURE"
const val WINDOW_INFO = "WINDOW_INFO"
const val WINDOW_EDIT = "WINDOW_EDIT"
const val WINDOW_INSTANT = "WINDOW_INSTANT"

const val KAKU_CHANNEL_ID = "kaku_notification_channel_id"
const val KAKU_CHANNEL_NAME = "Show Kaku Notification"

const val REQUEST_SCREENSHOT = 100
const val REQUEST_DRAW_ON_TOP = 200
const val REQUEST_SERVICE_TOGGLE_IMAGE_PREVIEW = 300
const val REQUEST_SERVICE_TOGGLE_PAGE_MODE = 400
const val REQUEST_SERVICE_TOGGLE_INSTANT_MODE = 500
const val REQUEST_SERVICE_SHUTDOWN = 600