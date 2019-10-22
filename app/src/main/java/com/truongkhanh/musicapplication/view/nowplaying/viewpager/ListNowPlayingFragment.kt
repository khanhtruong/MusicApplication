package com.truongkhanh.musicapplication.view.nowplaying.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.getNowPlayingViewModelFactory
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingFragmentViewModel
import com.truongkhanh.musicapplication.view.nowplaying.adapter.ListNowPlayingAdapter
import kotlinx.android.synthetic.main.fragment_now_playing_list.*

class ListNowPlayingFragment : BaseFragment() {
    private lateinit var nowPlayingFragmentViewModel: NowPlayingFragmentViewModel
    private lateinit var mediaID: String
    private lateinit var listNowPlayingAdapter: ListNowPlayingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing_list, container, false)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        getData()
        initRecyclerView()
        bindingViewModel()
    }

    private fun getData() {
        arguments?.let{
            mediaID = it.getString(BUNDLE_MEDIA_ID)!!
        }
    }

    private fun bindingViewModel() {
        val activity = activity?:return
        nowPlayingFragmentViewModel = ViewModelProviders.of(activity, getNowPlayingViewModelFactory(activity, mediaID))
            .get(NowPlayingFragmentViewModel::class.java)

        nowPlayingFragmentViewModel.mediaItems.observe(this, Observer {listItem ->
            listNowPlayingAdapter.submitList(listItem)
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvNowPlayingList.layoutManager = layoutManager
        listNowPlayingAdapter = ListNowPlayingAdapter {id ->
            nowPlayingFragmentViewModel.sipToQueueItem(id)
        }
        rvNowPlayingList.adapter = listNowPlayingAdapter
    }

}