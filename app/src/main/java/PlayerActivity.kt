package com.noahaung.myanmarradio

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.widget.ImageView

class PlayerActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private var isPlaying = false
    private lateinit var audioManager: AudioManager
    private lateinit var playPauseButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Initialize AudioManager for volume control
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val name = intent.getStringExtra("STATION_NAME") ?: "Unknown"
        val url = intent.getStringExtra("STATION_URL") ?: ""
        val imageUrl = intent.getStringExtra("STATION_IMAGE") ?: ""

        findViewById<TextView>(R.id.station_name).text = name
        playPauseButton = findViewById<ImageButton>(R.id.play_pause_button)
        val stopButton = findViewById<ImageButton>(R.id.stop_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val volumeControl = findViewById<SeekBar>(R.id.volume_control)
        val background = findViewById<ImageView>(R.id.station_background)

        Picasso.get().load(imageUrl).into(background)

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()
        val dataSourceFactory = DefaultDataSource.Factory(this)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))
        player.setMediaSource(mediaSource)
        player.prepare()

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

        // Stop button
        stopButton.setOnClickListener {
            if (isPlaying) {
                player.stop()
                playPauseButton.setImageResource(R.drawable.ic_play_arrow)
                isPlaying = false
            }
        }

        // Back button
        backButton.setOnClickListener { finish() }

        // Volume control
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeControl.max = maxVolume
        volumeControl.progress = currentVolume

        volumeControl.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}