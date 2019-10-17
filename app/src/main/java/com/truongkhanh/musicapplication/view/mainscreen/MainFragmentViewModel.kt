package com.truongkhanh.musicapplication.view.mainscreen

import android.app.Activity
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.util.*
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingActivity
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingFragment

class MainFragmentViewModel(private val mediaSessionConnection: MediaSessionConnection) :
    ViewModel() {

    val rootMediaID: LiveData<String> =
        Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
            if (isConnected)
                mediaSessionConnection.rootMediaId
            else
                null
        }

    val navigateToFragment: LiveData<Event<Activity>> get() = _navigateToFragment
    private val _navigateToFragment = MutableLiveData<Event<Activity>>()

    fun mediaItemClick(itemData: MediaItemData) {
        playMedia(itemData)
        showFragment(NowPlayingActivity())
    }

    private fun showFragment(activity: Activity) {
        _navigateToFragment.value = Event(activity)
    }

    private fun playMedia(itemData: MediaItemData) {
        val nowPlaying = mediaSessionConnection.mediaMetadata.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepare ?: false
        if (isPrepared && itemData.mediaId == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnable -> transportControls.play()
                    else -> {
                        Log.w(
                            "Debuggg",
                            "User click play/pause button but neither pause and play are enabled"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(itemData.mediaId, null)
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