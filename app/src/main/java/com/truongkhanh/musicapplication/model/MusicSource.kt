package com.truongkhanh.musicapplication.model

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.IntDef

interface MusicSource: Iterable<MediaMetadataCompat> {
    fun load()
    fun whenReady(performAction: (Boolean) -> Unit): Boolean
//    fun search(keyWord: String, extras: Bundle): List<MediaMetadataCompat>
}

@IntDef(STATE_CREATE, STATE_INITIALIZING, STATE_INITIALIZED, STATE_ERROR)
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

}