package com.truongkhanh.musicapplication.view.nowplaying

import android.os.Bundle
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseAppBarActivity

class NowPlayingActivity : BaseAppBarActivity() {
    private lateinit var nowPlayingFragment: NowPlayingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            nowPlayingFragment = NowPlayingFragment.getInstance()
            if (intent != null) {
                nowPlayingFragment.arguments = intent.extras
            }
            setFragment(nowPlayingFragment)
        }
    }

    private fun setFragment(nowPlayingFragment: NowPlayingFragment) {
        replaceFragment(R.id.fragmentContainer, nowPlayingFragment)
    }
}