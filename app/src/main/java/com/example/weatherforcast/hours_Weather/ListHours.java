
package com.example.weatherforcast.hours_Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListHours {

    @SerializedName("dt")
    @Expose
    private Integer dt;
    @SerializedName("main")
    @Expose
    private Main main;
    @SerializedName("weather")
    @Expose
    private java.util.List<Weather> weather;
    @SerializedName("clouds")
    @Expose
    private Clouds clouds;
    @SerializedName("wind")
    @Expose
    private Wind wind;
    @SerializedName("visibility")
    @Expose
    private Integer visibility;
    @SerializedName("pop")
    @Expose
    private Float pop;
    @SerializedName("sys")
    @Expose
    private Sys sys;
    @SerializedName("dt_txt")
    @Expose
    private String dtTxt;
    @SerializedName("rain")
    @Expose
    private Rain rain;

    public ListHours() {
    }

    public ListHours(Integer dt, Main main, java.util.List<Weather> weather, Clouds clouds, Wind wind, Integer visibility, Float pop, Sys sys, String dtTxt, Rain rain) {
        super();
        this.dt = dt;
        this.main = main;
        this.weather = weather;
        this.clouds = clouds;
        this.wind = wind;
        this.visibility = visibility;
        this.pop = pop;
        this.sys = sys;
        this.dtTxt = dtTxt;
        this.rain = rain;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(java.util.List<Weather> weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Float getPop() {
        return pop;
    }

    public void setPop(Float pop) {
        this.pop = pop;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public void setDtTxt(String dtTxt) {
        this.dtTxt = dtTxt;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

}
