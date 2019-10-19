package com.truongkhanh.musicapplication.view.album.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.truongkhanh.musicapplication.model.MediaItemData
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumViewHolder(view: View, itemClickListener: (MediaItemData) -> Unit) : RecyclerView.ViewHolder(view) {

    val album: TextView = view.tvAlbumName
    val albumAvatar: ImageView = view.ivAlbumAvatar
    var itemData: MediaItemData? = null

    init {
        view.setOnClickListener {
            itemData?.let { itemClickListener(it) }
        }
    }
}