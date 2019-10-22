package com.truongkhanh.musicapplication.view.nowplaying

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.model.NowPlayingMetadata
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.getNowPlayingViewModelFactory
import com.truongkhanh.musicapplication.util.getSharedPreferences
import com.truongkhanh.musicapplication.view.nowplaying.adapter.BUTTONS_CONTROL_FRAGMENT
import com.truongkhanh.musicapplication.view.nowplaying.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_now_playing.*

class NowPlayingFragment : BaseFragment() {

    private lateinit var nowPlayingFragmentViewModel: NowPlayingFragmentViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaID: String
    private lateinit var viewPagerAdapter: ViewPagerAdapter

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = getSharedPreferences(context)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        getData()
        bindingViewModel()
        fragmentManager?.let { initViewPager(it) }
    }

    private fun getData() {
        arguments?.let{
            mediaID = it.getString(BUNDLE_MEDIA_ID)!!
        }
    }

    private fun bindingViewModel() {
        val context = activity ?: return
        nowPlayingFragmentViewModel =
            ViewModelProviders.of(context, getNowPlayingViewModelFactory(context, mediaID))
                .get(NowPlayingFragmentViewModel::class.java)

        nowPlayingFragmentViewModel.mediaMetadata.observe(this, Observer { nowPlayingMetaData ->
            updateUI(nowPlayingMetaData)
        })
    }

    private fun initViewPager(fragmentManager: FragmentManager) {
        viewPagerAdapter = ViewPagerAdapter(mediaID, fragmentManager)
        vpNowPlaying.adapter = viewPagerAdapter
        vpNowPlaying.setCurrentItem(BUTTONS_CONTROL_FRAGMENT, false)
    }

    private fun updateUI(nowPlayingMetadata: NowPlayingMetadata) {
        Glide.with(this)
            .load(nowPlayingMetadata.displayIcon)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(ivSongAvatar)
    }

}