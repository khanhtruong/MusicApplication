package com.truongkhanh.musicapplication.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.media.MusicSource

class BrowseTree (private var context: Context, musicSource: MusicSource) {
    private val musicRootMap = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    init {
        val rootList = musicRootMap[ROOT_MAP_KEY] ?: mutableListOf()

        val allSongList = MediaMetadataCompat.Builder().apply {
            id = ALL_SONG_MAP_KEY
            title = context.getString(R.string.lbl_all_song_map_key_title)
            albumArtUri = context.resources.getResourceEntryName(R.drawable.ic_launcher_foreground)
            flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        }.build()

        val artistList = MediaMetadataCompat.Builder().apply {
            id = ARTIST_MAP_KEY
            title = context.getString(R.string.lbl_artist_map_key_title)
            albumArtUri = context.resources.getResourceEntryName(R.drawable.ic_launcher_foreground)
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        val albumList = MediaMetadataCompat.Builder().apply {
            id = ALBUM_MAP_KEY
            title = context.getString(R.string.lbl_album_map_key_title)
            albumArtUri = context.resources.getResourceEntryName(R.drawable.ic_launcher_foreground)
            flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        rootList += allSongList
        rootList += artistList
        rootList += albumList

        musicRootMap[ROOT_MAP_KEY] = rootList

        mappingAllSong(musicSource)
        mappingArtist(musicSource)
        mappingAlbum(musicSource)
    }

    operator fun get(mediaId: String) = musicRootMap[mediaId]

    private fun mappingAllSong(musicSource: MusicSource) {
        musicSource.forEach {mediaMetaData ->
            val listSong = musicRootMap[ALL_SONG_MAP_KEY] ?: mutableListOf<MediaMetadataCompat>().also {
                musicRootMap[ALL_SONG_MAP_KEY] = it
            }
            listSong += mediaMetaData
        }
    }

    private fun mappingAlbum(musicSource: MusicSource) {
        musicSource.forEach {mediaMetaData ->
            val album = mediaMetaData.displayDescription.urlEncoded
            val albumList = musicRootMap[album] ?:buildAlbumRoot(mediaMetaData)
            albumList += mediaMetaData
        }
    }

    private fun mappingArtist(musicSource: MusicSource) {
        musicSource.forEach {mediaMetaData ->
            val artist = mediaMetaData.artist.urlEncoded
            val artistList = musicRootMap[artist] ?: buildArtistRoot(mediaMetaData)
            artistList += mediaMetaData
        }
    }

    private fun buildAlbumRoot(mediaItem: MediaMetadataCompat) : MutableList<MediaMetadataCompat> {
        val bitmap = getDisplayIcon(context, mediaItem.mediaUri)
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.displayDescription.urlEncoded
            title = mediaItem.displayDescription
            album = mediaItem.displayDescription
            displayDescription = mediaItem.displayDescription
            displaySubtitle = mediaItem.displaySubtitle
            displayTitle = mediaItem.displayTitle
            artist = mediaItem.artist
            displayIcon = bitmap
        }.build()

        val rootList = musicRootMap[ALBUM_MAP_KEY] ?: mutableListOf()
        rootList += albumMetadata
        musicRootMap[ALBUM_MAP_KEY] = rootList

        return mutableListOf<MediaMetadataCompat>().also {
            musicRootMap[albumMetadata.id!!] = it
        }
    }

    private fun buildArtistRoot(mediaItem: MediaMetadataCompat) : MutableList<MediaMetadataCompat> {
        val bitmap = getDisplayIcon(context, mediaItem.mediaUri)
        val artistMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.artist.urlEncoded
            title = mediaItem.artist
            artist = mediaItem.artist
            displayDescription = mediaItem.displayDescription
            displaySubtitle = mediaItem.displaySubtitle
            displayTitle = mediaItem.displayTitle
            album = mediaItem.displayDescription
            displayIcon = bitmap
        }.build()

        val rootList = musicRootMap[ARTIST_MAP_KEY] ?: mutableListOf()
        rootList += artistMetadata
        musicRootMap[ARTIST_MAP_KEY] = rootList

        return mutableListOf<MediaMetadataCompat>().also {
            musicRootMap[artistMetadata.id!!] = it
        }
    }

    private fun getDisplayIcon(context: Context, uri: Uri): Bitmap? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, uri)
        val byteArray = mediaMetadataRetriever.embeddedPicture
        return if (byteArray != null) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
        }
    }
}

const val ROOT_MAP_KEY = "rootMapKey"
const val ALL_SONG_MAP_KEY = "allSongMapKey"
const val ARTIST_MAP_KEY = "artistMapKey"
const val ALBUM_MAP_KEY = "albumMapKey"