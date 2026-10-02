package org.gorugle.keepup;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class KeepUpService extends Service {

    private static final String TAG = "KeepUpService";
    private static final String CHANNEL_ID = TAG;
    private static final int NOTIFICATION_ID = 1;

    UserPresentReceiver receiver = new UserPresentReceiver();
    private boolean receiverRegistered = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private AppOpsManager appOpsManager;
    // Called on a binder thread, so re-check the permission on the main thread
    private final AppOpsManager.OnOpChangedListener overlayPermissionListener =
            (op, packageName) -> handler.post(this::updatePermissionState);

    public KeepUpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        // Watch the overlay permission so that the state is updated
        // without going back from the settings screen
        appOpsManager = (AppOpsManager)getSystemService(Context.APP_OPS_SERVICE);
        appOpsManager.startWatchingMode(
                AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                getPackageName(),
                overlayPermissionListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        // Show this on every start so that launching the app while the service
        // is already running also gives feedback
        Toast.makeText(
                getApplicationContext(),
                R.string.app_start,
                Toast.LENGTH_LONG)
                .show();

        NotificationManager manager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_name),
                NotificationManager.IMPORTANCE_LOW);
        manager.createNotificationChannel(channel);

        startForeground(NOTIFICATION_ID, buildNotification(Settings.canDrawOverlays(this)));

        updatePermissionState();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        appOpsManager.stopWatchingMode(overlayPermissionListener);
        handler.removeCallbacksAndMessages(null);

        if (receiverRegistered) {
            getApplicationContext().unregisterReceiver(receiver);
            receiverRegistered = false;
        }

        Toast.makeText(
                getApplicationContext(),
                R.string.app_stop,
                Toast.LENGTH_LONG)
                .show();
    }

    private void updatePermissionState() {
        boolean granted = Settings.canDrawOverlays(this);

        Log.d(TAG, "updatePermissionState: " + granted);

        // Launching Keep from the background requires the overlay permission,
        // so receive ACTION_USER_PRESENT only while it is granted
        if (granted && !receiverRegistered) {
            // ACTION_USER_PRESENT is sent by SystemUI, which runs with its own uid,
            // so the receiver must be exported. It is a protected broadcast
            // that only the system can send.
            ContextCompat.registerReceiver(
                    getApplicationContext(),
                    receiver,
                    new IntentFilter(Intent.ACTION_USER_PRESENT),
                    ContextCompat.RECEIVER_EXPORTED);
            receiverRegistered = true;
        } else if (!granted && receiverRegistered) {
            getApplicationContext().unregisterReceiver(receiver);
            receiverRegistered = false;
        }

        NotificationManager manager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, buildNotification(granted));
    }

    private Notification buildNotification(boolean granted) {
        // Tapping the notification always stops the service
        Intent i = new Intent(this, NotificationContentActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pending = PendingIntent.getActivity(
                this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pending)
                .setAutoCancel(true);

        if (granted) {
            builder.setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_text));
        } else {
            PendingIntent settingsPending = PendingIntent.getActivity(
                    this, 1, createOverlaySettingsIntent(this),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            builder.setContentTitle(getString(R.string.notification_title_no_permission))
                    .setContentText(getString(R.string.notification_text_no_permission))
                    .addAction(new Notification.Action.Builder(
                            null,
                            getString(R.string.notification_action_permission),
                            settingsPending)
                            .build());
        }

        return builder.build();
    }

    // Open the app info screen instead of ACTION_MANAGE_OVERLAY_PERMISSION,
    // which always shows the list of all apps on Android 11 and later
    static Intent createOverlaySettingsIntent(Context context) {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.getPackageName(), null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
