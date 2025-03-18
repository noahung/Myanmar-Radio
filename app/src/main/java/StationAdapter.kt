package com.noahaung.myanmarradio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class StationAdapter(
    private val stations: List<Station>,
    private val onStationClick: (Station) -> Unit
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.station_item, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = stations[position]
        holder.bind(station)
    }

    override fun getItemCount(): Int = stations.size

    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.station_thumbnail)
        private val name: TextView = itemView.findViewById(R.id.station_name)

        fun bind(station: Station) {
            name.text = station.name
            Glide.with(thumbnail.context)
                .load(station.imageResId)
                .thumbnail(0.25f) // Load a smaller thumbnail first for smoother scrolling
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized images
                .override(60, 60) // Resize to match the ImageView size (60dp)
                .into(thumbnail)

            itemView.setOnClickListener { onStationClick(station) }
        }
    }
}