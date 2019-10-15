package com.truongkhanh.musicapplication.view.song.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.model.MediaItemData.Companion.PLAYBACK_RES_CHANGED

class SongAdapter(private val itemClickListener: (MediaItemData) -> Unit): ListAdapter<MediaItemData, SongViewHolder>(MediaItemData.diffCallback) {

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        val mediaItem = getItem(position)
        var fullRefresh = payloads.isEmpty()

        if (payloads.isNotEmpty()) {
            payloads.forEach { payload ->
                when (payload) {
                    PLAYBACK_RES_CHANGED -> {
                        holder.avatar.setImageResource(mediaItem.playbackRes)
                    }
                    else -> fullRefresh = true
                }
            }
        }

        if (fullRefresh) {
            holder.itemData = mediaItem
            holder.name.text = mediaItem.title
            holder.artist.text = mediaItem.subtitle
            holder.album.text = mediaItem.album

            Glide.with(holder.avatar)
                .load(mediaItem.albumArtUri)
                .into(holder.avatar)
        }
    }
}