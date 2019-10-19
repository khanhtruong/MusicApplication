package com.truongkhanh.musicapplication.view.song

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.media.Song
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.getMainFragmentViewModelFactory
import com.truongkhanh.musicapplication.util.getSongFragmentViewModelFactory
import com.truongkhanh.musicapplication.view.mainscreen.MainFragmentViewModel
import com.truongkhanh.musicapplication.view.song.adapter.SongAdapter
import kotlinx.android.synthetic.main.layout_songs.*

class SongFragment : BaseFragment() {
    private var songs: ArrayList<Song>? = null
    private var songAdapter: SongAdapter? = null
    private lateinit var mainFragmentViewModel: MainFragmentViewModel
    private lateinit var songFragmentViewModel: SongFragmentViewModel
    private var mediaID: String? = null

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
        getData()
        bindingViewModel()
        initRecyclerView()
    }

    private fun getData() {
        arguments?.let{
            this.mediaID = it.getString(BUNDLE_MEDIA_ID)
        }
    }

    private fun bindingViewModel() {
        val context = activity ?: return
        mainFragmentViewModel =
            ViewModelProviders.of(context, getMainFragmentViewModelFactory(context))
                .get(MainFragmentViewModel::class.java)

        mediaID?.let{ bindingSongFragmentViewModel(context, it) }

        mainFragmentViewModel.navigateToActivity.observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let { activity ->
                val intent = Intent(context, activity::class.java)
                context.startActivity(intent)
            }
        })
    }

    private fun bindingSongFragmentViewModel(context: FragmentActivity, mediaID: String) {
        songFragmentViewModel =
            ViewModelProviders.of(context, getSongFragmentViewModelFactory(context, mediaID))
                .get(SongFragmentViewModel::class.java)

        songFragmentViewModel.mediaItems.observe(this, Observer {mediaItems ->
            songAdapter?.submitList(mediaItems)
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvSongs.layoutManager = layoutManager
        songAdapter = SongAdapter { itemData ->
            mainFragmentViewModel.mediaItemClick(itemData)
        }
        rvSongs.adapter = songAdapter
    }
}