package com.truongkhanh.musicapplication.view.artist

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.media.EMPTY_PLAYBACK_STATE
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.NOTHING_PLAYING
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.util.id
import com.truongkhanh.musicapplication.util.isPlaying

class ArtistFragmentViewModel(context: Context, private val mediaID: String, mediaSessionConnection: MediaSessionConnection) : ViewModel() {

    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
            val itemsList = children.map { child ->
                val avatarBitmap = if (child.description.iconBitmap != null) {
                    child.description.iconBitmap
                } else {
                    BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
                }
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

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = mediaSessionConnection.mediaMetadata.value ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaItems.postValue(updateState(playbackState, metadata))
        }
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState = mediaSessionConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val metadata = it ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaItems.postValue(updateState(playbackState, metadata))
        }
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        Log.d("Debuggg", "Artist subscribe: $mediaID")
        it.subscribe(mediaID, subscriptionCallback)
        it.playbackState.observeForever(playbackStateObserver)
        it.mediaMetadata.observeForever(mediaMetadataObserver)
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.mediaMetadata.removeObserver(mediaMetadataObserver)
        mediaSessionConnection.unSubscribe(mediaID, subscriptionCallback)
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ): List<MediaItemData> {
        val newResId = when (playbackState.isPlaying) {
            true -> R.drawable.ic_play_arrow_black_24dp
            else -> R.drawable.ic_pause_black_24dp
        }

        return mediaItems.value?.map {
            val useResId = if (it.mediaId == mediaMetadata.id) newResId else NO_RESOURCE
            it.copy(playbackRes = useResId)
        } ?: emptyList()
    }

    private fun getResourceForMediaId(mediaId: String): Int {
        val isActive = mediaId == mediaSessionConnection.mediaMetadata.value?.id
        val isPlaying = mediaSessionConnection.playbackState.value?.isPlaying ?: false
        return when {
            !isActive -> NO_RESOURCE
            isPlaying -> R.drawable.ic_play_arrow_black_24dp
            else -> R.drawable.ic_pause_black_24dp
        }
    }

    class Factory(
        private val context: Context,
        private val mediaId: String,
        private val mediaSessionConnection: MediaSessionConnection
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ArtistFragmentViewModel(context, mediaId, mediaSessionConnection) as T
        }
    }
}

private const val NO_RESOURCE = 0