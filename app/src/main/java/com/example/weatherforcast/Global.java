package com.example.weatherforcast;

public class Global {
    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = "b272f357dce93b64982cc431978d6f01";
    public static final String URL_ICON = "https://openweathermap.org/img/wn/";
    public static final String ICON_FORMAT = "@4x.png";
    public static final String ICON_FORMAT_HOURS = "@2x.png";
    public static final String CELSIUS = "\u2103";
    public static final String VN = "vi";

    public static String convertKtoC(float kelvin) {
        return Math.round(kelvin - 272.15) + CELSIUS;
    }
    public static String getImageDescription(String imgId) {
        return URL_ICON + imgId + ICON_FORMAT;
    }
    public static String getImageDescriptionHours(String imgId) {
        return URL_ICON + imgId+ ICON_FORMAT_HOURS;
    }
}
