package com.xseth.homey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import static com.xseth.homey.utils.ColorRunner.startColorRunner;

public class NotificationActivity extends FragmentActivity {

    private TextView notif_message;
    private ImageView notif_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // View used for rainbow background
        FrameLayout notification_background = findViewById(R.id.notification_background);

        // Start rainbow color thread
        startColorRunner(notification_background);

        notif_message = findViewById(R.id.notification_message);
        notif_icon = findViewById(R.id.notification_icon);

        Intent intent = getIntent();
        notif_message.setText(intent.getIntExtra(MainActivity.NOTIF_MESSAGE, 0));
        notif_icon.setImageResource(intent.getIntExtra(MainActivity.NOTIF_ICON, 0));
    }
}