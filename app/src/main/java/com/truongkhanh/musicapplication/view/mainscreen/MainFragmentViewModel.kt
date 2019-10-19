package com.truongkhanh.musicapplication.view.mainscreen

import android.app.Activity
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.media.EMPTY_PLAYBACK_STATE
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.NOTHING_PLAYING
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.model.NowPlayingMetadata
import com.truongkhanh.musicapplication.util.*
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingActivity

class MainFragmentViewModel(mediaSessionConnection: MediaSessionConnection) :
    ViewModel() {

    val rootMediaID: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected)
                mediaSessionConnection.rootMediaId
            else
                null
        }

    val navigateToActivity: LiveData<Event<Activity>> get() = _navigateToActivity
    private val _navigateToActivity = MutableLiveData<Event<Activity>>()

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

    fun mediaItemClick(itemData: MediaItemData) {
        playMedia(itemData)
        showFragment(NowPlayingActivity())
    }

    fun playMediaBySearch(keyWord: String, action: String) {
        val transportControls = mediaSessionConnection.transportControls
        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepare ?: false
        if (isPrepared) {
            transportControls.stop()
        }
        playMediaFromSearch(keyWord, action)
        showFragment(NowPlayingActivity())
    }

    fun onClickPlayButton() {
        if(playbackState.isPlaying) {
            mediaSessionConnection.transportControls.pause()
        } else if (playbackState.isPauseEnable) {
            mediaSessionConnection.transportControls.play()
        }
    }

    private fun showFragment(activity: Activity) {
        _navigateToActivity.value = Event(activity)
    }

    private fun playMedia(itemData: MediaItemData) {
        val nowPlaying = mediaSessionConnection.mediaMetadata.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepare ?: false
        if (!(isPrepared && itemData.mediaId == nowPlaying?.id))
            transportControls.playFromMediaId(itemData.mediaId, null)
    }

    private fun playMediaFromSearch(artist: String, action: String) {
        val bundle = Bundle()
        bundle.putString(BUNDLE_ACTION, action)
        mediaSessionConnection.transportControls.playFromSearch(artist, bundle)
    }

    private fun updateState(playbackState: PlaybackStateCompat, mediaMetaData: MediaMetadataCompat) {
        if (mediaMetaData.duration != 0L) {
            val nowPlayingMetadata =
                NowPlayingMetadata(
                    mediaMetaData.id,
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