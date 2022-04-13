package com.example.dummypro.favourite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dummypro.model.FavRepository
import com.example.dummypro.model.fav.FavCountry
import com.example.dummypro.room.FavCountryDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavViewModel(application: Application) : AndroidViewModel(application){
    val allLocations: LiveData<List<FavCountry>>
    val repository: FavRepository
    init {
        val dao = FavCountryDataBase.getDatabase(application).getLocationDAO()
        repository = FavRepository(dao)
        allLocations = repository.getAllLocations()
    }
    fun insertLocation(favCountry : FavCountry){
        viewModelScope.launch (Dispatchers.IO){ repository.insertLocation(favCountry)  }
    }
    fun deleteLocation(favCountry: FavCountry){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteLocation(favCountry)
        }
    }
}