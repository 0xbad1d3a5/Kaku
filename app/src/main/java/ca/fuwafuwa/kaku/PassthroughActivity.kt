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
    }
}
