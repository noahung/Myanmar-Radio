package com.noahaung.myanmarradio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class StationListFragment : Fragment() {

    companion object {
        private const val ARG_SHOW_FAVORITES = "show_favorites"

        fun newInstance(showFavorites: Boolean): StationListFragment {
            val fragment = StationListFragment()
            val args = Bundle()
            args.putBoolean(ARG_SHOW_FAVORITES, showFavorites)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var adapter: StationAdapter
    private lateinit var stations: ArrayList<Station>
    var showFavorites: Boolean = false // Changed from private to public (default visibility)
    private lateinit var emptyFavoritesMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_station_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.station_list)
        emptyFavoritesMessage = view.findViewById(R.id.empty_favorites_message)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, 24, true))

        showFavorites = arguments?.getBoolean(ARG_SHOW_FAVORITES) ?: false
        stations = (activity as MainActivity).getStations()
        val filteredStations = if (showFavorites) {
            stations.filter { it.isFavorite }
        } else {
            stations
        }

        adapter = StationAdapter(requireContext(), ArrayList(filteredStations)) { station ->
            try {
                // Set the current station in PlaybackManager
                PlaybackManager.setCurrentStation(station)
                android.util.Log.d("StationListFragment", "onStationClicked: Set current station: ${station.name}")

                // Start the PlaybackService with the PLAY action
                val serviceIntent = Intent(context, PlaybackService::class.java).apply {
                    action = "PLAY"
                }
                requireContext().startService(serviceIntent)
                android.util.Log.d("StationListFragment", "onStationClicked: Started PlaybackService with PLAY action")

                // Navigate to PlayerActivity
                val index = stations.indexOf(station)
                if (index == -1) {
                    Snackbar.make(view, "Station not found!", Snackbar.LENGTH_SHORT).show()
                    return@StationAdapter
                }
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    putExtra("STATION_INDEX", index)
                    putExtra("STATION_LIST", stations)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(view, "Error opening station: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
        recyclerView.adapter = adapter

        if (showFavorites && filteredStations.isEmpty()) {
            emptyFavoritesMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyFavoritesMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        return view
    }

    fun filter(query: String) {
        val filteredStations = if (showFavorites) {
            stations.filter { it.isFavorite && it.name.contains(query, ignoreCase = true) }
        } else {
            stations.filter { it.name.contains(query, ignoreCase = true) }
        }
        adapter = StationAdapter(requireContext(), ArrayList(filteredStations)) { station ->
            try {
                // Set the current station in PlaybackManager
                PlaybackManager.setCurrentStation(station)
                android.util.Log.d("StationListFragment", "onStationClicked (filter): Set current station: ${station.name}")

                // Start the PlaybackService with the PLAY action
                val serviceIntent = Intent(context, PlaybackService::class.java).apply {
                    action = "PLAY"
                }
                requireContext().startService(serviceIntent)
                android.util.Log.d("StationListFragment", "onStationClicked (filter): Started PlaybackService with PLAY action")

                // Navigate to PlayerActivity
                val index = stations.indexOf(station)
                if (index == -1) {
                    Snackbar.make(requireView(), "Station not found!", Snackbar.LENGTH_SHORT).show()
                    return@StationAdapter
                }
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    putExtra("STATION_INDEX", index)
                    putExtra("STATION_LIST", stations)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(requireView(), "Error opening station: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
        view?.findViewById<RecyclerView>(R.id.station_list)?.adapter = adapter

        if (showFavorites && filteredStations.isEmpty()) {
            emptyFavoritesMessage.visibility = View.VISIBLE
            view?.findViewById<RecyclerView>(R.id.station_list)?.visibility = View.GONE
        } else {
            emptyFavoritesMessage.visibility = View.GONE
            view?.findViewById<RecyclerView>(R.id.station_list)?.visibility = View.VISIBLE
        }
    }

    fun refresh() {
        val filteredStations = if (showFavorites) {
            stations.filter { it.isFavorite }
        } else {
            stations
        }
        adapter = StationAdapter(requireContext(), ArrayList(filteredStations)) { station ->
            try {
                // Set the current station in PlaybackManager
                PlaybackManager.setCurrentStation(station)
                android.util.Log.d("StationListFragment", "onStationClicked (refresh): Set current station: ${station.name}")

                // Start the PlaybackService with the PLAY action
                val serviceIntent = Intent(context, PlaybackService::class.java).apply {
                    action = "PLAY"
                }
                requireContext().startService(serviceIntent)
                android.util.Log.d("StationListFragment", "onStationClicked (refresh): Started PlaybackService with PLAY action")

                // Navigate to PlayerActivity
                val index = stations.indexOf(station)
                if (index == -1) {
                    Snackbar.make(requireView(), "Station not found!", Snackbar.LENGTH_SHORT).show()
                    return@StationAdapter
                }
                val intent = Intent(context, PlayerActivity::class.java).apply {
                    putExtra("STATION_INDEX", index)
                    putExtra("STATION_LIST", stations)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(requireView(), "Error opening station: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
        view?.findViewById<RecyclerView>(R.id.station_list)?.adapter = adapter

        if (showFavorites && filteredStations.isEmpty()) {
            emptyFavoritesMessage.visibility = View.VISIBLE
            view?.findViewById<RecyclerView>(R.id.station_list)?.visibility = View.GONE
        } else {
            emptyFavoritesMessage.visibility = View.GONE
            view?.findViewById<RecyclerView>(R.id.station_list)?.visibility = View.VISIBLE
        }
    }
}