package com.truongkhanh.musicapplication.view.song.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.truongkhanh.musicapplication.model.MediaItemData
import kotlinx.android.synthetic.main.item_song.view.*

class SongViewHolder(view: View, itemClickListener: (MediaItemData) -> Unit) :
    RecyclerView.ViewHolder(view) {

    val name: TextView = view.tvSongName
    val artist: TextView = view.tvSongArtist
    val album: TextView = view.tvSongAlbum
    val avatar: ImageView = view.ivSongAvatar

    var itemData: MediaItemData? = null

    init {
        view.setOnClickListener {
            itemData?.let {
                itemClickListener(it)
            }
        }
    }

//    fun onBind(song: Song) {
//        song.source?.let {
//            val mediaMetaDataRetriever = MediaMetadataRetriever()
//            mediaMetaDataRetriever.setDataSource(it)
//            val songAvatarByteArray = mediaMetaDataRetriever.embeddedPicture
//            if (songAvatarByteArray != null) {
//                val bitmap = BitmapFactory.decodeByteArray(songAvatarByteArray, 0, songAvatarByteArray.size)
//                itemView.ivSongAvatar.setImageBitmap(bitmap)
//            } else {
//                itemView.ivSongAvatar.setImageResource(R.drawable.ic_launcher_foreground)
//            }
//        }
//        itemView.tvSongName.text = song.name
//        itemView.tvSongArtist.text = song.artist
//        itemView.tvSongAlbum.text = song.album
//    }
}