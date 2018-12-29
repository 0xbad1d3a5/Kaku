package ca.fuwafuwa.kaku

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity()
{
    private var mSectionsPagerAdapter: FragmentStatePagerAdapter? = null
    private var mMediaProjectionManager: MediaProjectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.offscreenPageLimit = 0

        tab_indicator.setupWithViewPager(container, true)

        checkDrawOnTopPermissions()
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_SCREENSHOT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (!(requestCode == REQUEST_SCREENSHOT && resultCode == Activity.RESULT_OK)) {
            return
        }

        // In theory we shouldn't need this permission, but for some reason it crashes on older devices without this,
        // despite the fact that starting in API 19 all apps should be able to write to their private folder, so idk
        if (!isExternalStorageWritable()){
            return
        }

        setupKakuDatabasesAndFiles(this)

        val i = Intent(this, MainService::class.java)
                .putExtra(MainService.EXTRA_RESULT_CODE, resultCode)
                .putExtra(MainService.EXTRA_RESULT_INTENT, data)

        startService(i)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm)
    {
        override fun getItem(position: Int): Fragment
        {
            if (position == 0){
                return StartFragment.newInstance()
            }
            return InstructionFragment.newInstance(position + 1)
        }

        override fun getCount(): Int
        {
            return 10
        }
    }



    private fun checkDrawOnTopPermissions()
    {
        var checkPermissions = "Check \"Draw on Top of Other Apps\" Permission"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!Settings.canDrawOverlays(this))
            {
                Toast.makeText(this, checkPermissions, Toast.LENGTH_LONG).show()
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_DRAW_ON_TOP)
            }
        }
        else
        {
            Toast.makeText(this, "Manually $checkPermissions\nKaku Might Not Work on This Device", Toast.LENGTH_LONG).show()
        }
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
        private val TAG = MainService::class.java.name
    }
}
