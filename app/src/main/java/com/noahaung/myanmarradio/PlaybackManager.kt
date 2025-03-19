package com.noahaung.myanmarradio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

object PlaybackManager {
    private var player: ExoPlayer? = null
    private var currentStation: Station? = null
    private var isPlaying: Boolean = false
    private var listeners: MutableList<Player.Listener> = mutableListOf()

    fun initialize(context: Context) {
        if (player == null) {
            val dataSourceFactory = DefaultDataSource.Factory(context)
            player = ExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
                .build()
            player?.addListener(object : Player.Listener {
                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    isPlaying = playWhenReady
                    notifyListeners()
                }

                override fun onPlaybackStateChanged(state: Int) {
                    notifyListeners()
                }
            })
        }
    }

    fun getPlayer(): ExoPlayer {
        return player ?: throw IllegalStateException("Player not initialized")
    }

    fun setCurrentStation(station: Station) {
        currentStation = station
        try {
            val mediaItem = MediaItem.fromUri(station.streamUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
        } catch (e: Exception) {
            // Handle error if needed
        }
    }

    fun getCurrentStation(): Station? {
        return currentStation
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    fun addListener(listener: Player.Listener) {
        listeners.add(listener)
        player?.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        listeners.remove(listener)
        player?.removeListener(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { listener ->
            listener.onPlayWhenReadyChanged(isPlaying, 0)
            listener.onPlaybackStateChanged(player?.playbackState ?: Player.STATE_IDLE)
        }
    }

    fun release() {
        player?.release()
        player = null
        currentStation = null
        isPlaying = false
        listeners.clear()
    }
}