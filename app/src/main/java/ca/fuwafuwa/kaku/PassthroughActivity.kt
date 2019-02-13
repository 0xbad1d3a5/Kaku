package ca.fuwafuwa.kaku

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import ca.fuwafuwa.kaku.Windows.InformationWindow
import ca.fuwafuwa.kaku.Windows.WindowCoordinator

class PassthroughActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setupKakuDatabasesAndFiles(this)

        var processText : String? = null
        when {
            intent?.action == Intent.ACTION_PROCESS_TEXT ->
            {
                processText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
            }
            intent?.action == Intent.ACTION_SEND ->
            {
                if ("text/plain" == intent.type)
                {
                    processText = intent.getStringExtra(Intent.EXTRA_TEXT)
                }
            }
        }

        if (processText != null)
        {
            InformationWindow(applicationContext, WindowCoordinator(applicationContext)).setResult(processText)
            finish()
        }
    }
}
