package com.example.dummypro.daily.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dummypro.databinding.AdapterDailyBinding
import com.example.dummypro.model.response.DailyItem
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class DailyAdapter : RecyclerView.Adapter<MainViewHolder>() {
    private lateinit var sharedWindUnit: String
    private lateinit var sharedTempUnit: String
    private var dailyInfoList = mutableListOf<DailyItem>()

    fun setDailyList(returnedDailyInfoList: List<DailyItem>,windUnit:String,temp:String) {
        sharedWindUnit = windUnit
        sharedTempUnit = temp
        this.dailyInfoList = returnedDailyInfoList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterDailyBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = dailyInfoList[position]
        val netDate = Date(item.dt.toLong() * 1000)
        holder.binding.day.text = netDate.toString()
        holder.binding.clouds.text = item.clouds.toString()
        holder.binding.humidity.text = item.humidity.toString()
        holder.binding.temp.text = item.temp.day.toString()
        holder.binding.status.text = item.weather?.get(0)?.description.toString()
        if(sharedWindUnit == "meter/sec"){
            if (sharedTempUnit=="imperial"){
                val solution:String = String.format("%.1f", item.windSpeed*2.2)
                holder.binding.wind.text  = solution
            }
            else {
                holder.binding.wind.text  = item.windSpeed.toString()
            }
        }
        if(sharedWindUnit == "miles/hour"){
            if (sharedTempUnit=="imperial"){
                val solution:String = String.format("%.1f", item.windSpeed*0.4)
                holder.binding.wind.text  =solution
            }
            else {
                holder.binding.wind.text  = item.windSpeed.toString()
            }
        }
        holder.binding.pressure.text = item.pressure.toString()

    }

    override fun getItemCount(): Int {
        return dailyInfoList.size
    }
}
    class MainViewHolder(val binding: AdapterDailyBinding) : RecyclerView.ViewHolder(binding.root) {
    }


