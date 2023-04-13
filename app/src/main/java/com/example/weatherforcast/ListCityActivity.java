package com.example.weatherforcast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.weatherforcast.History_search.IOnItemClickListener;
import com.example.weatherforcast.History_search.ListCityAdapter;
import com.example.weatherforcast.History_search.SwipeToDeleteItem;
import com.example.weatherforcast.SQLDataBase.SQLHelper;
import com.example.weatherforcast.current_City.CurrentWeather;
import com.example.weatherforcast.search_City.CityName;
import com.example.weatherforcast.search_City.Coord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCityActivity extends AppCompatActivity implements IOnItemClickListener, SwipeToDeleteItem.OnSwipeListener {
    final String TAG = "ListCityActivity";
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    @BindView(R.id.rvListCity)
    RecyclerView rvListCity;

    private WeatherServices mWeatherServices;
    private static final int REQUEST_CODE = 1;
    private String newCity, newLatitude, newLongtitude;
    private SQLHelper mSqlHelper;
    private ArrayList<CurrentWeather> mListHistoryCity;
    private ListCityAdapter mListCityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_city);
        mWeatherServices = RetrofitClient.getServices(Global.BASE_URL, WeatherServices.class);
        inData();
        inView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            newCity = data.getStringExtra("newCity");
            newLatitude = data.getStringExtra("lat");
            newLongtitude = data.getStringExtra("lon");
            CityName cityName = new CityName();
            cityName.setName(newCity);
            Coord coord = new Coord();
            coord.setLat(Float.valueOf(newLatitude));
            coord.setLon(Float.valueOf(newLongtitude));
            cityName.setCoord(coord);
            checkDuplicate(cityName, mSqlHelper);
            intentData(newLatitude, newLongtitude);
        }
        loadListHistoryCity(mSqlHelper);
        finish();
    }

    private void checkDuplicate(CityName cityName, SQLHelper mSqlHelper) {
        ArrayList<CityName> cities = new ArrayList<>();
        cities = mSqlHelper.getListCity();
        Boolean check = true;
        if (cities.size() == 0 || cities == null) {
            mSqlHelper.addCity(cityName);
        } else {
            for (int i = 0; i < cities.size(); i++) {
                if (cityName.getName().equals(cities.get(i).getName())) {
                    check = false;
                    break;
                }
            }
            if (check == true) {
                mSqlHelper.addCity(cityName);
            }
        }
    }

    private void loadListHistoryCity(SQLHelper mSqlHelper) {
        ArrayList<CityName> cities = mSqlHelper.getListCity();
        String lat, lon;
        mListHistoryCity.clear();
        for (int i = 0; i < cities.size(); i++) {
            lat = String.valueOf(cities.get(i).getCoord().getLat());
            lon = String.valueOf(cities.get(i).getCoord().getLon());

            //Do thời gian trả về của retrofit mỗi lần request là khác nhau lên số thứ tự sẽ không lần lượt
            int finalI = i;
            mWeatherServices.getWeatherByLocation(lat, lon, Global.VN, Global.API_KEY).enqueue(new Callback<CurrentWeather>() {
                @Override
                public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            CurrentWeather currentWeather = response.body();
                            currentWeather.setId(cities.get(finalI).getId());
                            mListHistoryCity.add(currentWeather);
                            binData(mListHistoryCity);
                        }
                    }

                }

                @Override
                public void onFailure(Call<CurrentWeather> call, Throwable t) {

                }
            });
        }
    }

    private void binData(ArrayList<CurrentWeather> mListHistoryCity) {
        if (mListHistoryCity.size() <= 0) {
            Log.d(TAG, "bindData: mListHistoryCity trống");
        } else {
            Log.d(TAG, "bindData: mListHistoryCity.size " + mListHistoryCity.size());

            //Có thể nâng APi lên 24 cũng được do 21 = android 5.0 giờ cũng ít máy rồi
            mListCityAdapter.updateData((ArrayList<CurrentWeather>) mListHistoryCity.stream().sorted(Comparator.comparingInt(CurrentWeather::getId)).collect(Collectors.toList()));
        }
    }

    private void inData() {
        mSqlHelper = new SQLHelper(getApplicationContext());
        mListHistoryCity = new ArrayList<>();
        mListCityAdapter = new ListCityAdapter(mListHistoryCity);
        loadListHistoryCity(mSqlHelper);
    }

    private void inView() {
        ButterKnife.bind(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCityActivity.this, SearchCity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        mListCityAdapter.setIOnItemClickListener(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteItem(mListCityAdapter, this::onSwiped));
        itemTouchHelper.attachToRecyclerView(rvListCity);
        rvListCity.setAdapter(mListCityAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(CurrentWeather currentWeather) {
        String lat = String.valueOf(currentWeather.getCoord().getLat());
        String lon = String.valueOf(currentWeather.getCoord().getLon());
        intentData(lat, lon);
    }

    private void intentData(String lat, String lon) {
        Intent intent = new Intent();
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        Log.d(TAG, "searchData: lat " + lat + " lon " + lon);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
        CityName cityNameDelete = new CityName();
        cityNameDelete.setId(mListHistoryCity.get(position).getId());
        cityNameDelete.setName(mListHistoryCity.get(position).getName());
        Coord coordDelete = new Coord();
        coordDelete.setLat(mListHistoryCity.get(position).getCoord().getLat());
        coordDelete.setLon(mListHistoryCity.get(position).getCoord().getLon());
        cityNameDelete.setCoord(coordDelete);

        mSqlHelper.deleteCity(cityNameDelete);

        ListCityAdapter.ListCityViewHolder listCityViewHolder = (ListCityAdapter.ListCityViewHolder) viewHolder;
        listCityViewHolder.deleteItem(position);
        mListHistoryCity.remove(position);

//        ArrayList<CityName> newListCityName = new ArrayList<>();
//        CityName cityName = new CityName();
//        Coord coord = new Coord();
//        for (int i = 0; i < mListHistoryCity.size(); i++) {
//            cityName.setName(mListHistoryCity.get(position).getName());
//            coord.setLat(mListHistoryCity.get(position).getCoord().getLat());
//            coord.setLon(mListHistoryCity.get(position).getCoord().getLon());
//            cityName.setCoord(coord);
//            newListCityName.add(cityName);
//        }
//        mSqlHelper.updateData(newListCityName);

        mListCityAdapter.notifyDataSetChanged();
    }
}