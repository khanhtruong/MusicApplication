package com.truongkhanh.musicapplication.media

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import android.provider.MediaStore
import androidx.core.net.toUri
import com.truongkhanh.musicapplication.model.AbstractMusicSource


class GetMusicHelper(private val context: Context): AbstractMusicSource() {

    private var listMusic: List<MediaMetadataCompat> = emptyList()

    override fun load() {
        getMusicFromExternal(context)
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = listMusic.iterator()

    private fun getMusicFromExternal(context: Context): List<MediaMetadataCompat>? {
        val externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = context.contentResolver.query(externalUri, null, selection, null, null)
        if (cursor != null) {
            val songs = arrayListOf<mSong>()

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
                    songs.add(mSong(id, name, artist, album, uri))
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

    data class mSong(
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

        companion object CREATOR : Parcelable.Creator<mSong> {
            override fun createFromParcel(parcel: Parcel): mSong {
                return mSong(parcel)
            }

            override fun newArray(size: Int): Array<mSong?> {
                return arrayOfNulls(size)
            }
        }
    }
}

fun MediaMetadataCompat.Builder.from(context: Context, song: GetMusicHelper.mSong): MediaMetadataCompat.Builder {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(context, song.source?.toUri())



    val mDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

    val byteArray = mediaMetadataRetriever.embeddedPicture
    val imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)



    return this
}