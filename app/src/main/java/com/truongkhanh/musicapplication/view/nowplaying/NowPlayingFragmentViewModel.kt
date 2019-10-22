package com.truongkhanh.musicapplication.view.nowplaying

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
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

class NowPlayingFragmentViewModel(private val application: Application, private val mediaID: String, mediaSessionConnection: MediaSessionConnection): ViewModel() {

    var mediaMetadata = MutableLiveData<NowPlayingMetadata>()
    var mediaPosition = MutableLiveData<Long>().apply { postValue(0L) }
    var buttonPlayResource = MutableLiveData<Int>()
    var totalPosition = MutableLiveData<Long>().apply { postValue(0L) }
    var buttonNextEnable = MutableLiveData<Boolean>()
    var buttonPreviousEnable = MutableLiveData<Boolean>()
    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems
    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
            val itemsList = children.map { child ->
                val avatarBitmap = getSongBitmap(child, application)
                val subtitle = child.description.subtitle ?: ""
                MediaItemData(
                    child.mediaId!!,
                    child.description.title.toString(),
                    subtitle.toString(),
                    avatarBitmap,
                    child.isBrowsable,
                    child.description.description.toString(),
                    getResourceForMediaId(child.mediaId!!)
                )
            }
            _mediaItems.postValue(itemsList)
        }
    }

    private var playbackState = EMPTY_PLAYBACK_STATE
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isLoopHandler = true

    private val playbackStateForever = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metaData = mediaSessionConnection.mediaMetadata.value ?: NOTHING_PLAYING
        _mediaItems.postValue(updateState(playbackState, metaData))
    }

    private val mediaMetaDataForever = Observer<MediaMetadataCompat> {metaData ->
        _mediaItems.postValue(updateState(playbackState, metaData))
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(mediaID, subscriptionCallback)
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
    ): List<MediaItemData> {
        if (mediaMetaData.duration != 0L) {
            val nowPlayingMetadata =
                NowPlayingMetadata(
                    mediaMetaData.id!!,
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
        val newResId = when (playbackState.isPlaying) {
            true -> R.drawable.ic_play_circle_filled_black_24dp
            else -> R.drawable.ic_pause_circle_filled_black_24dp
        }

        return mediaItems.value?.map {
            val useResId = if (it.mediaId == mediaMetaData.id) newResId else NO_RESOURCE
            it.copy(playbackRes = useResId)
        } ?: emptyList()
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

    fun updateRepeatMode(repeatMode: Int) {
        mediaSessionConnection.transportControls.setRepeatMode(repeatMode)
    }

    fun updateShuffleMode(shuffleMode: Int) {
        mediaSessionConnection.transportControls.setShuffleMode(shuffleMode)
    }

    fun playOrPause() {
        if(playbackState.isPlaying) {
            mediaSessionConnection.transportControls.pause()
        } else if (playbackState.isPauseEnable) {
            mediaSessionConnection.transportControls.play()
        }
    }

    fun sipToQueueItem(id: Long) {
        mediaSessionConnection.transportControls.skipToQueueItem(id)
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateForever)
        mediaSessionConnection.mediaMetadata.removeObserver(mediaMetaDataForever)
        mediaSessionConnection.unSubscribe(mediaID, subscriptionCallback)
        isLoopHandler = false
    }

    private fun getResourceForMediaId(mediaId: String): Int {
        mediaSessionConnection.mediaMetadata.value?.id?.let{
            val isActive = mediaId == it
            val isPlaying = mediaSessionConnection.playbackState.value?.isPlaying ?: false
            return when {
                !isActive -> NO_RESOURCE
                isPlaying -> R.drawable.ic_play_circle_filled_black_24dp
                else -> R.drawable.ic_pause_circle_filled_black_24dp
            }
        }?: return NO_RESOURCE
    }

    class Factory(private var application: Application, private var mediaID: String, private var mediaSessionConnection: MediaSessionConnection): ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return NowPlayingFragmentViewModel(
                application,
                mediaID,
                mediaSessionConnection
            ) as T
        }
    }
}

private const val NO_RESOURCE = 0
private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L