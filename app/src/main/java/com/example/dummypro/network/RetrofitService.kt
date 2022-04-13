package com.example.dummypro.network

import com.example.dummypro.model.response.WeatherInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("data/2.5/onecall?")
    suspend fun getAllInfo(@Query("lat")lat: Double, @Query("lon")lon: Double, @Query("exclude")exclude: String, @Query("appid")apiKey:String ,@Query("units")units:String,@Query("lang")lang:String): Response<WeatherInfo>
    companion object{
        var retrofitService : RetrofitService? = null
        fun getInstance(): RetrofitService {
            if(retrofitService == null){
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create()
            }
            return retrofitService!!
        }

    }
}