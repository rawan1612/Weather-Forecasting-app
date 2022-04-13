package com.example.dummypro.model.response

import com.google.gson.annotations.SerializedName

data class DailyItem(val moonset: Int = 0,
                     val sunrise: Int = 0,
                     val temp: Temp,
                     @SerializedName("moon_phase")
                     val moonPhase: Double = 0.0,
                     val uvi: Double = 0.0,
                     val moonrise: Int = 0,
                     val pressure: Int = 0,
                     val clouds: Int = 0,
                     @SerializedName("feels_like")
                     val feelsLike: FeelsLike,
                     @SerializedName("wind_gust")
                     val windGust: Double = 0.0,
                     val dt: Int = 0,
                     val pop: Double = 0.0,
                     @SerializedName("wind_deg")
                     val windDeg: Int = 0,
                     @SerializedName("dew_point")
                     val dewPoint: Double = 0.0,
                     val sunset: Int = 0,
                     val weather: List<WeatherItem>?,
                     val humidity: Int = 0,
                     @SerializedName("wind_speed")
                     val windSpeed: Double = 0.0)