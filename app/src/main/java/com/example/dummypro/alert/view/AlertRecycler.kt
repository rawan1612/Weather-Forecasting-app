package com.example.dummypro.alert.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dummypro.alert.viewmodel.AlertViewModel
import com.example.dummypro.databinding.RecycleAlertBinding
import com.example.dummypro.model.alert.Alert

class AlertRecycler : AppCompatActivity(), EventClickInterface, EventDeleteIconClickInterface {
    private lateinit var binding: RecycleAlertBinding
    private lateinit var viewModel: AlertViewModel
    private lateinit var alertAdapter: AlertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecycleAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(AlertViewModel::class.java)

        alertAdapter = AlertAdapter(this, this)

        initView()
        observeEvents()
    }

    private fun initView() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AlertActivity::class.java)
            startActivity(intent)
        }

        binding.eventsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = alertAdapter
        }
    }

    private fun observeEvents() {
        viewModel.allEvents.observe(this, Observer { list ->
            list?.let {
                // updates the list.
                alertAdapter.updateList(it)
            }
        })
    }



    override fun onEventDeleteIconClick(alert: Alert) {
        viewModel.deleteEvent(alert)
        Toast.makeText(this, "Alert Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onEventClick(alert: Alert) {
        Toast.makeText(this,alert.description+" "+alert.timeView+" is Active for "+alert.duration+" days",Toast.LENGTH_LONG).show()
    }

}