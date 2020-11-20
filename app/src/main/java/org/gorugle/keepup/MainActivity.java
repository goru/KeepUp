package org.gorugle.keepup;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        if (!Settings.canDrawOverlays(this)) {
            Log.d(TAG, "ACTION_MANAGE_OVERLAY_PERMISSION");

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(
                    getApplicationContext(),
                    R.string.system_alert_window_permission,
                    Toast.LENGTH_LONG)
                    .show();

            finishAndRemoveTask();

            return;
        }

        Log.d(TAG, "startForegroundService");

        startForegroundService(new Intent(this, KeepUpService.class));

        finishAndRemoveTask();
    }
}