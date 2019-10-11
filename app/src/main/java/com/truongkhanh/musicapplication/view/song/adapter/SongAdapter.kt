package com.truongkhanh.musicapplication.view.song.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.model.Song

class SongAdapter(private val listener: ItemClick): RecyclerView.Adapter<SongViewHolder>() {
    private lateinit var songs: ArrayList<Song>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (songs.size < 0)
            return 0
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        songs.getOrNull(position)?.let{song ->
            holder.onBind(song)
            holder.itemView.setOnClickListener {
                listener.onItemClickEvent(position, song)
            }
        }
    }

    fun setSongs(newSongs: ArrayList<Song>?) {
        songs = newSongs?: arrayListOf()
        notifyDataSetChanged()
    }

    interface ItemClick {
        fun onItemClickEvent(position: Int, song: Song)
    }

}