package com.truongkhanh.musicapplication.view.nowplaying

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.Player
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.media.EMPTY_PLAYBACK_STATE
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.NOTHING_PLAYING
import com.truongkhanh.musicapplication.util.*
import kotlin.math.floor

class NowPlayingFragmentViewModel(private val application: Application, mediaSessionConnection: MediaSessionConnection): ViewModel() {

    data class NowPlayingMetadata(
        val id: String,
        val displayIcon: Bitmap?,
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

    var mediaMetadata = MutableLiveData<NowPlayingMetadata>()
    var mediaPosition = MutableLiveData<Long>().apply {
        postValue(0L)
    }
    var buttonPlayResource = MutableLiveData<Int>()
    var totalPosition = MutableLiveData<Long>().apply {
        postValue(0L)
    }
    var buttonNextEnable = MutableLiveData<Boolean>()
    var buttonPreviousEnable = MutableLiveData<Boolean>()

    private var playbackState = EMPTY_PLAYBACK_STATE
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isLoopHandler = true

    private val playbackStateForever = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metaData = mediaSessionConnection.mediaMetadata.value ?: NOTHING_PLAYING
        updateState(playbackState, metaData)
    }

    private val mediaMetaDataForever = Observer<MediaMetadataCompat> {metaData ->
        updateState(playbackState, metaData)
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
            val nowPlayingMetadata =
                NowPlayingMetadata(
                    mediaMetaData.id,
                    mediaMetaData.description.iconBitmap,
                    mediaMetaData.title?.trim(),
                    mediaMetaData.displaySubtitle?.trim(),
                    NowPlayingMetadata.timestampToMSS(
                        application,
                        mediaMetaData.duration
                    )
                )
            this.mediaMetadata.postValue(nowPlayingMetadata)
            this.totalPosition.postValue(mediaMetaData.duration)
            buttonPlayResource.postValue(
                when(playbackState.isPlaying) {
                    true -> R.drawable.ic_pause_black_24dp
                    else -> R.drawable.ic_play_arrow_black_24dp
                }
            )
            buttonNextEnable.postValue(playbackState.isSkipToNextEnabled)
            buttonPreviousEnable.postValue(playbackState.isSkipToPreviousEnabled)
        }
    }

    fun updatePosition(newPosition: Long) {
        mediaSessionConnection.transportControls.seekTo(newPosition)
    }

    fun changeRepeatMode(newRepeatMode: Int) {
        mediaSessionConnection.transportControls.setRepeatMode(newRepeatMode)
    }

    fun changeShuffleMode(newShuffleMode: Int) {
        mediaSessionConnection.transportControls.setShuffleMode(newShuffleMode)
    }

    fun playNext() {
        mediaSessionConnection.transportControls.skipToNext()
    }

    fun playPrevious() {
        mediaSessionConnection.transportControls.skipToPrevious()
    }

    fun playOrPause() {
        if(playbackState.isPlaying) {
            mediaSessionConnection.transportControls.pause()
        } else if (playbackState.isPauseEnable) {
            mediaSessionConnection.transportControls.play()
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
            return NowPlayingFragmentViewModel(
                application,
                mediaSessionConnection
            ) as T
        }
    }
}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L