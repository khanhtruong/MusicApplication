package com.truongkhanh.musicapplication.view.album.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.MediaItemData

class AlbumAdapter (private val itemClickListener: (MediaItemData) -> Unit) : ListAdapter<MediaItemData, AlbumViewHolder>(
    MediaItemData.diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {

    }

    override fun onBindViewHolder(
        holder: AlbumViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val mediaItem = getItem(position)
        var fullRefresh = payloads.isEmpty()

        if (payloads.isNotEmpty()) {
            fullRefresh = true
        }

        if (fullRefresh) {
            holder.itemData = mediaItem
            holder.album.text = mediaItem.album

            Glide.with(holder.itemView)
                .load(mediaItem.avatarBitmap)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.albumAvatar)
        }
    }
}