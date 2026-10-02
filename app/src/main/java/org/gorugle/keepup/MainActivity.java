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

        // Start the service even without the overlay permission.
        // The service watches the permission and updates its notification.
        Log.d(TAG, "startForegroundService");

        startForegroundService(new Intent(this, KeepUpService.class));

        if (!Settings.canDrawOverlays(this)) {
            Log.d(TAG, "ACTION_APPLICATION_DETAILS_SETTINGS");

            startActivity(KeepUpService.createOverlaySettingsIntent(this));

            Toast.makeText(
                    getApplicationContext(),
                    R.string.system_alert_window_permission,
                    Toast.LENGTH_LONG)
                    .show();
        }

        finishAndRemoveTask();
    }
}