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
    val state: ImageView = view.ivSongState
    val vState: View = view.vSongState

    var itemData: MediaItemData? = null

    init {
        view.setOnClickListener {
            itemData?.let {
                itemClickListener(it)
            }
        }
    }
}