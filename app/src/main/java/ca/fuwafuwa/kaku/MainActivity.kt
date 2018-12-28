package ca.fuwafuwa.kaku

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
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

        setupKakuDatabasesAndFiles(this)

        val i = Intent(this, MainService::class.java)
                .putExtra(MainService.EXTRA_RESULT_CODE, resultCode)
                .putExtra(MainService.EXTRA_RESULT_INTENT, data)

        startService(i)
        this.finish()
    }

    private fun setupKakuDatabasesAndFiles(context: Context)
    {
        val filesAndPaths = hashMapOf(JMDICT_DATABASE_NAME to context.getExternalFilesDir(null).absolutePath,
                KANJI_DATABASE_NAME to context.getExternalFilesDir(null).absolutePath,
                TESS_DATA_NAME to "${context.getExternalFilesDir(null).absolutePath}/$TESS_FOLDER_NAME")

        if (shouldResetData(filesAndPaths))
        {
            Log.d(TAG, "Resetting Data")
            for (fileAndPath in filesAndPaths){
                File("${fileAndPath.value}/${fileAndPath.key}").delete()
            }
        }

        copyFilesIfNotExists(filesAndPaths)

        var screenshotPath: String = context.getExternalFilesDir(null).absolutePath + "/$SCREENSHOT_FOLDER_NAME"
        createDirIfNotExists(screenshotPath)
        deleteScreenshotsOlderThanOneWeek(screenshotPath)
    }

    private fun shouldResetData(filesAndPaths: Map<String, String>) : Boolean
    {
        for (fileAndPath in filesAndPaths){
            if (!File("${fileAndPath.value}/${fileAndPath.key}").exists()) return true
        }
        return false
    }

    private fun createDirIfNotExists(path: String)
    {
        val dir = File(path)
        if (!dir.exists())
        {
            dir.mkdirs()
        }
    }

    private fun copyFilesIfNotExists(filesAndPaths: Map<String, String>)
    {
        for (fileAndPath in filesAndPaths)
        {
            val path = fileAndPath.value
            val fileName = fileAndPath.key
            val filePath = "$path/$fileName"

            if (File(filePath).exists())
            {
                return
            }

            createDirIfNotExists(path)

            val input = assets.open(fileName)
            val output = FileOutputStream(filePath)

            input.copyTo(output);
            output.close()

            Log.d(TAG, "Copied $filePath")
        }
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
