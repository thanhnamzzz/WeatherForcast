package com.example.weatherforcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListCityActivity2 extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    @BindView(R.id.rvListCity)
    RecyclerView rvListCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_city);
        inData();
        inView();
    }

    private void inData() {

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
                Intent intent = new Intent(ListCityActivity2.this, SearchCity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}