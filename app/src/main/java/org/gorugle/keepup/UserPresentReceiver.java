package org.gorugle.keepup;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class UserPresentReceiver extends BroadcastReceiver {

    private static final String TAG = "UserPresentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

        Log.d(TAG, intent.getAction());

        if (!intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            return;
        }

        try {
//            String packageName = "com.android.settings";
//            String className = "com.android.settings.Settings";
//            String packageName = "com.twitter.android";
//            String className = "com.twitter.android.StartActivity";
            String packageName = "com.google.android.keep";
            String className = "com.google.android.keep.activities.BrowseActivity";

            Intent i = new Intent();
            i.setClassName(packageName, className);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    context.getApplicationContext(),
                    R.string.app_not_found,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}