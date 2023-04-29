package com.example.weatherforcast.service;

import static com.example.weatherforcast.service.MyApplication.CHANNEL_ID;
import static com.example.weatherforcast.service.MyApplication.CHANNEL_ID_NULL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.weatherforcast.Global;
import com.example.weatherforcast.MainActivity;
import com.example.weatherforcast.R;
import com.example.weatherforcast.RetrofitClient;
import com.example.weatherforcast.WeatherServices;
import com.example.weatherforcast.current_City.CurrentWeather;
import com.example.weatherforcast.hours_Weather.HoursWeather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class backgroundService extends Service {
    private WeatherServices mWeatherServices;
    String lat, lon;
    Handler mHandler = new Handler();
    boolean selectNoti;
    private MyBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public backgroundService getBackgroundService() {
            return backgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showNotificationApp();
        mWeatherServices = RetrofitClient.getServices(Global.BASE_URL, WeatherServices.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.post(updateTime);
        return START_NOT_STICKY;
    }

    private void showNotificationApp() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_app_weather)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(null)
                .setVibrate(null)
                .setShowWhen(false)
                .build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            getTime();
            mHandler.postDelayed(this, 1000);
        }
    };

    private void getTime() {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("GMT+7");
        calendar.setTimeZone(timeZone);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(calendar.getTime());
        if (currentTime.equals("07:00:00")) {
            selectNoti = true;
            getLocation(selectNoti);
        }
        if (currentTime.equals("21:30:00") || currentTime.equals("21:30:10") || currentTime.equals("21:30:20")
                || currentTime.equals("21:30:30")) {
            selectNoti = false;
            getLocation(selectNoti);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification7am(CurrentWeather currentWeather) {
        String cityName = currentWeather.getName();
        String description = currentWeather.getWeather().get(0).getDescription();
        String templ = Global.convertKtoC(currentWeather.getMain().getTemp());

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        String contentText = "Chúc ngày mới tốt lành!\nThời tiết " + cityName + " hôm nay: " + description + "\nNhiệt độ hiện tại " + templ;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Thời tiết")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.icon_app_weather)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(2, notification);
    }

    private void getLocation(boolean selectNoti) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lat = String.valueOf(location.getLatitude());
                        lon = String.valueOf(location.getLongitude());
                        if (selectNoti == true) {
                            callApiByLocation(lat, lon);
                        } else {
                            requestHoursWeatherByLocation(lat, lon);
                        }
                    }
                }
            });
        }
    }

    private void callApiByLocation(String lat, String lon) {
        mWeatherServices.getWeatherByLocation(lat, lon, Global.VN, Global.API_KEY).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        CurrentWeather currentWeather = response.body();
                        sendNotification7am(currentWeather);
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
    }

    private void requestHoursWeatherByLocation(String lat, String lon) {
        mWeatherServices.getWeatherHoursByLocation(lat, lon, Global.VN, Global.API_KEY).enqueue(new Callback<HoursWeather>() {
            @Override
            public void onResponse(Call<HoursWeather> call, Response<HoursWeather> response) {
                requestListHourWeather(response);
            }

            @Override
            public void onFailure(Call<HoursWeather> call, Throwable t) {

            }
        });
    }

    private void requestListHourWeather(Response<HoursWeather> response) {
        if (response.isSuccessful()) {
            if (response.code() == 200) {
                HoursWeather hoursWeather = response.body();
                for (int i = 0; i < 5; i++) {
                    String iconDescription = hoursWeather.getListHours().get(i).getWeather().get(0).getIcon().trim();
                    if (iconDescription.equals("09d") || iconDescription.equals("09n") || iconDescription.equals("10d") ||
                            iconDescription.equals("10n") || iconDescription.equals("11d") || iconDescription.equals("11n")) {
                        sendNotificationBadWeather(hoursWeather, i);
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotificationBadWeather(HoursWeather hoursWeather, int i) {
        String cityName = hoursWeather.getCity().getName();
        String description = hoursWeather.getListHours().get(i).getWeather().get(0).getDescription();
        String templ = Global.convertKtoC(hoursWeather.getListHours().get(i).getMain().getTemp());

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        String contentText = "Cảnh báo thời tiết xấu!\nThời tiết tại " + cityName + " trong khoảng " + (i + 1) * 3 + " giờ tới " + description + "\nNhiệt độ khoảng " + templ;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Thời tiết")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.icon_app_weather)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(2, notification);
    }

}
