package com.noahaung.myanmarradio

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StationAdapter(
    private val stations: List<Station>,
    private val onStationClick: (Station) -> Unit
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        Log.d("StationAdapter", "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.station_item, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        Log.d("StationAdapter", "onBindViewHolder called for position $position")
        val station = stations[position]
        holder.bind(station)
    }

    override fun getItemCount(): Int {
        Log.d("StationAdapter", "getItemCount called: ${stations.size}")
        return stations.size
    }

    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stationThumbnail: ImageView = itemView.findViewById(R.id.station_thumbnail)
        private val stationName: TextView = itemView.findViewById(R.id.station_name)

        fun bind(station: Station) {
            Log.d("StationAdapter", "Binding station: ${station.name}")
            stationName.text = station.name
            Glide.with(itemView.context)
                .load(station.imageResId)
                .placeholder(android.R.color.darker_gray) // Gray placeholder
                .into(stationThumbnail)
            itemView.setOnClickListener { onStationClick(station) }
        }
    }
}