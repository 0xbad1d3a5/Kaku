package ca.fuwafuwa.kaku

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.fuwafuwa.kaku.Dialogs.StarRatingDialogFragment


class MainActivity : AppCompatActivity()
{
    private var mIsActivityVisible = false
    private var mShownRating = false

    private lateinit var mPrefs : SharedPreferences
    private lateinit var mStartKakuIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (isBeta())
        {
            startActivity(Intent(this, BetaActivity::class.java))
        }

        mPrefs = getSharedPreferences(KAKU_PREF_FILE, Context.MODE_PRIVATE)

        if (isFirstLaunch())
        {
            startActivity(Intent(this, TutorialActivity::class.java))
            finish()
        }
        else {
            supportActionBar?.hide()
            setContentView(R.layout.activity_main)

            setupKakuDatabasesAndFiles(this)
        }
    }

    override fun onStart()
    {
        super.onStart()

        checkDrawOnTopPermissions()
        checkScreenRecordPermissions()

        showRatingDialog()
    }

    override fun onPause()
    {
        super.onPause()
        Log.d(TAG, "ACTIVITY INVISIBLE")
        mIsActivityVisible = false
    }

    override fun onResume()
    {
        super.onResume()
        Log.d(TAG, "ACTIVITY VISIBLE")
        mIsActivityVisible = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        Log.d(TAG, "onActivityResult")

        val relaunchAppText = "Relaunch Kaku after verifying permission"

        if (requestCode == REQUEST_DRAW_ON_TOP)
        {
            Log.d(TAG, "Recieved ACTION_MANAGE_OVERLAY_PERMISSION Intent")

            if (resultCode != Activity.RESULT_OK)
            {
                Toast.makeText(this, "Check Permission: Draw on Other Apps\n$relaunchAppText", Toast.LENGTH_LONG).show()
                finish()
            }

            return
        }

        if (requestCode == REQUEST_SCREENSHOT)
        {
            Log.d(TAG, "Recieved REQUEST_SCREENSHOT Intent")

            if (resultCode != Activity.RESULT_OK)
            {
                Toast.makeText(this, "Check Permission: Record Screen\n$relaunchAppText", Toast.LENGTH_LONG).show()
                finish()
            }

            mStartKakuIntent = Intent(this, MainService::class.java)
                    .putExtra(EXTRA_PROJECTION_RESULT_CODE, resultCode)
                    .putExtra(EXTRA_PROJECTION_RESULT_INTENT, data)
            return
        }
    }

    fun startKaku(startFragment: MainStartFragment)
    {
        if (MainService.IsRunning())
        {
            return
        }

        if (!mIsActivityVisible)
        {
            return
        }

        if (::mStartKakuIntent.isInitialized)
        {
            startFragment.onKakuLoadStart()

            val totalDuration = 2000
            object : CountDownTimer(totalDuration.toLong(), 10)
            {
                override fun onFinish()
                {
                    startFragment.onKakuLoaded()
                    startKakuService(this@MainActivity, mStartKakuIntent)
                }

                override fun onTick(millisUntilFinished: Long)
                {
                }
            }.start()
        }
        else {
            Toast.makeText(this, "Unable to start Kaku service", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkDrawOnTopPermissions()
    {
        var checkPermissions = "Check \"Draw on Top of Other Apps\" permission"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!Settings.canDrawOverlays(this))
            {
                Log.d(TAG, "Sending ACTION_MANAGE_OVERLAY_PERMISSION Intent")
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), REQUEST_DRAW_ON_TOP)
            }
        }
        else
        {
            Toast.makeText(this, "Manually $checkPermissions\nKaku might not work on this device", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkScreenRecordPermissions()
    {
        Log.d(TAG, "Sending REQUEST_SCREENSHOT Intent")
        val mediaProjectionManager: MediaProjectionManager? = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_SCREENSHOT)
    }

    private fun showRatingDialog()
    {
        if (mShownRating)
        {
            return
        }

        mShownRating = true

        val timesLaunched = mPrefs.getInt(KAKU_PREF_TIMES_LAUNCHED, 1)
        val rated = mPrefs.getBoolean(KAKU_PREF_PLAY_STORE_RATED, false)

        if (timesLaunched % 20 == 0 && !rated)
        {
            StarRatingDialogFragment().show(supportFragmentManager, "StarRating")
        }
    }

    private fun isFirstLaunch() : Boolean
    {
        return mPrefs.getBoolean(KAKU_PREF_FIRST_LAUNCH, true)
    }

    private fun isBeta() : Boolean
    {
        val CURRENT_PROD_VERSION = 73 // just hardcoded, change when a build is ready to be rolled out to prod
        return BuildConfig.VERSION_CODE > CURRENT_PROD_VERSION
    }

    companion object
    {
        private val TAG = MainActivity::class.java.name
    }
}
