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
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.truongkhanh.musicapplication.model.MusicSource
import com.truongkhanh.musicapplication.util.MUSIC_SERVICE
import com.truongkhanh.musicapplication.util.NOW_PLAYING_NOTIFICATION
import com.truongkhanh.musicapplication.util.flag

private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
private const val LOG_TAG = "log_tag"

class MusicService() : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var musicSource: MusicSource
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var isForegroundService = false

    private val mAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this).apply {
            setAudioAttributes(mAudioAttributes, true)
        }
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>
    ) {
        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }

        val results = musicSource.whenReady {success ->
            if (success) {
                val childrens = musicSource.map {mediaMetadataCompat ->
                    MediaItem(
                        mediaMetadataCompat.description,
                        mediaMetadataCompat.flag
                    )
                }.toMutableList()
                result.sendResult(childrens)
            } else {
                result.sendResult(null)
            }
        }

        if (!results) {
            result.detach()
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (allowBrowsing(clientPackageName, clientUid)) {
            BrowserRoot(MY_MEDIA_ROOT_ID, null)
        } else {
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

        musicSource = GetMusicHelper(this)
        musicSource.load()

        mediaSessionConnector = MediaSessionConnector(mediaSession).also {connector ->
            val dataSourceFactory = DefaultDataSourceFactory(
                this, Util.getUserAgent(this, EXO_USER_AGENT), null
            )

            val playbackPreparer = PlaybackPreparer(
                musicSource,
                exoPlayer,
                dataSourceFactory
            )

            connector.setPlayer(exoPlayer)
            connector.setPlaybackPreparer(playbackPreparer)
            connector.setQueueNavigator(QueueNavigator(mediaSession))
        }
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

private class QueueNavigator(
    mediaSession: MediaSessionCompat
) : TimelineQueueNavigator(mediaSession) {
    private val window = Timeline.Window()
    override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
        player.currentTimeline
            .getWindow(windowIndex, window, true).tag as MediaDescriptionCompat
}

const val GET_MUSIC_FAILED = "com.truongkhanh.musicapplication.media.getmusicfailed"

private const val EXO_USER_AGENT = "exoAgent"