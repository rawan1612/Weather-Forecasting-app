package com.example.dummypro.favourite.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dummypro.databinding.FavItemBinding
import com.example.dummypro.model.fav.FavCountry

class FavAdapter(
    val eventDeleteIconClickInterface: EventDeleteIconClickInterface,
    val eventClickInterface: EventClickInterface
) : RecyclerView.Adapter<EventViewHolder>() {


    private val allLocation = ArrayList<FavCountry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = FavItemBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        // sets data to item of recycler view.
        holder.binding.tvCountryName.text= allLocation[position].location
        holder.binding.imgDelete.setOnClickListener {
            eventDeleteIconClickInterface.onEventDeleteIconClick(allLocation.get(position))
        }

        holder.itemView.setOnClickListener {
            eventClickInterface.onEventClick(allLocation.get(position))
        }
    }

    override fun getItemCount(): Int {
        // return list size.
        return allLocation.size
    }

    // update the list of events.
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<FavCountry>) {
        allLocation.clear()
        allLocation.addAll(newList)
        notifyDataSetChanged()
    }
}

interface EventDeleteIconClickInterface {

    fun onEventDeleteIconClick(favCountry: FavCountry)
}

interface EventClickInterface {
    fun onEventClick(favCountry: FavCountry)
}

class EventViewHolder(val binding: FavItemBinding) : RecyclerView.ViewHolder(binding.root) {}