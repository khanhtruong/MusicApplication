package com.truongkhanh.musicapplication.view.listsong

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.util.BUNDLE_FILTER
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.getListSongFragmentViewModelFactory
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingActivity
import com.truongkhanh.musicapplication.view.song.adapter.SongAdapter
import kotlinx.android.synthetic.main.fragment_list_song.*

class ListSongFragment : BaseFragment() {

    private lateinit var listSongFragmentViewModel: ListSongFragmentViewModel
    private lateinit var bundleFilter: Bundle
    private lateinit var mediaID: String
    private lateinit var songAdapter: SongAdapter

    companion object {
        fun getInstance() = ListSongFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_song, container, false)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        getData()
        bindingViewModel()
        initRecyclerView()
    }

    private fun getData() {
        arguments?.let {
            mediaID = it.getString(BUNDLE_MEDIA_ID)!!
            bundleFilter = Bundle()
            bundleFilter.putString(BUNDLE_FILTER, it.getString(BUNDLE_FILTER))
        }
    }

    private fun bindingViewModel() {
        val activity = activity ?: return
        listSongFragmentViewModel =
            ViewModelProviders.of(activity, getListSongFragmentViewModelFactory(activity, mediaID))
                .get(ListSongFragmentViewModel::class.java)

        listSongFragmentViewModel.navigateToActivity.observe(this, Observer { event ->
            val intent = Intent(context, NowPlayingActivity::class.java)
            val bundle = Bundle()
            bundle.putString(BUNDLE_MEDIA_ID, this.mediaID)
            intent.putExtras(bundle)
            context?.startActivity(intent)
        })
        listSongFragmentViewModel.mediaItems.observe(this, Observer { listItem ->
            listItem.getOrNull(0)?.let {
                updateUI(it)
            }
            songAdapter.submitList(listItem)
        })
    }

    private fun updateUI(mediaItemData: MediaItemData) {
        Glide.with(this)
            .load(mediaItemData.avatarBitmap)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(ivListSongAvatar)
        tvListSongTitle.text = mediaItemData.title
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvListSong.layoutManager = layoutManager
        songAdapter = SongAdapter {
            listSongFragmentViewModel.playByMediaID(it.mediaId, mediaID, bundleFilter)
        }
        rvListSong.adapter = songAdapter
    }

}