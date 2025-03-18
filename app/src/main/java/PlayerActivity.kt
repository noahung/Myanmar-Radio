package com.noahaung.myanmarradio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import android.widget.ImageView
import timber.log.Timber

class PlayerActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private var isPlaying = false
    private lateinit var playPauseButton: ImageButton
    private lateinit var stations: List<Station>
    private var currentStationIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)



        // Get the station list and current index from the intent
        currentStationIndex = intent.getIntExtra("STATION_INDEX", 0)
        @Suppress("DEPRECATION")
        val serializableStations = intent.getSerializableExtra("STATION_LIST")
        stations = serializableStations as? List<Station>
            ?: throw IllegalStateException("Station list not found in intent")

        val currentStation = stations[currentStationIndex]
        val name = currentStation.name
        val url = currentStation.streamUrl
        val imageUrl = currentStation.imageUrl

        findViewById<TextView>(R.id.station_name).text = name
        playPauseButton = findViewById<ImageButton>(R.id.play_pause_button)
        val prevButton = findViewById<ImageButton>(R.id.prev_button)
        val nextButton = findViewById<ImageButton>(R.id.next_button)
        val background = findViewById<ImageView>(R.id.station_background)

        Glide.with(background.context)
            .load(imageUrl)
            .into(background)

        // Initialize ExoPlayer without custom User-Agent
        val dataSourceFactory = DefaultDataSource.Factory(this)
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Timber.e("Playback error: ${error.message}")
                Log.e("PlayerActivity", "Playback error: ${error.message}", error)
            }
        })

        try {
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
        } catch (e: Exception) {
            Timber.e("Failed to prepare media source: ${e.message}")
            Log.e("PlayerActivity", "Failed to prepare media source: ${e.message}", e)
        }

        // Play/Pause button
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                player.pause()
                playPauseButton.setImageResource(R.drawable.ic_play_arrow)
            } else {
                player.play()
                playPauseButton.setImageResource(R.drawable.ic_pause)
            }
            isPlaying = !isPlaying
        }

        // Previous button
        prevButton.setOnClickListener {
            if (currentStationIndex > 0) {
                currentStationIndex--
                switchStation()
            }
        }

        // Next button
        nextButton.setOnClickListener {
            if (currentStationIndex < stations.size - 1) {
                currentStationIndex++
                switchStation()
            }
        }
    }

    private fun switchStation() {
        val station = stations[currentStationIndex]
        findViewById<TextView>(R.id.station_name).text = station.name
        Glide.with(this)
            .load(station.imageUrl)
            .into(findViewById<ImageView>(R.id.station_background))

        // Update the media source for the new station
        try {
            val mediaItem = MediaItem.fromUri(station.streamUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            if (isPlaying) {
                player.play()
            }
        } catch (e: Exception) {
            Timber.e("Failed to switch station: ${e.message}")
            Log.e("PlayerActivity", "Failed to switch station: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}