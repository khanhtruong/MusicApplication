package com.truongkhanh.musicapplication.view.song

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.media.MediaSessionConnection
import com.truongkhanh.musicapplication.media.MusicService
import com.truongkhanh.musicapplication.model.Song
import com.truongkhanh.musicapplication.util.BUNDLE_SONGS
import com.truongkhanh.musicapplication.util.getSongViewModelFactory
import com.truongkhanh.musicapplication.view.song.adapter.SongAdapter
import kotlinx.android.synthetic.main.layout_songs.*

class SongFragment: BaseFragment(), SongAdapter.ItemClick {
    private var songs: ArrayList<Song>? = null
    private var songAdapter: SongAdapter? = null
    private lateinit var viewModel: SongFragmentViewModel

    companion object {
        fun getInstance() = SongFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_songs, container, false)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        getSongs()
        initRecyclerView()
        bindingViewModel()
    }

    private fun bindingViewModel() {
        val context = activity ?: return
        viewModel = ViewModelProviders.of(context, getSongViewModelFactory(context))
            .get(SongFragmentViewModel::class.java)

    }

    private fun getSongs() {
        arguments?.let{
            songs = it.getParcelableArrayList(BUNDLE_SONGS)
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvSongs.layoutManager = layoutManager
        songAdapter = SongAdapter(this)
        songAdapter?.let{
            it.setSongs(songs)
            rvSongs.adapter = it
        }
    }

    override fun onItemClickEvent(position: Int, song: Song) {

    }
}