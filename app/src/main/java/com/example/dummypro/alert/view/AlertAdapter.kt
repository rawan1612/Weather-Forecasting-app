package com.example.dummypro.alert.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dummypro.databinding.AlertItemBinding
import com.example.dummypro.model.alert.Alert


class AlertAdapter(
    val eventDeleteIconClickInterface: EventDeleteIconClickInterface,
    val eventClickInterface: EventClickInterface
) : RecyclerView.Adapter<EventViewHolder>() {

    private val allEvents = ArrayList<Alert>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = AlertItemBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.binding.tvTitle.text = allEvents.get(position).timeView
        holder.binding.tvDescription.text = allEvents.get(position).description

        holder.binding.imgDelete.setOnClickListener {
            eventDeleteIconClickInterface.onEventDeleteIconClick(allEvents.get(position))
        }

        holder.itemView.setOnClickListener {
            eventClickInterface.onEventClick(allEvents.get(position))
        }
    }

    override fun getItemCount(): Int {
        // return list size.
        return allEvents.size
    }

    // update the list of events.
    fun updateList(newList: List<Alert>) {

        allEvents.clear()

        allEvents.addAll(newList)

        notifyDataSetChanged()
    }
}

interface EventDeleteIconClickInterface {
    fun onEventDeleteIconClick(alert: Alert)
}

interface EventClickInterface {
    fun onEventClick(alert: Alert)
}

class EventViewHolder(val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root) {}