package ca.fuwafuwa.kaku

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import ca.fuwafuwa.kaku.Windows.InformationWindow

class PassthroughActivity : AppCompatActivity()
{
    private var mMediaProjectionManager: MediaProjectionManager? = null
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

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

        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_SCREENSHOT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (!(requestCode == REQUEST_SCREENSHOT && resultCode == Activity.RESULT_OK)) {
            return
        }

        val i = Intent(this, MainService::class.java)
                .putExtra(MainService.EXTRA_RESULT_CODE, resultCode)
                .putExtra(MainService.EXTRA_RESULT_INTENT, data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(i)
        }
        else {
            startService(i)
        }

        finish()
    }
}
