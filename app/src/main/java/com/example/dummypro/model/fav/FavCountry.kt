package com.example.dummypro.model.fav

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")

data class FavCountry(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "location")
    val location:String,
    @ColumnInfo(name = "lon")
    val lon: Double = 0.0,
    @ColumnInfo(name = "lat")
    val lat: Double = 0.0
    )
