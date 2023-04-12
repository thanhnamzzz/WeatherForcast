package com.example.weatherforcast;

import com.example.weatherforcast.current_City.CurrentWeather;
import com.example.weatherforcast.hours_Weather.HoursWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherServices {
    @GET("weather?")
//    Call<CurrentWeather> getWeatherByCityName(@Query("q") String cityName, @Query("appid") String apiKey);
    Call<CurrentWeather> getWeatherByCityName(@Query("q") String cityName, @Query("lang") String lang, @Query("appid") String apiKey);
    @GET("weather?")
//    Call<CurrentWeather> getWeatherByLocation(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String apiKey);
    Call<CurrentWeather> getWeatherByLocation(@Query("lat") String lat, @Query("lon") String lon,@Query("lang") String lang, @Query("appid") String apiKey);
    @GET("forecast?")
    Call<HoursWeather> getWeatherHoursByCityName(@Query("q")String cityName, @Query("appid") String apiKey);
    @GET("forecast?")
    Call<HoursWeather> getWeatherHoursByLocation(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String apiKey);
}
