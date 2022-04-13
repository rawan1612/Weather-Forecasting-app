package com.example.dummypro.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dummypro.model.alert.Alert

@Database(entities = [Alert::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun getEventsDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        private const val DB_NAME = "event_database.db"

        fun getDatabase(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}