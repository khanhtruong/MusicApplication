package com.truongkhanh.musicapplication.view.song.adapter

import com.truongkhanh.musicapplication.model.Song
import android.media.MediaMetadataRetriever
import android.graphics.BitmapFactory
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.truongkhanh.musicapplication.R
import kotlinx.android.synthetic.main.item_song.view.*

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun onBind(song: Song) {
        song.directory?.let {
            val mediaMetaDataRetriever = MediaMetadataRetriever()
            mediaMetaDataRetriever.setDataSource(it)
            val songAvatarByteArray = mediaMetaDataRetriever.embeddedPicture
            if (songAvatarByteArray != null) {
                val bitmap = BitmapFactory.decodeByteArray(songAvatarByteArray, 0, songAvatarByteArray.size)
                itemView.ivSongAvatar.setImageBitmap(bitmap)
            } else {
                itemView.ivSongAvatar.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
        itemView.tvSongName.text = song.name
        itemView.tvSongArtist.text = song.artist
        itemView.tvSongAlbum.text = song.album
    }
}