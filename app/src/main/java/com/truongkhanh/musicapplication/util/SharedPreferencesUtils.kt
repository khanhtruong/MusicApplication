package com.truongkhanh.musicapplication.util

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.edit

private const val SHARED_PREFERENCES_KEY = "SharedPreferencesKey"
private const val REPEAT_MODE_KEY = "RepeatModeKey"
private const val SHUFFLE_MODE_KEY = "ShuffleModeKey"
private const val DEFAULT_REPEAT_MODE = PlaybackStateCompat.REPEAT_MODE_ALL
private const val DEFAULT_SHUFFLE_MODE = PlaybackStateCompat.SHUFFLE_MODE_ALL

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
}

fun setRepeatMode(sharedPreferences: SharedPreferences, repeatMode: Int) {
    sharedPreferences.edit { 
        putInt(REPEAT_MODE_KEY, repeatMode)
        commit()
    }
}

fun getRepeatMode(sharedPreferences: SharedPreferences): Int {
    return sharedPreferences.getInt(REPEAT_MODE_KEY, DEFAULT_REPEAT_MODE)
}

fun setShuffleMode(sharedPreferences: SharedPreferences, shuffleMode: Int) {
    sharedPreferences.edit {
        putInt(SHUFFLE_MODE_KEY, shuffleMode)
        commit()
    }
}

fun getShuffleMode(sharedPreferences: SharedPreferences): Int {
    return sharedPreferences.getInt(SHUFFLE_MODE_KEY, DEFAULT_SHUFFLE_MODE)
}