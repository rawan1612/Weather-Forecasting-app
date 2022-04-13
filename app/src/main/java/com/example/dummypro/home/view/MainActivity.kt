package com.example.dummypro.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dummypro.R
import com.example.dummypro.alert.view.AlertRecycler
import com.example.dummypro.daily.view.RecycleView
import com.example.dummypro.favourite.view.AddFav
import com.example.dummypro.favourite.viewmodel.FavViewModel
import com.example.dummypro.home.modelview.MainViewModel
import com.example.dummypro.home.modelview.viewModelFactory
import com.example.dummypro.model.MainRepository
import com.example.dummypro.model.fav.FavCountry
import com.example.dummypro.network.RetrofitService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToLong


open class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    lateinit var favViewModel: FavViewModel
    private val TAG = "MainActivity"
    private lateinit var button: Button
    private lateinit var cityName: EditText
    private lateinit var desc: TextView
    private lateinit var temp: TextView
    private lateinit var cloud: TextView
    private lateinit var wind: TextView
    private lateinit var pressure: TextView
    private lateinit var humidity: TextView
    private lateinit var addressTV: TextView
    private lateinit var dateTime: TextView
    private val retrofitService = RetrofitService.getInstance()
    private lateinit var address: Address
    private lateinit var addresses: MutableList<Address>
    private lateinit var gc: Geocoder
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var countryName :String
    lateinit var dailyBtn: TextView
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var addToFav : FloatingActionButton
    lateinit var weatherInfo: SharedPreferences
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
   lateinit var navView: NavigationView



    var offline : Boolean = false;
    companion object {
         const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
         var longitude: Double = 0.0
         var latitude: Double = 0.0
         var sharedTempUnit: String = "standard"
         var sharedWindUnit: String = "meter/sec"
         var sharedLangValue: String = "en"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        getSharedPreferences()
       weatherInfo = this.getSharedPreferences("weatherInfo", MODE_PRIVATE)
        if (sharedLangValue == "ar") {
            val config = resources.configuration
            val lang = "ar" // your language code
            val locale = Locale(lang)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                config.setLocale(locale)
            else
                config.locale = locale

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)

            this.setContentView(R.layout.activity_main)
        } else {
            setContentView(R.layout.activity_main)
        }
        initView()
        // fav_screen
        favViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(FavViewModel::class.java)

        addToFav.setOnClickListener {
            favViewModel.insertLocation(FavCountry(countryName, longitude, latitude))
            Toast.makeText(this,"Country Added to favorite",Toast.LENGTH_LONG).show()
        }


        // navigation drawer

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (checkForInternet(this)) {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
            dailyBtn.isInvisible = true
            temp.text = weatherInfo.getString("temp","0.0")
            addressTV.text = weatherInfo.getString("country","")
            wind.text = weatherInfo.getString("wind","0.0")
            cloud.text = weatherInfo.getString("clouds","0.0")
            humidity.text = weatherInfo.getString("humidity","0.0")
            pressure.text = weatherInfo.getString("pressure","0.0")
            desc.text = weatherInfo.getString("desc","")
            dateTime.text = weatherInfo.getString("lastUpdate","")
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, com.example.dummypro.settings.Settings::class.java)
                    startActivity(intent)

                }
                R.id.nav_fav -> {
                    val intent = Intent(this, AddFav::class.java)
                    startActivity(intent)

                }
                R.id.nav_alert -> {
                    val intent = Intent(this, AlertRecycler::class.java)
                    startActivity(intent)
                }
            }
            true
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        dailyBtn.setOnClickListener {
            val intent = Intent(this, RecycleView::class.java).apply {
                putExtra("longitude", longitude)
                putExtra("latitude", latitude)
                putExtra("temperature", sharedTempUnit)
                putExtra("language", sharedLangValue)
                putExtra("wind_speed", sharedWindUnit)
            }
            startActivity(intent)
        }
        button.isEnabled = false

        getCurrentLocation();
        button.setOnClickListener {
            if (checkForInternet(this)) {
                var city = cityName.text.toString()
                gc = Geocoder(this, Locale.getDefault())
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        addresses = gc.getFromLocationName(city, 2)

                        if (addresses != null && addresses.size > 0) {
                            address = addresses[0]
                            longitude = address.longitude
                            latitude = address.latitude
                            countryName = if (address.locality != null) {
                                address.locality

                            } else {
                                address.countryName
                            }
                            ((latitude * 100.0).roundToLong() / 100.0).also { latitude = it }
                            longitude = (longitude * 100.0).roundToLong() / 100.0
                            callApiWithLatAndLong(latitude, longitude)

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Please enter city name again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }catch (e : IOException){
                        println(e)
                }
                }
            } else {
                Toast.makeText(this, "please check your connection", Toast.LENGTH_SHORT).show()
            }

        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocatedEnable()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "no location found", Toast.LENGTH_SHORT).show()
                    } else {
                        offline = true;
                        longitude = location.longitude
                        latitude = location.latitude
                        ((latitude * 100.0).roundToLong() / 100.0).also { latitude = it }
                        longitude =  (longitude * 100.0).roundToLong() / 100.0
                        callApiWithLatAndLong(latitude, longitude)

                        gc = Geocoder(this, Locale.getDefault())
                        addresses = gc.getFromLocation(location.latitude, location.longitude, 2)
                        address = addresses[0]
                        countryName = address.locality

                    }
                    button.isEnabled = true

                }

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocatedEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                button.isEnabled = true
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
    }

    fun callApiWithLatAndLong(latitude: Double, longitude: Double) {
        viewModel =
            ViewModelProvider(
                this,
                viewModelFactory(MainRepository(retrofitService))
            ).get(
                MainViewModel::class.java
            )
        viewModel.weatherInfo.observe(this, Observer {
            temp.text = it.current.temp.toString()
            desc.text = it.current.weather?.get(0)?.description.toString()
            cloud.text = it.current.clouds.toString()
            humidity.text = it.current.humidity.toString()
            if(sharedWindUnit == "meter/sec"){
                if (sharedTempUnit=="imperial"){
                    val solution:String = String.format("%.1f", it.current.windSpeed*2.2)
                    wind.text = solution
                }
                else {
                    wind.text =it.current.windSpeed.toString()
                }
            }
            if(sharedWindUnit == "miles/hour"){
                if (sharedTempUnit=="imperial"){
                    val solution:String = String.format("%.1f", it.current.windSpeed*0.4)
                    wind.text = solution
                }
                else {
                    wind.text = it.current.windSpeed.toString()
                }
            }
            pressure.text = it.current.pressure.toString()
            addressTV.text = address.locality

            val currTime = ZonedDateTime.now(ZoneId.of(it.timezone))
            currTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy MM dd")
            val text: String = currTime.format(dateFormatter)
            val parsedDate = LocalDate.parse(text, dateFormatter)
            val timeFormatter = DateTimeFormatter.ofPattern("hh : mm a")
            val timeText: String = currTime.format(timeFormatter)
            dateTime.text = "$parsedDate $timeText"
            if(offline) {
                val editor:SharedPreferences.Editor =  weatherInfo.edit()
                editor.putFloat("latitude", latitude.toFloat())
                editor.putFloat("longitude", longitude.toFloat())
                editor.putString("desc",desc.text.toString())
                editor.putString("country",addressTV.text.toString())
                editor.putString("wind",wind.text.toString())
                editor.putString("pressure",pressure.text.toString())
                editor.putString("clouds",cloud.text.toString())
                editor.putString("humidity",humidity.text.toString())
                editor.putString("temp", temp.text.toString())
                editor.putString("lastUpdate",dateTime.text.toString())
                editor.apply()
                editor.commit()
                offline=false
            }

        })
        viewModel.errorMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.getAllInfo(latitude, longitude, sharedTempUnit, sharedLangValue)

    }

    private fun getSharedPreferences() {
        val sharedPrefFile = "savedsetting"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        sharedTempUnit = sharedPreferences.getString("temperature", "standard").toString()
        sharedWindUnit = sharedPreferences.getString("wind_speed", "meter/sec").toString()
        sharedLangValue = sharedPreferences.getString("language", "en").toString()


    }
    override fun onBackPressed() {
        var intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    fun initView(){
        addToFav = findViewById(R.id.fab_add)
        desc = findViewById(R.id.status)
        temp = findViewById(R.id.temp)
        cityName = findViewById(R.id.cityName)
        button = findViewById(R.id.button)
        cloud = findViewById(R.id.clouds)
        wind = findViewById(R.id.wind)
        pressure = findViewById(R.id.pressure)
        humidity = findViewById(R.id.humidity)
        addressTV = findViewById(R.id.address)
        dateTime = findViewById(R.id.timeDate)
        dailyBtn = findViewById(R.id.dailyBtn)
         toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView= findViewById(R.id.nav_view)


    }

}
