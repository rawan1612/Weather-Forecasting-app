package com.example.dummypro.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dummypro.R
import com.example.dummypro.home.view.MainActivity
import com.example.dummypro.model.MainRepository
import com.example.dummypro.network.RetrofitService

class AlarmWorker (
    appContext : Context,
    params: WorkerParameters
) : CoroutineWorker(appContext,params)  {
    private val retrofitService: RetrofitService = RetrofitService.getInstance()
    private val repository: MainRepository = MainRepository(retrofitService)
    private val CHANNEL_ID = "01"
    private val notificationId = 101

    override suspend fun doWork(): Result {
        val response = repository.getAllInfo(MainActivity.latitude,MainActivity.longitude,MainActivity.sharedTempUnit,MainActivity.sharedLangValue)
        if(response != null){
            if(response.isSuccessful){
                createNotificationChannel()
                sendNotification(response.body()?.current?.weather?.get(0)?.description.toString())
                Log.i("TAG", "doWork: "+ response.body()?.current?.weather?.get(0)?.description.toString())
            }
            else {
                Log.i("TAG", response.raw().toString()+"Fail")
            }
        }
        return  Result.success()
        return Result.success()
    }
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name = "Alarm"
            val descriptionText = "alert with notification description"
            val important = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,important).apply {
                description = descriptionText
            }
            val notificationManager : NotificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun sendNotification(weatherDesc: String){
        val builder = NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_alarm_24)
            .setContentTitle("Weather Alert")
            .setContentText(weatherDesc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationId,builder.build())
        }
    }

}