package com.noahaung.myanmarradio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class StationAdapter(
    private val stations: List<Station>,
    private val onClick: (Station) -> Unit
) : RecyclerView.Adapter<StationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.station_name)
        val thumbnail: ImageView = view.findViewById(R.id.station_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.station_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = stations[position]
        holder.nameText.text = station.name
        Picasso.get().load(station.imageUrl).into(holder.thumbnail)
        holder.itemView.setOnClickListener { onClick(station) }
    }

    override fun getItemCount() = stations.size
}