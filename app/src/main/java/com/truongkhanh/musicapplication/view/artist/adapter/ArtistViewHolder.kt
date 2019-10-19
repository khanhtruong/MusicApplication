package com.truongkhanh.musicapplication.view.artist.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.truongkhanh.musicapplication.model.MediaItemData
import kotlinx.android.synthetic.main.item_artist.view.*

class ArtistViewHolder(private val view: View, itemClickListener: (MediaItemData) -> Unit) : RecyclerView.ViewHolder(view) {

    val artist: TextView = view.tvArtistName
    val avatar: ImageView = view.ivArtistAvatar
    var itemData: MediaItemData? = null

    init {
        view.setOnClickListener {
            itemData?.let{itemData -> itemClickListener(itemData) }
        }
    }
}