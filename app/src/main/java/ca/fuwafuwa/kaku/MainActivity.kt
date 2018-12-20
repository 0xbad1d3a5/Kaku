package ca.fuwafuwa.kaku

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    private var mMediaProjectionManager: MediaProjectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "GOT HERE")

        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_SCREENSHOT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SCREENSHOT && resultCode == Activity.RESULT_OK) {

            val i = Intent(this, MainService::class.java)
                    .putExtra(MainService.EXTRA_RESULT_CODE, resultCode)
                    .putExtra(MainService.EXTRA_RESULT_INTENT, data)

            startService(i)
            this.finish()
        }
    }

    companion object {

        private val TAG = MainService::class.java!!.getName()

        private val REQUEST_SCREENSHOT = 100
    }
}
