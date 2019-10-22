package com.truongkhanh.musicapplication.view.listsong

import android.os.Bundle
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseAppBarActivity

class ListSongActivity : BaseAppBarActivity() {
    private lateinit var listSongFragment: ListSongFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            listSongFragment = ListSongFragment.getInstance()
            if (intent != null) {
                listSongFragment.arguments = intent.extras
            }
            setFragment(listSongFragment)
        }
        setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(getString(R.string.toolbar_title_list_song))
    }

    private fun setFragment(listSongFragment: ListSongFragment) {
        replaceFragment(R.id.fragmentContainer, listSongFragment)
    }
}