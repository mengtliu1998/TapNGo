package com.example.sbuhacks.ui.main;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationApp extends Application {
    public static final String CHANNEL_1_ID = "channel1";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_DEFAULT
                    );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService((NotificationManager.class));
            manager.createNotificationChannel(channel1);
        }
    }
}
