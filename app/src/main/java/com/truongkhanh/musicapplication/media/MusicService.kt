package com.truongkhanh.musicapplication.media

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.truongkhanh.musicapplication.util.MUSIC_SERVICE
import com.truongkhanh.musicapplication.util.NOW_PLAYING_CHANNEL
import com.truongkhanh.musicapplication.util.NOW_PLAYING_NOTIFICATION

private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
private const val LOG_TAG = "log_tag"

class MusicService() : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    private var isActive: Boolean = false
    private lateinit var stateBuilder: PlaybackState.Builder
    private var isForegroundService = false

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }


    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        return if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            BrowserRoot(MY_MEDIA_ROOT_ID, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        return true
    }

    override fun onCreate() {
        super.onCreate()

        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        mediaSession = MediaSessionCompat(this, MUSIC_SERVICE).apply {
            setSessionActivity(sessionActivityPendingIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(MediaControllerCallback())
        }

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        becomingNoisyReceiver = BecomingNoisyReceiver(this, mediaSession.sessionToken)

        //TODO: Get the music playlist or some how passing music playlist to service


    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let {
                updateNotification(it)
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaController.playbackState?.let{
                updateNotification(it)
            }
        }
    }

    private fun updateNotification(state: PlaybackStateCompat) {
        val updateState = state.state

        val notification = if (state.state != PlaybackStateCompat.STATE_NONE && mediaController.metadata != null) {
            notificationBuilder.buildNotification(mediaSession.sessionToken)
        } else {
            null
        }

        when (updateState) {
            PlaybackStateCompat.STATE_BUFFERING, PlaybackStateCompat.STATE_PLAYING -> {
                becomingNoisyReceiver.register()

                notification?.let{
                    notificationManager.notify(NOW_PLAYING_NOTIFICATION, it)
                    if (!isForegroundService) {
                        ContextCompat.startForegroundService(applicationContext,
                            Intent(applicationContext, this@MusicService::class.java)
                        )
                        startForeground(NOW_PLAYING_NOTIFICATION, notification)
                        isForegroundService = true
                    }
                }
            }
            else -> {
                becomingNoisyReceiver.unRegister()

                if (isForegroundService) {
                    stopForeground(false)
                    isForegroundService = false

                    if (updateState == PlaybackStateCompat.STATE_NONE) {
                        stopSelf()
                    }

                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    } else {
                        removeNowPlayingNotification()
                    }
                }
            }
        }
    }

    private fun removeNowPlayingNotification() {
        stopForeground(true)
    }

    private inner class BecomingNoisyReceiver(
        private var context: Context,
        sessionToken: MediaSessionCompat.Token
    ):BroadcastReceiver() {

        private var noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        private var controller = MediaControllerCompat(context, sessionToken)

        private var register: Boolean = false

        fun register() {
            if (!register) {
                context.registerReceiver(this, noisyIntentFilter)
                register = true
            }
        }

        fun unRegister() {
            if (register) {
                context.unregisterReceiver(this)
                register = false
            }
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                controller.transportControls.pause()
            }
        }

    }


}