package com.truongkhanh.musicapplication.view.nowplaying.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.view.nowplaying.viewpager.ButtonsControlFragment
import com.truongkhanh.musicapplication.view.nowplaying.viewpager.ListNowPlayingFragment
import com.truongkhanh.musicapplication.view.nowplaying.viewpager.SongLyricsFragment

class ViewPagerAdapter(private var mediaID: String, fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            LIST_NOW_PLAYING_FRAGMENT -> {
                getListNowPlayingFragment()
            }
            BUTTONS_CONTROL_FRAGMENT -> {
                getUpButtonsControlFragment()
            }
            SONG_LYRICS_FRAGMENT -> {
                getSongLyricsFragment()
            }
            else -> {
                getSongLyricsFragment()
            }
        }
    }

    private fun getSongLyricsFragment(): Fragment {
        val songLyricsFragment = SongLyricsFragment()
        val bundle = Bundle()
        bundle.putString(BUNDLE_MEDIA_ID, mediaID)
        songLyricsFragment.arguments = bundle
        return songLyricsFragment
    }

    private fun getListNowPlayingFragment(): Fragment {
        val nowListPlayingFragment = ListNowPlayingFragment()
        val bundle = Bundle()
        bundle.putString(BUNDLE_MEDIA_ID, mediaID)
        nowListPlayingFragment.arguments = bundle
        return nowListPlayingFragment
    }

    private fun getUpButtonsControlFragment(): Fragment {
        val buttonsControlFragment = ButtonsControlFragment()
        val bundle = Bundle()
        bundle.putString(BUNDLE_MEDIA_ID, mediaID)
        buttonsControlFragment.arguments = bundle
        return buttonsControlFragment
    }

    override fun getCount(): Int = TOTAL_OF_SCREEN_SLIDE

}

const val LIST_NOW_PLAYING_FRAGMENT = 0
const val BUTTONS_CONTROL_FRAGMENT = 1
const val SONG_LYRICS_FRAGMENT = 2
private const val TOTAL_OF_SCREEN_SLIDE = 3