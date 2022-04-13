package com.example.dummypro.home.modelview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dummypro.model.MainRepository
import com.example.dummypro.model.response.DailyItem
import com.example.dummypro.model.response.WeatherInfo
import kotlinx.coroutines.*


    class MainViewModel constructor(private val repository: MainRepository)  : ViewModel() {
        val weatherInfo = MutableLiveData<WeatherInfo>()
        val dailyList = MutableLiveData<List<DailyItem>?>()
        val errorMessage = MutableLiveData<String>()
        var job: Job? = null
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }
        fun getAllInfo(lat: Double,long: Double,unit:String,lang:String) {
            job = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
                while (isActive) {
                    val response = repository.getAllInfo(lat, long,unit,lang)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            weatherInfo.postValue(response.body())
                            dailyList.postValue(response.body()?.daily)
                        } else {
                            onError("Error : ${response.message()} ")
                        }
                    }
                        delay(600000)
                    println("updated")
                }
            }
        }

        private fun onError(message: String) {
            errorMessage.value = message
        }

        override fun onCleared() {
            super.onCleared()
            job?.cancel()
        }
    }
