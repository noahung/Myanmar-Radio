package com.noahaung.myanmarradio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.bumptech.glide.Glide
import android.graphics.Canvas
import android.graphics.Color

class PlaybackService : Service() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private var currentStation: Station? = null
    private lateinit var notificationManager: NotificationManager

    companion object {
        private const val CHANNEL_ID = "playback_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize the ExoPlayer and MediaSession
        player = PlaybackManager.getPlayer()
        mediaSession = MediaSession.Builder(this, player).build()

        // Initialize the NotificationManager
        notificationManager = getSystemService(NotificationManager::class.java)

        // Create a notification channel
        createNotificationChannel()

        // Listen for playback state changes to update the notification
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                android.util.Log.d("PlaybackService", "onPlaybackStateChanged: state=$state")
                updateNotification() // Update the notification when playback state changes
                if (state == Player.STATE_ENDED || state == Player.STATE_IDLE) {
                    stopForeground(STOP_FOREGROUND_REMOVE) // Updated to use the new method
                    stopSelf()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateNotification() // Update the notification when play/pause state changes
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        android.util.Log.d("PlaybackService", "onStartCommand: action=${intent?.action}")
        when (intent?.action) {
            "PLAY" -> {
                currentStation = PlaybackManager.getCurrentStation()
                if (currentStation != null) {
                    val mediaItem = androidx.media3.common.MediaItem.fromUri(currentStation!!.streamUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    android.util.Log.d("PlaybackService", "onStartCommand: Playing station: ${currentStation?.name}")
                    updateNotification() // Show the notification when playback starts
                } else {
                    android.util.Log.w("PlaybackService", "onStartCommand: No current station to play")
                }
            }
            "PAUSE" -> {
                player.pause()
                android.util.Log.d("PlaybackService", "onStartCommand: Paused playback")
            }
            "NEXT" -> {
                val stations = PlaybackManager.getStations()
                val currentIndex = stations.indexOf(currentStation)
                if (currentIndex < stations.size - 1) {
                    currentStation = stations[currentIndex + 1]
                    PlaybackManager.setCurrentStation(currentStation!!)
                    val mediaItem = androidx.media3.common.MediaItem.fromUri(currentStation!!.streamUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    android.util.Log.d("PlaybackService", "onStartCommand: Switched to next station: ${currentStation?.name}")
                }
            }
            "PREVIOUS" -> {
                val stations = PlaybackManager.getStations()
                val currentIndex = stations.indexOf(currentStation)
                if (currentIndex > 0) {
                    currentStation = stations[currentIndex - 1]
                    PlaybackManager.setCurrentStation(currentStation!!)
                    val mediaItem = androidx.media3.common.MediaItem.fromUri(currentStation!!.streamUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    android.util.Log.d("PlaybackService", "onStartCommand: Switched to previous station: ${currentStation?.name}")
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        android.util.Log.d("PlaybackService", "onDestroy: Cleaning up")
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Playback Controls",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Controls for Myanmar Radio playback"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        if (currentStation == null) {
            stopForeground(STOP_FOREGROUND_REMOVE) // Updated to use the new method
            return
        }

        // Load the station image (or use a fallback)
        val largeIcon: Bitmap = try {
            Glide.with(this)
                .asBitmap()
                .load(currentStation?.imageResId ?: R.drawable.ic_launcher_foreground)
                .submit()
                .get()
        } catch (e: Exception) {
            android.util.Log.e("PlaybackService", "updateNotification: Failed to load image: ${e.message}", e)
            Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888).apply {
                val canvas = Canvas(this)
                canvas.drawColor(Color.GRAY)
            }
        }

        // Create intents for the notification actions
        val playPauseIntent = Intent(this, PlaybackService::class.java).apply {
            action = if (player.isPlaying) "PAUSE" else "PLAY"
        }
        val playPausePendingIntent = PendingIntent.getService(
            this,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, PlaybackService::class.java).apply {
            action = "NEXT"
        }
        val nextPendingIntent = PendingIntent.getService(
            this,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val previousIntent = Intent(this, PlaybackService::class.java).apply {
            action = "PREVIOUS"
        }
        val previousPendingIntent = PendingIntent.getService(
            this,
            0,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create an intent to open the app when the notification is tapped
        val contentIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this resource exists
            .setContentTitle(currentStation?.name ?: "Myanmar Radio")
            .setContentText(if (player.isPlaying) "Playing" else "Paused")
            .setLargeIcon(largeIcon)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true) // Make the notification ongoing (cannot be swiped away)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.ic_prev, // Add a previous icon to your drawable resources
                "Previous",
                previousPendingIntent
            )
            .addAction(
                if (player.isPlaying) R.drawable.ic_pause else R.drawable.ic_play, // Add play/pause icons
                if (player.isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(
                R.drawable.ic_next, // Add a next icon to your drawable resources
                "Next",
                nextPendingIntent
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2) // Show all actions in compact view
                    .setMediaSession(mediaSession.sessionCompatToken) // Link to MediaSession for lock screen controls
            )
            .build()

        // Start the foreground service with the notification
        startForeground(NOTIFICATION_ID, notification)
        android.util.Log.d("PlaybackService", "updateNotification: Notification updated")
    }
}