package ca.fuwafuwa.kaku

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

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

        setupKakuDatabasesAndFiles()
        checkDrawOnTopPermissions()

        Log.d(TAG, "Sending REQUEST_SCREENSHOT Intent")
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_SCREENSHOT)
    }

    override fun onStart()
    {
        super.onStart()
        startActivityForResult(mMediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_SCREENSHOT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == REQUEST_DRAW_ON_TOP)
        {
            Log.d(TAG, "Recieved ACTION_MANAGE_OVERLAY_PERMISSION Intent")
            if (resultCode != Activity.RESULT_OK)
            {
                Toast.makeText(this, "Unable to draw on top of other apps", Toast.LENGTH_LONG).show()
            }
            return
        }

        if (requestCode == REQUEST_SCREENSHOT)
        {
            Log.d(TAG, "Recieved REQUEST_SCREENSHOT Intent")
            if (resultCode != Activity.RESULT_OK)
            {
                Toast.makeText(this, "Unable to get screen capture token", Toast.LENGTH_LONG).show()
                return
            }

            val i = Intent(this, MainService::class.java)
                    .putExtra(EXTRA_PROJECTION_RESULT_CODE, resultCode)
                    .putExtra(EXTRA_PROJECTION_RESULT_INTENT, data)

            startKakuService(this, i)
        }
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
        var checkPermissions = "Check \"Draw on Top of Other Apps\" permission"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!Settings.canDrawOverlays(this))
            {
                Toast.makeText(this, checkPermissions, Toast.LENGTH_LONG).show()

                Log.d(TAG, "Sending ACTION_MANAGE_OVERLAY_PERMISSION Intent")
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_DRAW_ON_TOP)
            }
        }
        else
        {
            Toast.makeText(this, "Manually $checkPermissions\nKaku might not work on this device", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupKakuDatabasesAndFiles() : Boolean
    {
        try {
            val filesAndPaths = hashMapOf(
                    JMDICT_DATABASE_NAME to filesDir.absolutePath,
                    TESS_DATA_NAME to "${filesDir.absolutePath}/$TESS_FOLDER_NAME")

            if (shouldResetData(filesAndPaths))
            {
                Log.d(TAG, "Resetting Data")
                for (fileAndPath in filesAndPaths){
                    File("${fileAndPath.value}/${fileAndPath.key}").delete()
                }
            }

            copyFilesIfNotExists(filesAndPaths)

            var screenshotPath: String = filesDir.absolutePath + "/$SCREENSHOT_FOLDER_NAME"
            createDirIfNotExists(screenshotPath)
            deleteScreenshotsOlderThanOneDay(screenshotPath)
        }
        catch (e: Exception)
        {
            Toast.makeText(this, "Unable to setup Kaku database", Toast.LENGTH_LONG).show()
            return false
        }

        return true
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

    private fun deleteScreenshotsOlderThanOneDay(path: String)
    {
        var dir = File(path)
        if (dir.exists())
        {
            var listFiles = dir.listFiles()
            var purgeTime = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000
            for (file in listFiles)
            {
                if (file.lastModified() < purgeTime)
                {
                    file.delete()
                }
            }
        }
    }

    companion object
    {
        private val TAG = MainService::class.java.name
    }
}
