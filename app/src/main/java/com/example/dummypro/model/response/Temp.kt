package com.example.dummypro.model.response

data class Temp(val min: Double = 0.0,
                val max: Double = 0.0,
                val eve: Double = 0.0,
                val night: Double = 0.0,
                val day: Double = 0.0,
                val morn: Double = 0.0)