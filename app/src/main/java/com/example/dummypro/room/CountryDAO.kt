package com.example.dummypro.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dummypro.model.fav.FavCountry

@Dao
interface CountryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FavCountry)
    @Delete
    suspend fun deleteLocation(location: FavCountry)

    @Query("SELECT * from favourite ORDER BY location ASC")
    fun getCountries() : LiveData<List<FavCountry>>

}