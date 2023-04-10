package com.example.weatherforcast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherforcast.current_City.CurrentWeather;
import com.example.weatherforcast.hours_Weather.HoursAdapter;
import com.example.weatherforcast.hours_Weather.HoursWeather;
import com.example.weatherforcast.hours_Weather.ListHours;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainAcitvity";
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

    private WeatherServices mWeatherServices;
    private ArrayList<ListHours> mListHours;
    private HoursAdapter mHoursAdapter;
    private String currentCity = "Hà Nội";
    private String lat, lon;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        requestLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inData();
        inView();
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
                Intent intent = new Intent(MainActivity.this, ListCityActivity2.class);
                startActivity(intent);
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
//                callApiByLocation();
            }
        });
        rvHours.setAdapter(mHoursAdapter);
        getTime();
    }

    private void getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());
        tvTime.setText(currentTime + " " + currentDate);
    }

    private void inData() {
        mWeatherServices = RetrofitClient.getServices(Global.BASE_URL, WeatherServices.class);
        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lon = intent.getStringExtra("lon");
        Log.d(TAG, "inData: lat " + lat + " lon " + lon);
        if (lat == null && lon == null) {
            callApiByCityName(currentCity);
        } else {
            callApiByLocation(lat, lon);
        }
        mListHours = new ArrayList<>();
        requestHoursWeatherByCityName(currentCity);
        requestDaylyWeatherByCityName(currentCity);
        mHoursAdapter = new HoursAdapter(mListHours);
    }

    private void requestDaylyWeatherByCityName(String currentCity) {

    }

    private void requestHoursWeatherByCityName(String currentCity) {
        mWeatherServices.getWeatherHoursByCityName(currentCity, Global.API_KEY).enqueue(new Callback<HoursWeather>() {
            @Override
            public void onResponse(Call<HoursWeather> call, Response<HoursWeather> response) {
                Log.d(TAG, "onResponse: response " + response.body());
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        HoursWeather hoursWeather = response.body();
                        mListHours.clear();
                        for (int i = 0; i < 15; i++) {
                            mListHours.add((hoursWeather.getListHours().get(i)));
                        }
                        Log.d(TAG, "onResponse: mListHours.size " + mListHours.size());
                        bindData(mListHours);
                    }
                }
            }

            @Override
            public void onFailure(Call<HoursWeather> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
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
//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location != null) {
//                        getCityNameFromLocation(location);
//                    }
//                }
//            });
//        }

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
//                            locationCity[0] = location.getLatitude() + "";
//                            locationCity[1] = location.getLongitude() + "";
                                Log.d(TAG, "onSuccess: " + location.getLatitude() + " | " + location.getLongitude());
                            }
                        }
                    });
        }


//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                double lat = location.getLatitude();
//                double lon = location.getLongitude();
//                Log.d(TAG, "onLocationChanged: lat " + lat + " | lon " + lon);
//            }
//        };
    }

    private void callApiByLocation(String lat, String lon) {
        mWeatherServices.getWeatherByLocation(lat, lon, Global.API_KEY).enqueue(new Callback<CurrentWeather>() {
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
        mWeatherServices.getWeatherByCityName(currentCity, Global.API_KEY).enqueue(new Callback<CurrentWeather>() {
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

    private void bindViewCurrentWeater(CurrentWeather currentWeather) {
        tvCurrentCity.setText(currentWeather.getName());
        tvCurrentTemple.setText(Global.convertKtoC(currentWeather.getMain().getTemp()));
        tvCurrentDescription.setText(currentWeather.getWeather().get(0).getDescription().toUpperCase());
        tvHightAndLow.setText("H: " + Global.convertKtoC(currentWeather.getMain().getTempMax())
                + " - L: " + Global.convertKtoC(currentWeather.getMain().getTempMin()));
        Glide.with(getApplicationContext()).load(Global.getImageDescription(currentWeather.getWeather().get(0).getIcon())).into(imgCurrentDescription);
        tvWindSpeed.setText(currentWeather.getWind().getSpeed() + " m/s");
        tvHumidity.setText(currentWeather.getMain().getHumidity() + "%");
        tvFeelsLike.setText(Global.convertKtoC(currentWeather.getMain().getFeelsLike()));
        tvPressure.setText(currentWeather.getMain().getPressure() + " hPa");
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
                        // Precise location access granted.
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                    } else {
                        // No location access granted.
                    }
                });
        locationPermissionRequest.launch(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
}