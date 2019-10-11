package com.truongkhanh.musicapplication.view.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment

class AlbumFragment() : BaseFragment() {

    companion object {
        fun getInstance() = AlbumFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_album, container, false)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
    }
}