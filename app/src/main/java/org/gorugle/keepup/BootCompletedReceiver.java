package org.gorugle.keepup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

        Log.d(TAG, "onReceive");

        if (!Settings.canDrawOverlays(context)) {
            Log.d(TAG, "ACTION_MANAGE_OVERLAY_PERMISSION");
            return;
        }

        Log.d(TAG, "startForegroundService");

        context.startForegroundService(new Intent(context, KeepUpService.class));
    }
}