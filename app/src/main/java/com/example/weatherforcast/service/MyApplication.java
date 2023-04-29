package com.example.weatherforcast.service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.weatherforcast.R;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "Thông báo thời tiết";
    public static final String CHANNEL_ID_NULL = "WeatherForcast";

    @Override
    public void onCreate() {
        super.onCreate();
        creatNotifichannel();
    }

    private void creatNotifichannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Channel 1
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //Channel 0
            CharSequence name_0 = getString(R.string.channel_name_0);
            int importance_0 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel_0 = new NotificationChannel(CHANNEL_ID_NULL, name_0, importance_0);
            channel.setSound(null, null);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel_0);
        }
    }
}
