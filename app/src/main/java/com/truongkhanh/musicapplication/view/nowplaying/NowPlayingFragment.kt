package com.truongkhanh.musicapplication.view.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.util.getMainFragmentViewModelFactory
import com.truongkhanh.musicapplication.util.getNowPlayingViewModelFactory
import com.truongkhanh.musicapplication.view.mainscreen.MainFragmentViewModel
import kotlinx.android.synthetic.main.fragment_now_playing.*

class NowPlayingFragment : BaseFragment() {

    private lateinit var nowPlayingFragmentViewModel: NowPlayingFragmentViewModel
    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    companion object {
        fun getInstance() = NowPlayingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        bindingViewModel()
    }

    private fun bindingViewModel() {
        val context = activity ?: return
        nowPlayingFragmentViewModel = ViewModelProviders.of(context, getNowPlayingViewModelFactory(context))
            .get(NowPlayingFragmentViewModel::class.java)
        mainFragmentViewModel = ViewModelProviders.of(context, getMainFragmentViewModelFactory(context))
            .get(MainFragmentViewModel::class.java)

        nowPlayingFragmentViewModel.mediaMetadata.observe(this, Observer { nowPlayingMetaData ->
            updateUI(nowPlayingMetaData)
        })
        nowPlayingFragmentViewModel.mediaPosition.observe(this, Observer {currentPosition ->
            updateCurrentPosition(currentPosition)
        })
        nowPlayingFragmentViewModel.buttonPlayResource.observe(this, Observer {buttonResource ->
            btnPlayPause.setImageResource(buttonResource)
        })
    }

    private fun updateCurrentPosition(currentPosition: Long) {
        if (currentPosition > 0) {
            context?.let {context ->
                val currentPositionString =
                    NowPlayingFragmentViewModel.NowPlayingMetadata.timestampToMSS(context, currentPosition)
                tvCurrentPosition.text = currentPositionString
            }
        }
    }

    private fun updateUI(nowPlayingMetadata: NowPlayingFragmentViewModel.NowPlayingMetadata) {
        Glide.with(this)
            .load(nowPlayingMetadata.displayIcon)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(ivSongAvatar)

        tvSongName.text = nowPlayingMetadata.title
        tvSongArtist.text = nowPlayingMetadata.subtitle
        tvTotalPosition.text = nowPlayingMetadata.duration
    }

}