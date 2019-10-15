package com.truongkhanh.musicapplication.view.song

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.media.EMPTY_PLAYBACK_STATE
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.NOTHING_PLAYING
import com.truongkhanh.musicapplication.util.*
import kotlin.math.floor

class SongFragmentViewModel(private val application: Application, mediaSessionConnection: MediaSessionConnection): ViewModel() {

    data class NowPlayingMetadata(
        val id: String,
        val albumArtUri: Uri,
        val title: String?,
        val subtitle: String?,
        val duration: String
    ) {

        companion object {
            /**
             * Utility method to convert milliseconds to a display of minutes and seconds
             */
            fun timestampToMSS(context: Context, position: Long): String {
                val totalSeconds = floor(position / 1E3).toInt()
                val minutes = totalSeconds / 60
                val remainingSeconds = totalSeconds - (minutes * 60)
                return if (position < 0) context.getString(R.string.duration_unknown)
                else context.getString(R.string.duration_format).format(minutes, remainingSeconds)
            }
        }
    }

    private var playbackState = EMPTY_PLAYBACK_STATE
    private var mediaMetadata = MutableLiveData<NowPlayingMetadata>()
    private var buttonPlayResource = MutableLiveData<Int>()
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var mediaPosition = MutableLiveData<Long>().apply {
        postValue(0L)
    }
    private var isLoopHandler = true

    private val playbackStateForever = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metaData = mediaSessionConnection.mediaMetadata.value ?: NOTHING_PLAYING
        updateState(playbackState, metaData)
    }

    private val mediaMetaDataForever = Observer<MediaMetadataCompat> {
        updateState(playbackState, it)
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.playbackState.observeForever(playbackStateForever)
        it.mediaMetadata.observeForever(mediaMetaDataForever)
        updatePlayPosition()
    }

    private fun updatePlayPosition():Boolean = handler.postDelayed({
        val currentPosition = playbackState.currentPlaybackPosition
        if(mediaPosition.value != currentPosition)
            mediaPosition.postValue(currentPosition)
        if(isLoopHandler)
            updatePlayPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetaData: MediaMetadataCompat
    ) {
        if (mediaMetaData.duration != 0L) {
            val nowPlayingMetadata = NowPlayingMetadata(
                mediaMetaData.id,
                mediaMetaData.albumArtUri,
                mediaMetaData.title?.trim(),
                mediaMetaData.displaySubtitle?.trim(),
                NowPlayingMetadata.timestampToMSS(application, mediaMetaData.duration)
            )
            this.mediaMetadata.postValue(nowPlayingMetadata)

            buttonPlayResource.postValue(
                when(playbackState.isPlaying) {
                    true -> R.drawable.ic_pause_black_24dp
                    else -> R.drawable.ic_play_arrow_black_24dp
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateForever)
        mediaSessionConnection.mediaMetadata.removeObserver(mediaMetaDataForever)

        //Stop looping update position
        isLoopHandler = false
    }

    class Factory(private var application: Application, private var mediaSessionConnection: MediaSessionConnection): ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SongFragmentViewModel(application, mediaSessionConnection) as T
        }
    }
}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L