package com.example.weatherforcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

    private static final String TAG = "Main";
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

    private List<CityName> cityNames;
    private ArrayList<String> listCityName;

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
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.search_item, listCityName);
        edtSearchCity.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

//        edtSearchCity.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.d(TAG, "beforeTextChanged: "+s);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d(TAG, "onTextChanged: "+s);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.d(TAG, "afterTextChanged: "+s);
//            }
//        });
        edtSearchCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtSearchCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchData();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData();
            }
        });
    }

    private void searchData() {
        String citySearch = edtSearchCity.getText().toString();
        for (CityName cityName : cityNames) {
            if (cityName.getName().equals(citySearch)) {
                double latitude = cityName.getCoord().getLat();
                double longtitude = cityName.getCoord().getLon();
                Intent intent = new Intent(SearchCity.this, MainActivity.class);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longtitude);
                startActivity(intent);
            }
        }
        flLayout.addView(addView(citySearch));
        edtSearchCity.setText("");
    }

    private View addView(String test) {
        View view = getLayoutInflater().inflate(R.layout.layout_flowlayout,
                (ViewGroup) getWindow().getDecorView().getRootView(), false);
        TextView tvContentSearch = view.findViewById(R.id.tvContentSearch);
        tvContentSearch.setText(test);
        return view;
    }

    private void inData() {
        try {
            cityNames = readCityFromJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        listCityName = new ArrayList<>();
        for (CityName cityName : cityNames) {
            listCityName.add(cityName.getName());
        }

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
    }
}