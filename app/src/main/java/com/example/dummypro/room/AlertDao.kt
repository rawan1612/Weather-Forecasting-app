package com.example.dummypro.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dummypro.model.alert.Alert

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(alertItem :Alert)

    @Delete
    suspend fun deleteEvent(alertItem :Alert)



    @Query("Select * from alertTable order by alert_id ASC")
    fun getAllEvents(): LiveData<List<Alert>>


}