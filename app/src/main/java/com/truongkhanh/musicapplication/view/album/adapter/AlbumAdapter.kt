package com.truongkhanh.musicapplication.view.album.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.MediaItemData
import java.util.*

class AlbumAdapter (private val itemClickListener: (MediaItemData) -> Unit) : ListAdapter<MediaItemData, AlbumViewHolder>(
    MediaItemData.diffCallback), Filterable {
    private var newCurrentList: MutableList<MediaItemData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        newCurrentList = currentList
        return AlbumViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
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

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val keyWord = constraint.toString()
            val listFiltered = if (keyWord.isBlank()) {
                newCurrentList
            } else {
                val filterList = mutableListOf<MediaItemData>()
                newCurrentList.forEach {mediaItemData ->
                    val string = mediaItemData.album
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
            this@AlbumAdapter.submitList(results?.values as MutableList<MediaItemData>)
        }
    }
}