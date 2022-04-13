package com.example.dummypro.favourite.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dummypro.R
import com.example.dummypro.daily.view.RecycleView
import com.example.dummypro.home.modelview.MainViewModel
import com.example.dummypro.home.modelview.viewModelFactory
import com.example.dummypro.model.MainRepository
import com.example.dummypro.network.RetrofitService
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class FavDetails : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var desc: TextView
    private lateinit var temp: TextView
    private lateinit var cloud: TextView
    private lateinit var wind: TextView
    private lateinit var pressure: TextView
    private lateinit var humidity: TextView
    private lateinit var addressTV: TextView
    private lateinit var dateTime: TextView
    private val retrofitService = RetrofitService.getInstance()
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private lateinit var countryName :String
    private lateinit var dailyBtn: TextView
    private var sharedTempUnit: String = "standard"
    private var sharedWindUnit: String = "meter/sec"
    private var sharedLangValue: String = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fav_details)
        desc = findViewById(R.id.status)
        temp = findViewById(R.id.temp)
        cloud = findViewById(R.id.clouds)
        wind = findViewById(R.id.wind)
        pressure = findViewById(R.id.pressure)
        humidity = findViewById(R.id.humidity)
        addressTV = findViewById(R.id.address)
        dateTime = findViewById(R.id.timeDate)
        dailyBtn = findViewById(R.id.dailyBtn)
        getSharedPreferences()
        countryName = intent.getStringExtra("location").toString()
        latitude=intent.getDoubleExtra("favLatitude",latitude)
        longitude = intent.getDoubleExtra("favLongitude",longitude)
        callApiWithLatAndLong(latitude,longitude)
        dailyBtn.setOnClickListener {
            val intent = Intent(this, RecycleView::class.java).apply {
                putExtra("longitude", longitude)
                putExtra("latitude", latitude)
                putExtra("temperature",sharedTempUnit)
                putExtra("language",sharedLangValue)
                putExtra("wind_speed",sharedWindUnit)
            }
            startActivity(intent)
        }
    }
    @SuppressLint("SetTextI18n")
    private fun callApiWithLatAndLong(latitude: Double, longitude: Double) {
        viewModel =
            ViewModelProvider(
                this,
                viewModelFactory(MainRepository(retrofitService))
            ).get(
                MainViewModel::class.java
            )
        viewModel.weatherInfo.observe(this) {
            temp.text = it.current.temp.toString()
            desc.text = it.current.weather?.get(0)?.description.toString()
            cloud.text = it.current.clouds.toString()
            humidity.text = it.current.humidity.toString()
            if (sharedWindUnit == "meter/sec") {
                if (sharedTempUnit == "imperial") {
                    val solution: Double =
                        String.format("%.1f", it.current.windSpeed * 2.2).toDouble()
                    wind.text = solution.toString()
                } else {
                    wind.text = it.current.windSpeed.toString()
                }
            }
            if (sharedWindUnit == "miles/hour") {
                if (sharedTempUnit == "imperial") {
                    val solution: Double =
                        String.format("%.1f", it.current.windSpeed * 0.4).toDouble()
                    wind.text = solution.toString()
                } else {
                    wind.text = it.current.windSpeed.toString()
                }
            }
            pressure.text = it.current.pressure.toString()
            addressTV.text = countryName

            val currTime = ZonedDateTime.now(ZoneId.of(it.timezone))
            currTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy MM dd")
            val text: String = currTime.format(dateFormatter)
            val parsedDate = LocalDate.parse(text, dateFormatter)
            val timeFormatter = DateTimeFormatter.ofPattern("hh : mm a")
            val timeText: String = currTime.format(timeFormatter)
            dateTime.text = "$parsedDate $timeText"


        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.getAllInfo(latitude, longitude, sharedTempUnit, sharedLangValue)
    }
    private fun getSharedPreferences() {
        val sharedPrefFile = "savedsetting"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        sharedTempUnit = sharedPreferences.getString("temperature", "standard").toString()
        sharedWindUnit = sharedPreferences.getString("wind_speed", "meter/sec").toString()
        sharedLangValue = sharedPreferences.getString("language", "en").toString()
        println(sharedTempUnit)
        println(sharedWindUnit)
        println(sharedLangValue)


    }
}