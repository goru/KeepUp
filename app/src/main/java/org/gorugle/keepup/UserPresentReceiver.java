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

//        String packageName = "com.android.settings";
//        String packageName = "com.twitter.android";
        String packageName = "com.google.android.keep";

        // Launch the same activity as the launcher does, so that this keeps working
        // even if the app changes its internal activity names.
        // The package must be listed in <queries> in AndroidManifest.xml to be visible.
        Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (i == null) {
            showAppNotFound(context);
            return;
        }

        try {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.getApplicationContext().startActivity(i);
        } catch (ActivityNotFoundException e) {
            showAppNotFound(context);
        }
    }

    private void showAppNotFound(Context context) {
        Toast.makeText(
                context.getApplicationContext(),
                R.string.app_not_found,
                Toast.LENGTH_LONG)
                .show();
    }
}