package ca.fuwafuwa.kaku

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import ca.fuwafuwa.kaku.Windows.InformationWindow
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var mMediaProjectionManager: MediaProjectionManager? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        var processText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)

        if (processText != null){
            InformationWindow(this).setTextResults(processText);
            finish()
        }

        if (intent.hasExtra(MainService.KAKU_TOGGLE_IMAGE_PREVIEW)){

            var prefs = getSharedPreferences(MainService.KAKU_PREF_FILE, Context.MODE_PRIVATE)
            var mShowPreviewImage = prefs.getBoolean(MainService.KAKU_PREF_SHOW_PREVIEW_IMAGE, true);
            prefs.edit().putBoolean(MainService.KAKU_PREF_SHOW_PREVIEW_IMAGE, !mShowPreviewImage).apply()

            stopService(Intent(this, MainService::class.java))
        }

        if (intent.hasExtra(MainService.KAKU_TOGGLE_PAGE_MODE)){

            var prefs = getSharedPreferences(MainService.KAKU_PREF_FILE, Context.MODE_PRIVATE)
            var mHorizontalText = prefs.getBoolean(MainService.KAKU_PREF_HORIZONTAL_TEXT, true);
            prefs.edit().putBoolean(MainService.KAKU_PREF_HORIZONTAL_TEXT, !mHorizontalText).apply()

            stopService(Intent(this, MainService::class.java))
        }

        var checkPermissions = "Check \"Draw on Top of Other Apps\" Permission"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)){
                Toast.makeText(this, checkPermissions, Toast.LENGTH_LONG).show()
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_DRAW_ON_TOP)
            }
        }
        else {
            Toast.makeText(this, "Manually $checkPermissions\nKaku Might Not Work on This Device", Toast.LENGTH_LONG).show()
        }

        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        startActivityForResult(mMediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_SCREENSHOT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (!(requestCode == REQUEST_SCREENSHOT && resultCode == Activity.RESULT_OK)) {
            return
        }

        if (!isExternalStorageWritable()){
            return
        }

        copyFileIfNotExists(getExternalFilesDir(null).absolutePath, "JmDict.db")
        copyFileIfNotExists(getExternalFilesDir(null).absolutePath, "KanjiDict2.db")
        copyFileIfNotExists(getExternalFilesDir(null).absolutePath + "/tessdata", "jpn.traineddata")

        var screenshotPath: String = getExternalFilesDir(null).absolutePath + "/screenshots"
        createDirIfNotExists(screenshotPath)
        deleteScreenshotsOlderThanOneWeek(screenshotPath)

        val i = Intent(this, MainService::class.java)
                .putExtra(MainService.EXTRA_RESULT_CODE, resultCode)
                .putExtra(MainService.EXTRA_RESULT_INTENT, data)

        startService(i)
        this.finish()
    }

    private fun deleteScreenshotsOlderThanOneWeek(path: String)
    {
        var dir = File(path)
        if (dir.exists())
        {
            var listFiles = dir.listFiles()
            var purgeTime = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
            for (file in listFiles)
            {
                if (file.lastModified() < purgeTime)
                {
                    file.delete()
                }
            }
        }
    }

    private fun createDirIfNotExists(path: String)
    {
        var dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    private fun copyFileIfNotExists(path: String, fileName: String)
    {
        var filePath: String = String.format("%s/%s", path, fileName)

        if (File(filePath).exists()){
            return
        }

        createDirIfNotExists(path)

        val input = assets.open(fileName)
        val output = FileOutputStream(filePath)

        input.copyTo(output);
        output.close()
    }

    private fun isExternalStorageWritable(): Boolean
    {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    companion object
    {
        private val TAG = MainService::class.java.getName()

        private val REQUEST_SCREENSHOT = 100
        private val REQUEST_DRAW_ON_TOP = 200
    }
}
