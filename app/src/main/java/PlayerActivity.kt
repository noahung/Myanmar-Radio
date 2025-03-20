package com.noahaung.myanmarradio

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class PlayerActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private var isPlaying = false
    private lateinit var playPauseButton: ImageButton
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var bufferingText: TextView
    private lateinit var stations: ArrayList<Station>
    private var currentStationIndex: Int = 0
    private lateinit var soundWaveView: SoundWaveView
    private lateinit var favoriteIcon: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Timber.e("Playback error: ${error.message}")
            Log.e("PlayerActivity", "Playback error: ${error.message}", error)

            loadingIndicator.visibility = View.GONE
            bufferingText.visibility = View.GONE
            soundWaveView.setPlaying(false)
            isPlaying = false
            playPauseButton.setImageResource(R.drawable.ic_play)

            val errorMessage = when {
                error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                        error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> {
                    "Failed to load stream. Please check your network connection."
                }
                error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED -> {
                    "An unexpected error occurred while playing the stream."
                }
                else -> {
                    "An error occurred: ${error.message}"
                }
            }

            Snackbar.make(
                findViewById(R.id.control_layout),
                errorMessage,
                Snackbar.LENGTH_LONG
            ).setAction("Retry") {
                try {
                    val mediaItem = MediaItem.fromUri(stations[currentStationIndex].streamUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    if (isPlaying) {
                        player.play()
                    }
                } catch (e: Exception) {
                    Timber.e("Retry failed: ${e.message}")
                    Log.e("PlayerActivity", "Retry failed: ${e.message}", e)
                }
            }.show()
        }

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_BUFFERING -> {
                    loadingIndicator.visibility = View.VISIBLE
                    bufferingText.visibility = View.VISIBLE
                    soundWaveView.setPlaying(false)
                }
                Player.STATE_READY -> {
                    loadingIndicator.visibility = View.GONE
                    bufferingText.visibility = View.GONE
                    if (player.playWhenReady) {
                        isPlaying = true
                        playPauseButton.setImageResource(R.drawable.ic_pause)
                        soundWaveView.setPlaying(true)
                    } else {
                        isPlaying = false
                        playPauseButton.setImageResource(R.drawable.ic_play)
                        soundWaveView.setPlaying(false)
                    }
                }
                Player.STATE_ENDED, Player.STATE_IDLE -> {
                    loadingIndicator.visibility = View.GONE
                    bufferingText.visibility = View.GONE
                    isPlaying = false
                    playPauseButton.setImageResource(R.drawable.ic_play)
                    soundWaveView.setPlaying(false)
                }
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            if (playWhenReady && player.playbackState == Player.STATE_READY) {
                isPlaying = true
                playPauseButton.setImageResource(R.drawable.ic_pause)
                soundWaveView.setPlaying(true)
                loadingIndicator.visibility = View.GONE
                bufferingText.visibility = View.GONE
            } else {
                isPlaying = false
                playPauseButton.setImageResource(R.drawable.ic_play)
                soundWaveView.setPlaying(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)

        try {
            currentStationIndex = intent.getIntExtra("STATION_INDEX", 0)
            Log.d("PlayerActivity", "Received STATION_INDEX: $currentStationIndex")

            @Suppress("DEPRECATION")
            stations = intent.getSerializableExtra("STATION_LIST") as? ArrayList<Station>
                ?: throw IllegalStateException("Station list not found in intent")
            Log.d("PlayerActivity", "Received STATION_LIST with size: ${stations.size}")

            if (currentStationIndex < 0 || currentStationIndex >= stations.size) {
                throw IllegalStateException("Invalid station index: $currentStationIndex")
            }

            playPauseButton = findViewById(R.id.play_pause_button)
            val prevButton = findViewById<ImageButton>(R.id.prev_button)
            val nextButton = findViewById<ImageButton>(R.id.next_button)
            val backButton = findViewById<ImageButton>(R.id.back_button)
            soundWaveView = findViewById(R.id.soundwave_view)
            loadingIndicator = findViewById(R.id.loading_indicator)
            bufferingText = findViewById(R.id.buffering_text)
            favoriteIcon = findViewById(R.id.favorite_icon_player)

            val currentStation = stations[currentStationIndex]
            updateUI(currentStation)

            backButton.setOnClickListener {
                finish()
            }

            player = PlaybackManager.getPlayer()
            PlaybackManager.setCurrentStation(currentStation)
            PlaybackManager.addListener(playerListener)

            try {
                val mediaItem = MediaItem.fromUri(currentStation.streamUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            } catch (e: Exception) {
                Timber.e("Failed to prepare media source: ${e.message}")
                Log.e("PlayerActivity", "Failed to prepare media source: ${e.message}", e)
                loadingIndicator.visibility = View.GONE
                bufferingText.visibility = View.GONE
                showErrorSnackbar("Failed to prepare stream: ${e.message}")
            }

            playPauseButton.setOnClickListener {
                if (isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
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

            favoriteIcon.setOnClickListener {
                currentStation.isFavorite = !currentStation.isFavorite
                favoriteIcon.setImageResource(
                    if (currentStation.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                )
                sharedPreferences.edit()
                    .putBoolean(currentStation.name, currentStation.isFavorite)
                    .apply()
                Log.d("PlayerActivity", "Favorite toggled for ${currentStation.name}: ${currentStation.isFavorite}")
                setResult(RESULT_OK) // Notify MainActivity to refresh
            }
        } catch (e: Exception) {
            Log.e("PlayerActivity", "Error in onCreate: ${e.message}", e)
            Snackbar.make(
                findViewById(android.R.id.content),
                "Error starting player: ${e.message}",
                Snackbar.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun updateUI(station: Station) {
        Log.d("PlayerActivity", "Updating UI for station: ${station.name}")
        findViewById<TextView>(R.id.station_name).text = station.name
        findViewById<TextView>(R.id.station_subtext).text = station.name

        Glide.with(this)
            .load(station.imageResId)
            .into(findViewById(R.id.current_station_image))

        val prevIndex = if (currentStationIndex > 0) currentStationIndex - 1 else stations.size - 1
        Glide.with(this)
            .load(stations[prevIndex].imageResId)
            .into(findViewById(R.id.prev_station_image))

        val nextIndex = if (currentStationIndex < stations.size - 1) currentStationIndex + 1 else 0
        Glide.with(this)
            .load(stations[nextIndex].imageResId)
            .into(findViewById(R.id.next_station_image))

        favoriteIcon.setImageResource(
            if (station.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )
    }

    private fun switchStation() {
        val station = stations[currentStationIndex]
        PlaybackManager.setCurrentStation(station)
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
            loadingIndicator.visibility = View.GONE
            bufferingText.visibility = View.GONE
            showErrorSnackbar("Failed to switch station: ${e.message}")
        }
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(
            findViewById(R.id.control_layout),
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            try {
                val mediaItem = MediaItem.fromUri(stations[currentStationIndex].streamUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
                if (isPlaying) {
                    player.play()
                }
            } catch (e: Exception) {
                Timber.e("Retry failed: ${e.message}")
                Log.e("PlayerActivity", "Retry failed: ${e.message}", e)
            }
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        PlaybackManager.removeListener(playerListener)
    }
}