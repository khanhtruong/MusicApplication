package com.truongkhanh.musicapplication.media

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.IntDef
import com.truongkhanh.musicapplication.util.BUNDLE_ACTION
import com.truongkhanh.musicapplication.util.album
import com.truongkhanh.musicapplication.util.artist
import com.truongkhanh.musicapplication.util.title

interface MusicSource: Iterable<MediaMetadataCompat> {
    fun load()
    fun whenReady(performAction: (Boolean) -> Unit): Boolean
    fun search(keyWord: String, extras: Bundle): List<MediaMetadataCompat>
}

@IntDef(
    STATE_CREATE,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
)
annotation class State
const val STATE_CREATE = 0
const val STATE_INITIALIZING = 1
const val STATE_INITIALIZED = 2
const val STATE_ERROR = 3

abstract class AbstractMusicSource: MusicSource {
    @State
    var state = STATE_CREATE
        set(value) {
            if (value == STATE_CREATE || value == STATE_INITIALIZING) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {listener ->
                        listener(state == STATE_INITIALIZING)
                    }
                }
            } else {
                field = value
            }
        }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    override fun whenReady(performAction: (Boolean) -> Unit): Boolean =
        when(state) {
            STATE_CREATE, STATE_INITIALIZING -> {
                onReadyListeners += performAction
                false
            }
            else -> {
                performAction(state != STATE_ERROR)
                true
            }
        }

    override fun search(keyWord: String, extras: Bundle): List<MediaMetadataCompat> {
        val results = extras.getString(BUNDLE_ACTION)?.let{action ->
            when (action) {
                ACTION_SEARCH_ARTIST -> {
                   filter {song ->
                       song.artist == keyWord
                   }
                }
                ACTION_SEARCH_ALBUM -> {
                    filter {song ->
                        song.album == keyWord
                    }
                }
                else -> {
                    null
                }
            }
        }
        return if (results.isNullOrEmpty()) {
            if(keyWord.isNotBlank()) {
                filter { song ->
                    song.title == keyWord
                }
            } else {
                shuffled()
            }
        } else {
            results
        }
    }
}

const val ACTION_SEARCH_ARTIST = "actionSearchArtist"
const val ACTION_SEARCH_ALBUM = "actionSearchAlbum"