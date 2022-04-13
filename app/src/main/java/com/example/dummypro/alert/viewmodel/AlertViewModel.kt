package com.example.dummypro.alert.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dummypro.model.EventRepository
import com.example.dummypro.model.alert.Alert
import com.example.dummypro.room.EventDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertViewModel(
    application: Application
) : AndroidViewModel(application)
{

    val allEvents: LiveData<List<Alert>>
    val repository: EventRepository

    // initialize dao, repository and all events
    init {
        val dao = EventDatabase.getDatabase(application).getEventsDao()
        repository = EventRepository(dao)
        allEvents = repository.getAllEvents()
    }

    fun insertEvent(alert: Alert) =
        viewModelScope.launch(Dispatchers.IO) { repository.insertEvent(alert) }

    fun deleteEvent(alert: Alert) =
        viewModelScope.launch(Dispatchers.IO) { repository.deleteEvent(alert) }

}