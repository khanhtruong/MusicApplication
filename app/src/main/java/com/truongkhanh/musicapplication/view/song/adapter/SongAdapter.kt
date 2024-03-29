package com.truongkhanh.musicapplication.view.song.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.model.MediaItemData.Companion.PLAYBACK_RES_CHANGED
import java.util.*

class SongAdapter(private val itemClickListener: (MediaItemData) -> Unit): ListAdapter<MediaItemData, SongViewHolder>(MediaItemData.diffCallback), Filterable {
    private var newCurrentList: MutableList<MediaItemData> = mutableListOf()

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        newCurrentList = currentList
        return SongViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
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
                    PLAYBACK_RES_CHANGED -> {
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

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val keyWord = constraint.toString()
            val listFiltered = if (keyWord.isBlank()) {
                newCurrentList
            } else {
                val filterList = mutableListOf<MediaItemData>()
                newCurrentList.forEach {mediaItemData ->
                    val string = mediaItemData.title
                    if (string.toLowerCase(Locale.ROOT).contains(keyWord)) {
                        filterList.add(mediaItemData)
                    }
                }
                filterList
            }

            val filterResults = FilterResults()
            filterResults.values = listFiltered
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            this@SongAdapter.submitList(results?.values as MutableList<MediaItemData>)
        }
    }
}