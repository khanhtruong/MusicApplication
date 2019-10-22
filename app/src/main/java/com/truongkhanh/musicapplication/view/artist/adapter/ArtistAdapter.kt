package com.truongkhanh.musicapplication.view.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.MediaItemData
import java.util.*

class ArtistAdapter(private val itemClickListener: (MediaItemData) -> Unit) : ListAdapter<MediaItemData, ArtistViewHolder>(MediaItemData.diffCallback), Filterable {
    private var newCurrentList: MutableList<MediaItemData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        newCurrentList = currentList
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
            holder.artist.text = mediaItem.artist

            Glide.with(holder.itemView)
                .load(mediaItem.avatarBitmap)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.avatar)
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
                    val string = mediaItemData.artist
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
            this@ArtistAdapter.submitList(results?.values as MutableList<MediaItemData>)
        }
    }
}