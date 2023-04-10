package com.example.weatherforcast;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit weatherRetrofitInstance;
    private static Retrofit getWeatherRetrofitInstance (String url) {
        if (weatherRetrofitInstance == null) {
            weatherRetrofitInstance =new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return weatherRetrofitInstance;
    }
    public static <T>T getServices (String url, Class<T> services){
        return getWeatherRetrofitInstance(url).create(services);
    }
}
