package com.noahaung.myanmarradio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import android.widget.Button
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.widget.ImageView

class PlayerActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val name = intent.getStringExtra("STATION_NAME") ?: "Unknown"
        val url = intent.getStringExtra("STATION_URL") ?: ""
        val imageUrl = intent.getStringExtra("STATION_IMAGE") ?: ""

        findViewById<TextView>(R.id.station_name).text = name
        val playPauseButton = findViewById<Button>(R.id.play_pause_button)
        val backButton = findViewById<Button>(R.id.back_button)
        val background = findViewById<ImageView>(R.id.station_background)

        Picasso.get().load(imageUrl).into(background)

        // Create ExoPlayer instance with Builder
        player = ExoPlayer.Builder(this).build()

        // Create a media source for the stream URL
        val dataSourceFactory = DefaultDataSource.Factory(this)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))

        // Set the media source and prepare the player
        player.setMediaSource(mediaSource)
        player.prepare()

        playPauseButton.setOnClickListener {
            if (isPlaying) {
                player.pause()
                playPauseButton.text = "Play"
            } else {
                player.play()
                playPauseButton.text = "Pause"
            }
            isPlaying = !isPlaying
        }

        backButton.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}