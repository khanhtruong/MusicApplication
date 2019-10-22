package com.truongkhanh.musicapplication.view.nowplaying.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.model.NowPlayingMetadata
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.getNowPlayingViewModelFactory
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingFragmentViewModel
import kotlinx.android.synthetic.main.fragment_song_lyrics.*

class SongLyricsFragment : BaseFragment() {
    private lateinit var nowPlayingFragmentViewModel: NowPlayingFragmentViewModel
    private lateinit var mediaID: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song_lyrics, container, false)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        getData()
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

        nowPlayingFragmentViewModel.mediaMetadata.observe(this, Observer {nowPlayingMetaData ->
            updateUI(nowPlayingMetaData)
        })
    }

    private fun updateUI(nowPlayingMetaData: NowPlayingMetadata) {
        tvSongTitle.text = nowPlayingMetaData.title
    }

}