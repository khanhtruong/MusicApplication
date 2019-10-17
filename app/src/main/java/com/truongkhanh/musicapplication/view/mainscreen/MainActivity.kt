package com.truongkhanh.musicapplication.view.mainscreen

import android.media.AudioManager
import android.os.Bundle
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseNoAppBarActivity

class MainActivity : BaseNoAppBarActivity() {

    private lateinit var mainFragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        if (savedInstanceState == null) {
            mainFragment = MainFragment.getInstance()
            if (intent != null) {
                mainFragment.arguments = intent.extras
            }
            setFragment(mainFragment)
        }
    }

    private fun setFragment(mainFragment: MainFragment) {
        replaceFragment(R.id.fragmentContainer, mainFragment)
    }
}
