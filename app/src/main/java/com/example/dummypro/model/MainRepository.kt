package com.example.dummypro.model

import com.example.dummypro.network.RetrofitService


class MainRepository constructor(private val retrofitService: RetrofitService) {
    private val apiKey : String = "371db258e595cbc8737ba7ba51c97072"
    private val exclude : String = "hourly,minutely"
    suspend fun getAllInfo(lat: Double,lon: Double,units:String,lang:String) = retrofitService.getAllInfo(lat,lon,exclude,apiKey,units,lang)
    }
