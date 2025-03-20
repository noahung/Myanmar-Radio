package com.noahaung.myanmarradio

import android.content.Context
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player

object PlaybackManager {
    private lateinit var player: ExoPlayer
    private var currentStation: Station? = null
    private var stations: ArrayList<Station>? = null
    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
        player = ExoPlayer.Builder(context).build()
    }

    fun getPlayer(): ExoPlayer = player

    fun setCurrentStation(station: Station) {
        currentStation = station
        // Start the PlaybackService when a station is set
        val intent = Intent(context, PlaybackService::class.java).apply {
            action = "PLAY"
        }
        context.startService(intent)
    }

    fun getCurrentStation(): Station? = currentStation

    fun setStations(stations: ArrayList<Station>) {
        this.stations = stations
    }

    fun getStations(): ArrayList<Station> = stations ?: arrayListOf()

    fun isPlaying(): Boolean = player.isPlaying

    fun addListener(listener: Player.Listener) {
        player.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        player.removeListener(listener)
    }

    fun release() {
        player.release()
    }
}