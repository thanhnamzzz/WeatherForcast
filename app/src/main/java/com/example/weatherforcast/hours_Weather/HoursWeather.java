
package com.example.weatherforcast.hours_Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HoursWeather {

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private Integer message;
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    ///trong api nó k trả trường listHours nào nên thằng listHours sẽ bị null
    @SerializedName("list")
    @Expose
    private List<ListHours> listHours;
    @SerializedName("city")
    @Expose
    private City city;

//    public HoursWeather() {
//    }

//    public HoursWeather(String cod, Integer message, Integer cnt, List<ListHours> listHours, City city) {
//        super();
//        this.cod = cod;
//        this.message = message;
//        this.cnt = cnt;
//        this.listHours = listHours;
//        this.city = city;
//    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public List<ListHours> getListHours() {
        return listHours;
    }

    public void setList(List<ListHours> listHours) {
        this.listHours = listHours;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

}
