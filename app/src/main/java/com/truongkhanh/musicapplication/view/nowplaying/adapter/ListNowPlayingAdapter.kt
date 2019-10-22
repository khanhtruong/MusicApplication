package com.truongkhanh.musicapplication.view.nowplaying.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.MediaItemData

class ListNowPlayingAdapter(private val itemClickListener: (Long) -> Unit): ListAdapter<MediaItemData, ListNowPlayingViewHolder>(
    MediaItemData.diffCallback) {
    private var newCurrentList: MutableList<MediaItemData> = mutableListOf()

    override fun onBindViewHolder(holder: ListNowPlayingViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNowPlayingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        newCurrentList = currentList
        return ListNowPlayingViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(
        holder: ListNowPlayingViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val mediaItem = getItem(position)
        var fullRefresh = payloads.isEmpty()

        when(mediaItem.playbackRes) {
            0 -> holder.vState.visibility = View.GONE
            else -> holder.vState.visibility = View.VISIBLE
        }

        if (payloads.isNotEmpty()) {
            payloads.forEach { payload ->
                when (payload) {
                    MediaItemData.PLAYBACK_RES_CHANGED -> {
                        Glide.with(holder.itemView)
                            .load(mediaItem.playbackRes)
                            .into(holder.state)
                    }
                    else -> fullRefresh = true
                }
            }
        }

        if (fullRefresh) {
            holder.itemData = mediaItem
            holder.name.text = mediaItem.title
            holder.artist.text = mediaItem.artist
            holder.album.text = mediaItem.album

            Glide.with(holder.itemView)
                .load(mediaItem.avatarBitmap)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.avatar)

            Glide.with(holder.itemView)
                .load(mediaItem.playbackRes)
                .into(holder.state)
        }
    }
}