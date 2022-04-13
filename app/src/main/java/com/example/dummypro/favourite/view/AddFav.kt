package com.example.dummypro.favourite.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dummypro.databinding.FavScreenBinding
import com.example.dummypro.favourite.viewmodel.FavViewModel

import com.example.dummypro.model.fav.FavCountry

class AddFav  : AppCompatActivity() , EventClickInterface, EventDeleteIconClickInterface {
    private lateinit var binding: FavScreenBinding
    private lateinit var viewModel: FavViewModel
    private lateinit var favAdapter: FavAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FavScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(FavViewModel::class.java)
   favAdapter = FavAdapter(this,this)
        initView()
        observeEvents()
    }
    private fun initView() {

        binding.eventsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favAdapter
        }
    }

    private fun observeEvents() {
        viewModel.allLocations.observe(this) { list ->
            list?.let {
                // updates the list.
                favAdapter.updateList(it)
            }
        }
    }

    override fun onEventDeleteIconClick(favCountry: FavCountry) {
        viewModel.deleteLocation(favCountry)
        Toast.makeText(this, "Country Deleted", Toast.LENGTH_LONG).show()    }

    override fun onEventClick(favCountry: FavCountry) {
        val intent = Intent(this, FavDetails::class.java)
        intent.putExtra("location", favCountry.location)
        intent.putExtra("favLatitude", favCountry.lat)
        intent.putExtra("favLongitude", favCountry.lon)
        startActivity(intent)
        this.finish()
    }
}