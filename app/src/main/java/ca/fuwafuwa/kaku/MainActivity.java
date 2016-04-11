package ca.fuwafuwa.kaku;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SCREENSHOT = 100;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_SCREENSHOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_SCREENSHOT && resultCode == RESULT_OK){

            Intent i = new Intent(this, MainService.class)
                    .putExtra(MainService.EXTRA_RESULT_CODE, resultCode)
                    .putExtra(MainService.EXTRA_RESULT_INTENT, data);

            startService(i);
            this.finish();
        }
    }
}
