package com.example.dummypro.model

import androidx.lifecycle.LiveData
import com.example.dummypro.model.fav.FavCountry
import com.example.dummypro.room.CountryDAO

class FavRepository(private val countryDao: CountryDAO) {
    fun getAllLocations(): LiveData<List<FavCountry>> = countryDao.getCountries()

    suspend fun insertLocation(favCountry: FavCountry) {
        countryDao.insertLocation(favCountry)
    }

    suspend fun deleteLocation(favCountry: FavCountry) {
        countryDao.deleteLocation(favCountry)
    }
}