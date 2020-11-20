package org.gorugle.keepup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class KeepUpService extends Service {

    private static final String TAG = "KeepUpService";
    UserPresentReceiver receiver = new UserPresentReceiver();

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

        Toast.makeText(
                getApplicationContext(),
                R.string.app_start,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        String channelID = TAG;

        NotificationManager manager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                channelID,
                getString(R.string.notification_name),
                NotificationManager.IMPORTANCE_LOW);
        manager.createNotificationChannel(channel);

        Intent i = new Intent(this, NotificationContentActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pending = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext(), channelID)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(pending)
                .setAutoCancel(true)
                .build();

        startForeground(1, notification);

        getApplicationContext().registerReceiver(
                receiver,
                new IntentFilter(Intent.ACTION_USER_PRESENT));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        getApplicationContext().unregisterReceiver(receiver);

        Toast.makeText(
                getApplicationContext(),
                R.string.app_stop,
                Toast.LENGTH_LONG)
                .show();
    }
}