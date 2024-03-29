package com.truongkhanh.musicapplication.media

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DataSource
import com.truongkhanh.musicapplication.util.*

class PlaybackPreparer(
    private val musicSource: MusicSource,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.PlaybackPreparer {

    override fun getSupportedPrepareActions(): Long =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH

    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        musicSource.whenReady {
            val itemToPlay: MediaMetadataCompat? = musicSource.find { item ->
                item.id == mediaId
            }
            val filter = extras?.getString(BUNDLE_FILTER)
            if (itemToPlay == null) {
                // TODO: Notify caller of the error.
            } else {
                val metadataList = buildPlaylist(filter, itemToPlay)
                val mediaSource = metadataList.toMediaSource(dataSourceFactory)

                val initialWindowIndex = metadataList.indexOf(itemToPlay)

                exoPlayer.prepare(mediaSource)
                exoPlayer.seekTo(initialWindowIndex, 0)
            }
        }
    }

    private fun buildPlaylist(filter: String?, item: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        return when (filter) {
            FILTER_ARTIST -> {
                musicSource.filter { it.artist == item.artist }.sortedBy { it.trackNumber }.toMutableList()
            }
            FILTER_ALBUM -> {
                musicSource.filter { it.album == item.album }.sortedBy { it.trackNumber }.toMutableList()
            }
            else -> {
                musicSource.toMutableList()
            }
        }
    }

    override fun onCommand(
        player: Player?,
        controlDispatcher: ControlDispatcher?,
        command: String?,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean = false

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
        musicSource.whenReady {
            val metaDataList =
                musicSource.search(query ?: "", extras ?: Bundle.EMPTY)
            if(metaDataList.isNotEmpty()) {
                val mediaSource = metaDataList.toMediaSource(dataSourceFactory)
                exoPlayer.prepare(mediaSource)
            }
        }
    }

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) = Unit

    override fun onPrepare() = Unit

}