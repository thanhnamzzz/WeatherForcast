package com.example.weatherforcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherforcast.SQLDataBase.SQLHelperHistory;
import com.example.weatherforcast.search_City.CityName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchCity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.edtSearchCity)
    AutoCompleteTextView edtSearchCity;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    @BindView(R.id.tvCity)
    TextView tvCity;
    @BindView(R.id.flLayout)
    FlowLayout flLayout;

    private SQLHelperHistory mSqlHelperHistory;

    private List<CityName> cityNamesFromJSon;
    private ArrayList<String> listCityForSearch;
    private ArrayList<CityName> listCitySearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        inData();
        inView();
    }

    private void inView() {
        ButterKnife.bind(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.search_item, listCityForSearch);
        edtSearchCity.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        edtSearchCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String citySearch = edtSearchCity.getText().toString();
                hideKeyBoard();
                checkSelection(citySearch);
                searchData(citySearch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtSearchCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String citySearch = edtSearchCity.getText().toString();
                hideKeyBoard();
                checkSelection(citySearch);
                searchData(citySearch);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String citySearch = edtSearchCity.getText().toString();
                searchData(citySearch);
            }
        });

        for (int i = 0; i < listCitySearched.size(); i++) {
            flLayout.addView(addView(listCitySearched.get(i)));
        }
    }

    private void checkSelection(String citySearch) {
        CityName cityName = new CityName();
        cityName.setName(citySearch);
        Boolean check = true;
        if (listCitySearched.size() == 0 || listCitySearched == null) {
            mSqlHelperHistory.addSearchHistory(cityName);
        } else {
            for (int i = 0; i < listCitySearched.size(); i++) {
                if (citySearch.equals(listCitySearched.get(i).getName())) {
                    check = false;
                    break;
                }
            }
            if (check == true) {
                mSqlHelperHistory.addSearchHistory(cityName);
            }
        }
    }

    private void searchData(String citySearch) {
        for (CityName cityName : cityNamesFromJSon) {
            if (cityName.getName().equals(citySearch)) {
                intentData(cityName);
            }
        }
        edtSearchCity.setText("");
    }

    private void intentData(CityName cityName) {
        String latitude = String.valueOf(cityName.getCoord().getLat());
        String longtitude = String.valueOf(cityName.getCoord().getLon());
        Intent intent = new Intent();
        intent.putExtra("lat", latitude);
        intent.putExtra("lon", longtitude);
        intent.putExtra("newCity", cityName.getName());
        setResult(MainActivity.RESULT_OK, intent);
        finish();
    }

    private View addView(CityName cityName) {
        String city = cityName.getName();
        View view = getLayoutInflater().inflate(R.layout.layout_flowlayout,
                (ViewGroup) getWindow().getDecorView().getRootView(), false);
        TextView tvContentSearch = view.findViewById(R.id.tvContentSearch);
        tvContentSearch.setText(city);
        tvContentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData(city);
            }
        });
        return view;
    }

    private void inData() {
        mSqlHelperHistory = new SQLHelperHistory(getApplicationContext());
        try {
            cityNamesFromJSon = readCityFromJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        listCityForSearch = new ArrayList<>();
        for (CityName cityName : cityNamesFromJSon) {
            listCityForSearch.add(cityName.getName());
        }
        listCitySearched = mSqlHelperHistory.getListCity();
    }

    private List<CityName> readCityFromJson() throws IOException, JSONException {
        List<CityName> cityNames = new ArrayList<>();
        String textCity = readJsonFile("city.list.min.json").replaceAll("\n\r", "");
        Type listType = new TypeToken<List<CityName>>() {
        }.getType();
        Gson gson = new Gson();
        cityNames = gson.fromJson(textCity, listType);
        return cityNames;
    }

    private String readJsonFile(String s) throws IOException {
        InputStream inputStream = getAssets().open(s);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        return new String(buffer, "utf-8");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}