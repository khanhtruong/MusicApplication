package com.truongkhanh.musicapplication.view.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truongkhanh.musicapplication.media.MediaSessionConnection

class SongFragmentViewModel(mediaSessionConnection: MediaSessionConnection): ViewModel() {



    class Factory(private var mediaSessionConnection: MediaSessionConnection): ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SongFragmentViewModel(mediaSessionConnection) as T
        }
    }
}