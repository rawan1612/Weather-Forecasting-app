package com.example.dummypro.model.alert

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertTable")
data class Alert(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "alert_id")
    val id: Int?,
    @ColumnInfo(name = "alert_description")
    val description: String?,
    @ColumnInfo(name = "time")
    val time: Long?,
    @ColumnInfo(name = "timeToView")
    val timeView: String?,
    @ColumnInfo(name = "start_date")
    val startDate: Long?,
    @ColumnInfo(name = "duration")
    val duration: Int?
)