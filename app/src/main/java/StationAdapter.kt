package com.noahaung.myanmarradio

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StationAdapter(
    private val context: Context,
    private val allStations: ArrayList<Station>,
    private val onStationClick: (Station) -> Unit
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    private var filteredStations: List<Station> = allStations
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        Log.d("StationAdapter", "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.station_item, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        Log.d("StationAdapter", "onBindViewHolder called for position $position")
        val station = filteredStations[position]
        holder.bind(station)
    }

    override fun getItemCount(): Int {
        Log.d("StationAdapter", "getItemCount called: ${filteredStations.size}")
        return filteredStations.size
    }

    fun filter(query: String) {
        filteredStations = if (query.isEmpty()) {
            allStations
        } else {
            allStations.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stationThumbnail: ImageView = itemView.findViewById(R.id.station_thumbnail)
        private val stationName: TextView = itemView.findViewById(R.id.station_name)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favorite_icon)

        fun bind(station: Station) {
            Log.d("StationAdapter", "Binding station: ${station.name}")
            stationName.text = station.name
            Glide.with(itemView.context)
                .load(station.imageResId)
                .placeholder(android.R.color.darker_gray)
                .into(stationThumbnail)

            favoriteIcon.setImageResource(
                if (station.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )

            favoriteIcon.setOnClickListener {
                station.isFavorite = !station.isFavorite
                favoriteIcon.setImageResource(
                    if (station.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                )
                sharedPreferences.edit()
                    .putBoolean(station.name, station.isFavorite)
                    .apply()
                Log.d("StationAdapter", "Favorite toggled for ${station.name}: ${station.isFavorite}")
                // Notify the adapter to refresh the UI
                notifyDataSetChanged()
            }

            itemView.setOnClickListener { onStationClick(station) }
        }
    }
}