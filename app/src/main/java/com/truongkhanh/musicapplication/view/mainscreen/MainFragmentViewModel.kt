package com.truongkhanh.musicapplication.view.mainscreen

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.media.EMPTY_PLAYBACK_STATE
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.NOTHING_PLAYING
import com.truongkhanh.musicapplication.model.NowPlayingMetadata
import com.truongkhanh.musicapplication.util.*

class MainFragmentViewModel(mediaSessionConnection: MediaSessionConnection) :
    ViewModel() {

    val rootMediaID: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected)
                mediaSessionConnection.rootMediaId
            else
                null
        }

    val navigateToActivity: LiveData<Event<String>> get() = _navigateToActivity
    private val _navigateToActivity = MutableLiveData<Event<String>>()

    private var playbackState = EMPTY_PLAYBACK_STATE
    var mediaMetadata = MutableLiveData<NowPlayingMetadata>()
    var buttonPlayResource = MutableLiveData<Int>()

    private val playbackStateForever = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metaData = mediaSessionConnection.mediaMetadata.value ?: NOTHING_PLAYING
        updateState(playbackState, metaData)
    }

    private val mediaMetaDataForever = Observer<MediaMetadataCompat> { metaData ->
        updateState(playbackState, metaData)
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.playbackState.observeForever(playbackStateForever)
        it.mediaMetadata.observeForever(mediaMetaDataForever)
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateForever)
        mediaSessionConnection.mediaMetadata.removeObserver(mediaMetaDataForever)
    }

    fun playByMediaID(mediaID: String, rootMediaID: String) {
        playMediaID(mediaID)
        nowPlayingRootMediaID = rootMediaID
        showNowPlayingFragment(rootMediaID)
    }

    fun onClickPlayButton() {
        if(playbackState.isPlaying) {
            mediaSessionConnection.transportControls.pause()
        } else if (playbackState.isPauseEnable) {
            mediaSessionConnection.transportControls.play()
        }
    }

    private fun showNowPlayingFragment(mediaID: String) {
        _navigateToActivity.value = Event(mediaID)
    }

    private fun playMediaID(mediaID: String) {
        val nowPlaying = mediaSessionConnection.mediaMetadata.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepare ?: false
        if (!(isPrepared && mediaID == nowPlaying?.id)) {
            transportControls.stop()
            transportControls.playFromMediaId(mediaID, null)
        }
    }

    private fun updateState(playbackState: PlaybackStateCompat, mediaMetaData: MediaMetadataCompat) {
        if (mediaMetaData.duration != 0L) {
            val nowPlayingMetadata =
                NowPlayingMetadata(
                    mediaMetaData.id!!,
                    mediaMetaData.description.iconBitmap,
                    mediaMetaData.title?.trim(),
                    mediaMetaData.displaySubtitle?.trim(),
                    ""
                )
            mediaMetadata.postValue(nowPlayingMetadata)
            buttonPlayResource.postValue(
                when(playbackState.isPlaying) {
                    true -> R.drawable.ic_pause_black_24dp
                    else -> R.drawable.ic_play_arrow_black_24dp
                }
            )
        }
    }

    class Factory(private var mediaSessionConnection: MediaSessionConnection) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainFragmentViewModel(mediaSessionConnection) as T
        }
    }
}