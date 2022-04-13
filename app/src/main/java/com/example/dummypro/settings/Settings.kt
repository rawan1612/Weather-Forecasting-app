package com.example.dummypro.settings

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dummypro.R
import com.example.dummypro.home.view.MainActivity
import kotlin.math.log
 lateinit var language : String
lateinit var temp : String
lateinit var wind : String
lateinit var saveBtn : Button
lateinit var spinnerTemp: Spinner
lateinit var spinnerWind: Spinner
lateinit var spinnerLang: Spinner
class Settings  : AppCompatActivity(){
    private val sharedPrefFile = "savedsetting"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_screen)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences( sharedPrefFile,Context.MODE_PRIVATE)
        saveBtn= findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener {
            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("temperature", temp)
            editor.putString("wind_speed", wind)
            editor.putString("language", language)
            editor.apply()
            editor.commit()
            Toast.makeText(this,"Settings saved successfully",Toast.LENGTH_LONG).show()
            finish()
        }
         spinnerTemp = findViewById(R.id.temp_spinner)
         spinnerWind = findViewById(R.id.wind_spinner)
         spinnerLang = findViewById(R.id.lang_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.temp_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTemp.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.wind_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerWind.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.lang_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLang.adapter = adapter
        }
        spinnerTemp.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 != null) {
                      if(p0.getItemAtPosition(p2).toString()=="Kelvin")
                      {
                          temp="standard"
                      }else if(p0.getItemAtPosition(p2).toString()=="Celsius"){
                          temp="metric"
                      }else if(p0.getItemAtPosition(p2).toString()=="Fahrenheit"){
                          temp="imperial"
                      }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        spinnerLang.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 != null) {
                     if(p0.getItemAtPosition(p2).toString()=="Arabic")
                     {
                         language ="ar"
                     }
                    else{
                        language="en"
                     }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        spinnerWind.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 != null) {
                    wind =  p0.getItemAtPosition(p2).toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }


}