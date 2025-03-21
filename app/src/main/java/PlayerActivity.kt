package com.noahaung.myanmarradio

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.ArrayAdapter
import android.widget.ListView

class PlayerActivity : AppCompatActivity() {

    private lateinit var currentStationImage: ImageView
    private lateinit var prevStationImage: ImageView
    private lateinit var nextStationImage: ImageView
    private lateinit var stationName: TextView
    private lateinit var stationSubtext: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var sleepTimerButton: ImageButton
    private lateinit var loadingIndicator: android.widget.ProgressBar
    private lateinit var bufferingText: TextView
    private lateinit var stations: ArrayList<Station>
    private var stationIndex: Int = 0
    private var sleepTimer: CountDownTimer? = null
    private var isSleepTimerActive: Boolean = false
    private var remainingTimeMillis: Long = 0

    private val playerListener = object : Player.Listener {
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            updatePlayPauseButton()
        }

        override fun onPlaybackStateChanged(state: Int) {
            updatePlayPauseButton()
            if (state == Player.STATE_BUFFERING) {
                loadingIndicator.visibility = android.view.View.VISIBLE
                bufferingText.visibility = android.view.View.VISIBLE
            } else {
                loadingIndicator.visibility = android.view.View.GONE
                bufferingText.visibility = android.view.View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentStationImage = findViewById(R.id.current_station_image)
        prevStationImage = findViewById(R.id.prev_station_image)
        nextStationImage = findViewById(R.id.next_station_image)
        stationName = findViewById(R.id.station_name)
        stationSubtext = findViewById(R.id.station_subtext)
        playPauseButton = findViewById(R.id.play_pause_button)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        favoriteButton = findViewById(R.id.favorite_button)
        sleepTimerButton = findViewById(R.id.sleep_timer_button)
        loadingIndicator = findViewById(R.id.loading_indicator)
        bufferingText = findViewById(R.id.buffering_text)

        stations = intent.getSerializableExtra("STATION_LIST") as ArrayList<Station>
        stationIndex = intent.getIntExtra("STATION_INDEX", 0)

        if (stations.isNotEmpty() && stationIndex in stations.indices) {
            val station = stations[stationIndex]
            updateUI(station)
            updateFavoriteButton(station)
            updateAdjacentStations()
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        playPauseButton.setOnClickListener {
            if (PlaybackManager.isPlaying()) {
                PlaybackManager.getPlayer().pause()
                val intent = Intent(this, PlaybackService::class.java).apply {
                    action = "PAUSE"
                }
                startService(intent)
            } else {
                PlaybackManager.getPlayer().play()
                val intent = Intent(this, PlaybackService::class.java).apply {
                    action = "PLAY"
                }
                startService(intent)
            }
        }

        prevButton.setOnClickListener {
            if (stationIndex > 0) {
                stationIndex--
                val station = stations[stationIndex]
                PlaybackManager.setCurrentStation(station)
                val intent = Intent(this, PlaybackService::class.java).apply {
                    action = "PREVIOUS"
                }
                startService(intent)
                updateUI(station)
                updateFavoriteButton(station)
                updateAdjacentStations()
            }
        }

        nextButton.setOnClickListener {
            if (stationIndex < stations.size - 1) {
                stationIndex++
                val station = stations[stationIndex]
                PlaybackManager.setCurrentStation(station)
                val intent = Intent(this, PlaybackService::class.java).apply {
                    action = "NEXT"
                }
                startService(intent)
                updateUI(station)
                updateFavoriteButton(station)
                updateAdjacentStations()
            }
        }

        favoriteButton.setOnClickListener {
            val station = stations[stationIndex]
            station.isFavorite = !station.isFavorite
            getSharedPreferences("favorites", MODE_PRIVATE).edit()
                .putBoolean(station.name, station.isFavorite)
                .apply()
            updateFavoriteButton(station)
        }

        sleepTimerButton.setOnClickListener {
            showSleepTimerDialog()
        }

        PlaybackManager.addListener(playerListener)
        updatePlayPauseButton()
    }

    private fun updateUI(station: Station) {
        stationName.text = station.name
        stationSubtext.text = station.name
        Glide.with(this)
            .load(station.imageResId)
            .into(currentStationImage)
    }

    private fun updateAdjacentStations() {
        if (stationIndex > 0) {
            Glide.with(this)
                .load(stations[stationIndex - 1].imageResId)
                .into(prevStationImage)
        } else {
            prevStationImage.setImageDrawable(null)
        }

        if (stationIndex < stations.size - 1) {
            Glide.with(this)
                .load(stations[stationIndex + 1].imageResId)
                .into(nextStationImage)
        } else {
            nextStationImage.setImageDrawable(null)
        }
    }

    private fun updatePlayPauseButton() {
        playPauseButton.setImageResource(
            if (PlaybackManager.isPlaying()) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    private fun updateFavoriteButton(station: Station) {
        favoriteButton.setImageResource(
            if (station.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )
    }

    private fun updateSleepTimerIcon() {
        if (isSleepTimerActive) {
            sleepTimerButton.setColorFilter(android.graphics.Color.YELLOW)
        } else {
            sleepTimerButton.clearColorFilter()
        }
    }

    private fun showSleepTimerDialog() {
        val durations = if (isSleepTimerActive) {
            arrayOf("15 minutes", "30 minutes", "45 minutes", "1 hour", "1.5 hours", "2 hours", "Turn off timer")
        } else {
            arrayOf("15 minutes", "30 minutes", "45 minutes", "1 hour", "1.5 hours", "2 hours")
        }
        val durationValues = longArrayOf(
            15 * 60 * 1000L,  // 15 minutes
            30 * 60 * 1000L,  // 30 minutes
            45 * 60 * 1000L,  // 45 minutes
            60 * 60 * 1000L,  // 1 hour
            90 * 60 * 1000L,  // 1.5 hours
            120 * 60 * 1000L  // 2 hours
        )

        val dialogView = layoutInflater.inflate(R.layout.dialog_sleep_timer_list, null)
        val headerText = dialogView.findViewById<TextView>(R.id.sleep_timer_header)
        val listView = dialogView.findViewById<ListView>(R.id.sleep_timer_list)

        // Set header text
        if (isSleepTimerActive) {
            val minutesLeft = (remainingTimeMillis / 1000 / 60).toInt()
            headerText.text = "Sleep timer - $minutesLeft min left"
        } else {
            headerText.text = "Set Sleep Timer"
        }

        // Set up the list
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, durations).apply {
            setDropDownViewResource(android.R.layout.simple_list_item_1)
        }
        listView.adapter = adapter

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        dialog.show()

        listView.setOnItemClickListener { _, _, position, _ ->
            if (isSleepTimerActive && position == durations.size - 1) {
                // "Turn off timer" was clicked
                stopSleepTimer()
                updateSleepTimerIcon()
            } else {
                val duration = durationValues[position]
                startSleepTimer(duration)
                updateSleepTimerIcon()
            }
            dialog.dismiss()
        }
    }

    private fun startSleepTimer(duration: Long) {
        stopSleepTimer()
        sleepTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
            }

            override fun onFinish() {
                PlaybackManager.getPlayer().stop()
                val intent = Intent(this@PlayerActivity, PlaybackService::class.java).apply {
                    action = "PAUSE"
                }
                startService(intent)
                isSleepTimerActive = false
                updateSleepTimerIcon()
            }
        }.start()
        isSleepTimerActive = true
        remainingTimeMillis = duration
    }

    private fun stopSleepTimer() {
        sleepTimer?.cancel()
        sleepTimer = null
        isSleepTimerActive = false
        remainingTimeMillis = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        PlaybackManager.removeListener(playerListener)
        stopSleepTimer()
    }
}