package com.example.dummypro.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dummypro.model.fav.FavCountry

@Database(entities = [FavCountry::class], version = 1, exportSchema = false)
abstract class FavCountryDataBase : RoomDatabase(){
    abstract fun getLocationDAO(): CountryDAO
    companion object {
        @Volatile
        private var INSTANCE: FavCountryDataBase? = null

        private const val DB_NAME = "fav_database.db"

        fun getDatabase(context: Context): FavCountryDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavCountryDataBase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}