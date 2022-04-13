package com.example.dummypro.daily.view

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dummypro.model.MainRepository
import com.example.dummypro.R
import com.example.dummypro.databinding.DailyScreenBinding
import com.example.dummypro.home.modelview.MainViewModel
import com.example.dummypro.home.modelview.viewModelFactory
import com.example.dummypro.home.view.MainActivity
import com.example.dummypro.network.RetrofitService

class RecycleView : MainActivity() {
    private val TAG = "DailyActivity"
    private lateinit var binding: DailyScreenBinding
    lateinit var viewModel: MainViewModel
    private var sharedTempUnit : String ="standard"
    private var sharedWindUnit: String = "meter/sec"
    private var sharedLangValue: String = "ar"
    private val retrofitService = RetrofitService.getInstance()
    val adapter = DailyAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daily_screen)
        val longitude = intent.getDoubleExtra("longitude",0.0)
        val latitude = intent.getDoubleExtra("latitude",0.0)
        sharedTempUnit = intent.getStringExtra("temperature").toString()
        sharedLangValue = intent.getStringExtra("language").toString()
        sharedWindUnit = intent.getStringExtra("wind_speed").toString()



        binding = DailyScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, viewModelFactory(MainRepository(retrofitService))).get(MainViewModel::class.java)

        binding.recyclerview.adapter = adapter
        viewModel.dailyList.observe(this, Observer {
            it?.let { it1 -> adapter.setDailyList(it1,sharedWindUnit,sharedTempUnit) }


        }
        )
        viewModel.errorMessage.observe(this, Observer {
        })
        viewModel.getAllInfo(latitude,longitude,sharedTempUnit,sharedLangValue)
    }

}