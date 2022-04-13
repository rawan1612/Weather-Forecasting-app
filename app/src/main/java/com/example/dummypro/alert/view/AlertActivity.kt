package com.example.dummypro.alert.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.dummypro.R
import com.example.dummypro.alert.AlarmWorker
import com.example.dummypro.alert.viewmodel.AlertViewModel
import com.example.dummypro.model.alert.Alert
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class AlertActivity : AppCompatActivity() {
    lateinit var formate : SimpleDateFormat
    lateinit var timeFormat : SimpleDateFormat
    var currentTime : Long =0
    var alertTime : Long = 0
    var timeInMilliseconds: Long =0
    var dateInMillisecond: Long = convertDateToMillis(LocalDate.now().toString())
    lateinit var viewModel: AlertViewModel
    lateinit var desc : EditText
    lateinit var durationTv : EditText
    var timeToView : String? =""
    var alertDuration:Int? =0
    private var alertDescription :String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alert_screen)
        desc = findViewById(R.id.alertDesc)
        durationTv = findViewById(R.id.duration)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(AlertViewModel::class.java)
        val  setAlertBtn = findViewById<Button>(R.id.set_alert)
        formate = SimpleDateFormat("dd/M/yyyy",
            Locale.forLanguageTag("en"))
        timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        setAlertBtn.setOnClickListener {
            if(desc.text.toString().isEmpty()||durationTv.text.toString().isEmpty()){
                Toast.makeText(this, "please complete the above fields", Toast.LENGTH_SHORT).show()

            }else {
                alertDescription = desc.text.toString()
                alertDuration = durationTv.text.toString().toInt()
                currentTime = System.currentTimeMillis()
                alertTime = timeInMilliseconds - currentTime
                viewModel.insertEvent(
                    Alert(
                        null,
                        alertDescription,
                        timeInMilliseconds,
                        timeToView,
                        dateInMillisecond,
                        alertDuration
                    )
                )
                setAlarm(alertTime)
                Toast.makeText(this, "Alarm Added", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        onClickTime()
        onClickDate()
    }
    private fun onClickTime() {
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY,hour)
            selectedTime.set(Calendar.MINUTE,minute)
            timeToView = timeFormat.format(selectedTime.time)
            convertTimeToLong(timeFormat.format(selectedTime.time))

        }
    }
    private fun onClickDate(){
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        datePicker.setOnDateChangedListener { datePicker, year, mon, day ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR,year)
            selectedDate.set(Calendar.MONTH,mon)
            selectedDate.set(Calendar.DAY_OF_MONTH,day)
            val date = formate.format(selectedDate.time)
            dateInMillisecond = convertDateToMillis(date)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimeToLong(time:String):Long{
        val localDate = LocalDate.now()
        val timeAndDate="${localDate.dayOfMonth}-"+ localDate.getMonthValue() + "-" + "${localDate.getYear()}" + " " +time
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        var mDate: Date? = null
        try {
            mDate = sdf.parse(timeAndDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        timeInMilliseconds = mDate!!.time
        return timeInMilliseconds
    }

    fun convertDateToMillis(dateStr: String?): Long {
        val sdf = SimpleDateFormat("dd/M/yyyy", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        try {
            val date = sdf.parse(dateStr)
            calendar.time = date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return calendar.timeInMillis
    }
    fun setAlarm(alarm : Long){
        val alarmRequest = OneTimeWorkRequest
            .Builder(AlarmWorker::class.java)
            .setInitialDelay(alarm, TimeUnit.MILLISECONDS).build()
        WorkManager.getInstance(this).enqueue(alarmRequest)
    }

}