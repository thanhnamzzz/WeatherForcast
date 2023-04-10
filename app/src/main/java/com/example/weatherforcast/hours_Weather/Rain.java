
package com.example.weatherforcast.hours_Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rain {

    @SerializedName("3h")
    @Expose
    private Float _3h;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Rain() {
    }

    /**
     * 
     * @param _3h
     */
    public Rain(Float _3h) {
        super();
        this._3h = _3h;
    }

    public Float get3h() {
        return _3h;
    }

    public void set3h(Float _3h) {
        this._3h = _3h;
    }

}
