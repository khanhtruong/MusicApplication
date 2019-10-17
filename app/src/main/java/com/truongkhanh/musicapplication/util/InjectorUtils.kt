package com.truongkhanh.musicapplication.util

import android.app.Application
import android.content.ComponentName
import android.content.Context
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.MusicService
import com.truongkhanh.musicapplication.view.mainscreen.MainFragmentViewModel
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingFragmentViewModel
import com.truongkhanh.musicapplication.view.song.SongFragmentViewModel

fun getNowPlayingViewModelFactory(context: Context): NowPlayingFragmentViewModel.Factory {
    val applicationContext = context.applicationContext
    val mediaSessionConnection = MediaSessionConnection.getInstance(applicationContext,
        ComponentName(applicationContext, MusicService::class.java)
    )
    return NowPlayingFragmentViewModel.Factory(applicationContext as Application, mediaSessionConnection)
}

fun getMainFragmentViewModelFactory(context: Context): MainFragmentViewModel.Factory {
    val applicationContext = context.applicationContext
    val mediaSessionConnection = MediaSessionConnection.getInstance(applicationContext,
        ComponentName(applicationContext, MusicService::class.java)
    )
    return MainFragmentViewModel.Factory(mediaSessionConnection)
}

fun getSongFragmentViewModelFactory(context: Context, mediaID: String): SongFragmentViewModel.Factory {
    val applicationContext = context.applicationContext
    val mediaSessionConnection = MediaSessionConnection.getInstance(applicationContext,
        ComponentName(applicationContext, MusicService::class.java)
    )
    return SongFragmentViewModel.Factory(context, mediaID, mediaSessionConnection)
}