package org.gorugle.keepup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NotificationContentActivity extends AppCompatActivity {

    private static final String TAG = "NotificationContentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        stopService(new Intent(this, KeepUpService.class));

        finishAndRemoveTask();
    }
}