package com.truongkhanh.musicapplication.util

import android.content.ComponentName
import android.content.Context
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.MusicService
import com.truongkhanh.musicapplication.view.song.SongFragmentViewModel


fun getSongViewModelFactory(context: Context): SongFragmentViewModel.Factory {
    val applicationContext = context.applicationContext
    val mediaSessionConnection = MediaSessionConnection.getInstance(applicationContext,
        ComponentName(applicationContext, MusicService::class.java)
    )
    return SongFragmentViewModel.Factory(mediaSessionConnection)
}