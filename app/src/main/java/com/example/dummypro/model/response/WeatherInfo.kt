package com.example.dummypro.model.response

import com.google.gson.annotations.SerializedName

data class WeatherInfo(val current: Current,
                       val timezone: String = "",
                       @SerializedName("timezone_offset")
                       val timezoneOffset: Int = 0,
                       val daily: List<DailyItem>?,
                       val lon: Double = 0.0,
                       val lat: Double = 0.0)