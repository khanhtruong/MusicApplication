package com.truongkhanh.musicapplication.view.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.MediaItemData

class ArtistAdapter(private val itemClickListener: (MediaItemData) -> Unit) : ListAdapter<MediaItemData, ArtistViewHolder>(MediaItemData.diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onBindViewHolder(
        holder: ArtistViewHolder,
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
            holder.artist.text = mediaItem.subtitle

            Glide.with(holder.itemView)
                .load(mediaItem.avatarBitmap)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.avatar)
        }
    }
}