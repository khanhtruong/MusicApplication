package com.truongkhanh.musicapplication.view.mainscreen

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.view.song.SongFragmentViewModel

class MainFragmentViewModel(mediaSessionConnection: MediaSessionConnection): ViewModel() {

    val listMediaMetadata: MutableLiveData<List<MediaMetadataCompat>> by lazy {
        MutableLiveData<List<MediaMetadataCompat>>()
    }

    class Factory(private var mediaSessionConnection: MediaSessionConnection): ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SongFragmentViewModel(mediaSessionConnection) as T
        }
    }
}