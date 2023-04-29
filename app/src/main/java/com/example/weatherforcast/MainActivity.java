package com.example.weatherforcast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherforcast.SQLDataBase.SQLHelper;
import com.example.weatherforcast.SQLDataBase.SQLHelperHistory;
import com.example.weatherforcast.current_City.CurrentWeather;
import com.example.weatherforcast.hours_Weather.HoursAdapter;
import com.example.weatherforcast.hours_Weather.HoursWeather;
import com.example.weatherforcast.hours_Weather.ListHours;
import com.example.weatherforcast.search_City.CityName;
import com.example.weatherforcast.service.backgroundService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainAcitvity";
    private backgroundService mBackgroundService;
    private boolean isServiceConnected;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            backgroundService.MyBinder myBinder = (backgroundService.MyBinder) iBinder;
            mBackgroundService = myBinder.getBackgroundService();
            isServiceConnected = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBackgroundService = null;
            isServiceConnected = false;
        }
    };
    @BindView(R.id.bgWeather)
    ImageView bgWeather;
    @BindView(R.id.tvCurrentCity)
    TextView tvCurrentCity;
    @BindView(R.id.tvCurrentTemple)
    TextView tvCurrentTemple;
    @BindView(R.id.tvCurrentDescription)
    TextView tvCurrentDescription;
    @BindView(R.id.tvHightAndLow)
    TextView tvHightAndLow;
    @BindView(R.id.imgCurrentDescription)
    ImageView imgCurrentDescription;
    @BindView(R.id.btnHourly)
    Button btnHourly;
    @BindView(R.id.btnWeek)
    Button btnWeek;
    @BindView(R.id.vUnderHoure)
    View vUnderHoure;
    @BindView(R.id.vUnderWeek)
    View vUnderWeek;
    @BindView(R.id.rvHours)
    RecyclerView rvHours;
    @BindView(R.id.rvDayly)
    RecyclerView rvDayly;
    @BindView(R.id.btnList)
    ImageButton btnList;
    @BindView(R.id.btnLocation)
    ImageButton btnLocation;

    @BindView(R.id.tvWindSpeed)
    TextView tvWindSpeed;
    @BindView(R.id.tvHumidity)
    TextView tvHumidity;
    @BindView(R.id.tvFeelsLike)
    TextView tvFeelsLike;
    @BindView(R.id.tvPressure)
    TextView tvPressure;
    @BindView(R.id.tvTime)
    TextView tvTime;
    Handler handler = new Handler();

    private WeatherServices mWeatherServices;
    private ArrayList<ListHours> mListHours;
    private HoursAdapter mHoursAdapter;
    private String currentCity;
    private String lat, lon;
    private static final int REQUEST_CODE = 1;
    private SQLHelper mSqlHelper;
    private SQLHelperHistory mSqlHelperHistory;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        requestLocation();
        Intent intent = new Intent(this, backgroundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inData();
        inView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateTime);
        Intent intent = new Intent(this, backgroundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTime);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this, backgroundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, backgroundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            lat = data.getStringExtra("lat");
            lon = data.getStringExtra("lon");
            Log.d(TAG, "onActivityResult: lat " + lat + " lon " + lon);
            callApiByLocation(lat, lon);
            requestHoursWeatherByLocation(lat, lon);
        }
    }

    private void inView() {
        ButterKnife.bind(this);
        btnHourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vUnderHoure.setVisibility(View.VISIBLE);
                vUnderWeek.setVisibility(View.GONE);
                rvDayly.setVisibility(View.GONE);
                rvHours.setVisibility(View.VISIBLE);
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vUnderHoure.setVisibility(View.GONE);
                vUnderWeek.setVisibility(View.VISIBLE);
                rvDayly.setVisibility(View.VISIBLE);
                rvHours.setVisibility(View.GONE);
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListCityActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        rvHours.setAdapter(mHoursAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void getTime() {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("GMT+7");
        calendar.setTimeZone(timeZone);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());
        tvTime.setText(currentTime + "  " + currentDate);
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            getTime();
            handler.postDelayed(this, 1000);
        }
    };

    private void inData() {
        mSqlHelper = new SQLHelper(getApplicationContext());
        mSqlHelperHistory = new SQLHelperHistory(getApplicationContext());
        mWeatherServices = RetrofitClient.getServices(Global.BASE_URL, WeatherServices.class);
        ArrayList<CityName> cities = mSqlHelper.getListCity();
        if (cities.size() == 0) {
            getLocation();
        } else {
            lat = String.valueOf(cities.get(0).getCoord().getLat());
            lon = String.valueOf(cities.get(0).getCoord().getLon());
            callApiByLocation(lat, lon);
            requestHoursWeatherByLocation(lat, lon);
        }
        mListHours = new ArrayList<>();
        mHoursAdapter = new HoursAdapter(mListHours);
    }

    private void requestDaylyWeatherByCityName(String currentCity) {

    }

    private void requestHoursWeatherByCityName(String currentCity) {
        mWeatherServices.getWeatherHoursByCityName(currentCity, Global.API_KEY).enqueue(new Callback<HoursWeather>() {
            @Override
            public void onResponse(Call<HoursWeather> call, Response<HoursWeather> response) {
                requestListHourWeather(response);
            }

            @Override
            public void onFailure(Call<HoursWeather> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
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
        Log.d(TAG, "onResponse: response " + response.body());
        if (response.isSuccessful()) {
            if (response.code() == 200) {
                HoursWeather hoursWeather = response.body();
                mListHours.clear();
                for (int i = 0; i < 20; i++) {
                    mListHours.add((hoursWeather.getListHours().get(i)));
                }
                Log.d(TAG, "onResponse: mListHours.size " + mListHours.size());
                bindData(mListHours);
            }
        }
    }

    private void bindData(ArrayList<ListHours> mListHours) {
        if (mListHours.size() <= 0) {
            Log.d(TAG, "bindData: mListHours trống");
        } else {
            Log.d(TAG, "bindData: mListHours.size " + mListHours.size());
            mHoursAdapter.updateData(mListHours);
        }
    }

    private void getLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                callApiByLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                requestHoursWeatherByLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
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
                        bindViewCurrentWeater(currentWeather);
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
            }
        });
    }

    private void callApiByCityName(String currentCity) {
        mWeatherServices.getWeatherByCityName(currentCity, Global.VN, Global.API_KEY).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        CurrentWeather currentWeather = response.body();
                        bindViewCurrentWeater(currentWeather);
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bindViewCurrentWeater(CurrentWeather currentWeather) {
        tvCurrentCity.setText(currentWeather.getName());
        tvCurrentTemple.setText(Global.convertKtoC(currentWeather.getMain().getTemp()));
        tvCurrentDescription.setText(currentWeather.getWeather().get(0).getDescription().toUpperCase());
        tvHightAndLow.setText("H: " + Global.convertKtoC(currentWeather.getMain().getTempMax())
                + " - L: " + Global.convertKtoC(currentWeather.getMain().getTempMin()));
        Glide.with(getApplicationContext())
                .load(Global.getImageDescription(currentWeather.getWeather().get(0).getIcon()))
                .into(imgCurrentDescription);
        tvWindSpeed.setText(currentWeather.getWind().getSpeed() + " m/s");
        tvHumidity.setText(currentWeather.getMain().getHumidity() + "%");
        tvFeelsLike.setText(Global.convertKtoC(currentWeather.getMain().getFeelsLike()));
        tvPressure.setText(currentWeather.getMain().getPressure() + " hPa");

        String iconDescription = currentWeather.getWeather().get(0).getIcon();
        if (iconDescription.equals("01d") || iconDescription.equals("02d")) {
            bgWeather.setBackgroundResource(R.drawable.bg_01d_02d);
        } else if (iconDescription.equals("01n") || iconDescription.equals("02n")) {
            bgWeather.setBackgroundResource(R.drawable.bg_01n_02n);
        } else if (iconDescription.equals("03d") || iconDescription.equals("04d")) {
            bgWeather.setBackgroundResource(R.drawable.bg_03d_04d);
        } else if (iconDescription.equals("03n") || iconDescription.equals("04n")) {
            bgWeather.setBackgroundResource(R.drawable.bg_03n_04n);
        } else if (iconDescription.equals("09d") || iconDescription.equals("10d")) {
            bgWeather.setBackgroundResource(R.drawable.bg_09d_10d);
        } else if (iconDescription.equals("09n") || iconDescription.equals("10n")) {
            bgWeather.setBackgroundResource(R.drawable.bg_09n_10n);
        } else if (iconDescription.equals("11d")) {
            bgWeather.setBackgroundResource(R.drawable.bg_11d);
        } else if (iconDescription.equals("11n")) {
            bgWeather.setBackgroundResource(R.drawable.bg_11n);
        } else if (iconDescription.equals("13d")) {
            bgWeather.setBackgroundResource(R.drawable.bg_13d);
        } else if (iconDescription.equals("13n")) {
            bgWeather.setBackgroundResource(R.drawable.bg_13n);
        } else if (iconDescription.equals("50d")) {
            bgWeather.setBackgroundResource(R.drawable.bg_50d);
        } else if (iconDescription.equals("50n")) {
            bgWeather.setBackgroundResource(R.drawable.bg_50n);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    //Cấp quyền truy cập vị trí
    private void requestLocation() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    } else {
                    }
                });
        locationPermissionRequest.launch(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
}