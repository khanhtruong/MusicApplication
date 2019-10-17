package com.truongkhanh.musicapplication.media

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import android.provider.MediaStore
import android.support.v4.media.MediaDescriptionCompat
import androidx.core.net.toUri
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.AbstractMusicSource
import com.truongkhanh.musicapplication.model.STATE_ERROR
import com.truongkhanh.musicapplication.model.STATE_INITIALIZED
import com.truongkhanh.musicapplication.model.STATE_INITIALIZING
import com.truongkhanh.musicapplication.util.*
import java.util.concurrent.TimeUnit


class GetMusicHelper(private val context: Context): AbstractMusicSource() {

    private var listMusic: List<MediaMetadataCompat> = emptyList()

    init {
        state = STATE_INITIALIZING
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = listMusic.iterator()

    override fun load() {
        getMusicFromExternal(context)?.let{listMusic ->
            this.listMusic = listMusic
            state = STATE_INITIALIZED
        } ?: run {
            this.listMusic = emptyList()
            state = STATE_ERROR
        }
    }

    fun getMusicFromExternal(context: Context): List<MediaMetadataCompat>? {
        val externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = context.contentResolver.query(externalUri, null, selection, null, null)
        if (cursor != null) {
            val songs = arrayListOf<Song>()

            if (cursor.moveToFirst()) {
                do {
                    val id =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val uri =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val album =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    songs.add(Song(id, name, artist, album, uri))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return songs.map { song ->
                MediaMetadataCompat.Builder()
                    .from(context, song)
                    .build()
            }.toList()
        }
        return null
    }
}

data class Song(
    var id: String?,
    var title: String?,
    var artist: String?,
    var album: String?,
    var source: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeString(source)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}

fun MediaMetadataCompat.Builder.from(context: Context, song: Song): MediaMetadataCompat.Builder {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(context, song.source?.toUri())

    val mDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

    val byteArray = mediaMetadataRetriever.embeddedPicture
    displayIcon = if (byteArray != null) {
        val imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        imageBitmap
    } else {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
        imageBitmap
    }
    displayIconUri = getUriToResource(context, R.drawable.ic_launcher_foreground).toString()


    song.id?.let {id = it}
    song.artist?.let {
        artist = it
        displaySubtitle = it
    }
    song.album?.let {
        album = it
        displayDescription = it
    }
    song.source?.let {mediaUri = it}
    song.title?.let {
        title = it
        displayTitle = it
    }
    duration = TimeUnit.SECONDS.toMillis(mDuration.toLong())

    downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED

    return this
}