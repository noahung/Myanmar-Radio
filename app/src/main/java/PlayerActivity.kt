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

        currentStationIndex = intent.getIntExtra("STATION_INDEX", 0)
        @Suppress("DEPRECATION")
        val serializableStations = intent.getSerializableExtra("STATION_LIST")
        stations = serializableStations as? List<Station>
            ?: throw IllegalStateException("Station list not found in intent")

        val currentStation = stations[currentStationIndex]
        updateUI(currentStation)

        playPauseButton = findViewById(R.id.play_pause_button)
        val prevButton = findViewById<ImageButton>(R.id.prev_button)
        val nextButton = findViewById<ImageButton>(R.id.next_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)

        // Back button functionality
        backButton.setOnClickListener {
            finish()
        }

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
            val mediaItem = MediaItem.fromUri(currentStation.streamUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
        } catch (e: Exception) {
            Timber.e("Failed to prepare media source: ${e.message}")
            Log.e("PlayerActivity", "Failed to prepare media source: ${e.message}", e)
        }

        playPauseButton.setOnClickListener {
            if (isPlaying) {
                player.pause()
                playPauseButton.setImageResource(R.drawable.ic_play)
            } else {
                player.play()
                playPauseButton.setImageResource(R.drawable.ic_pause)
            }
            isPlaying = !isPlaying
        }

        prevButton.setOnClickListener {
            if (currentStationIndex > 0) {
                currentStationIndex--
                updateUI(stations[currentStationIndex])
                switchStation()
            }
        }

        nextButton.setOnClickListener {
            if (currentStationIndex < stations.size - 1) {
                currentStationIndex++
                updateUI(stations[currentStationIndex])
                switchStation()
            }
        }
    }

    private fun updateUI(station: Station) {
        // Set station name and description
        findViewById<TextView>(R.id.station_name).text = station.name
        findViewById<TextView>(R.id.station_subtext).text = station.name

        // Load current station image
        Glide.with(this)
            .load(station.imageUrl)
            .into(findViewById(R.id.current_station_image))

        // Update previous station image
        val prevIndex = if (currentStationIndex > 0) currentStationIndex - 1 else stations.size - 1
        Glide.with(this)
            .load(stations[prevIndex].imageUrl)
            .into(findViewById(R.id.prev_station_image))

        // Update next station image
        val nextIndex = if (currentStationIndex < stations.size - 1) currentStationIndex + 1 else 0
        Glide.with(this)
            .load(stations[nextIndex].imageUrl)
            .into(findViewById(R.id.next_station_image))
    }

    private fun switchStation() {
        val station = stations[currentStationIndex]
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