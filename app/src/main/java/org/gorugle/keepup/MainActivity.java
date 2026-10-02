package org.gorugle.keepup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                Log.d(TAG, "POST_NOTIFICATIONS: " + granted);

                if (granted) {
                    start();
                    return;
                }

                // The dialog is no longer shown once the user denies it,
                // so open the notification settings of this app
                Log.d(TAG, "ACTION_APP_NOTIFICATION_SETTINGS");

                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Toast.makeText(
                        getApplicationContext(),
                        R.string.notification_permission,
                        Toast.LENGTH_LONG)
                        .show();

                finishAndRemoveTask();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        // The notification is the only way to stop the service, so require it first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            // On recreation, the result of the pending request is delivered to the launcher
            if (savedInstanceState == null) {
                Log.d(TAG, "POST_NOTIFICATIONS");

                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
            return;
        }

        start();
    }

    private void start() {
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
