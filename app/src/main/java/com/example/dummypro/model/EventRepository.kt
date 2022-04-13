package com.example.dummypro.model

import androidx.lifecycle.LiveData
import com.example.dummypro.model.alert.Alert
import com.example.dummypro.room.AlertDao

class EventRepository(private val alertDao: AlertDao) {

    fun getAllEvents(): LiveData<List<Alert>> = alertDao.getAllEvents()

    suspend fun insertEvent(alert: Alert) {
        alertDao.insertEvent(alert)
    }

    suspend fun deleteEvent(alert: Alert) {
        alertDao.deleteEvent(alert)
    }


}