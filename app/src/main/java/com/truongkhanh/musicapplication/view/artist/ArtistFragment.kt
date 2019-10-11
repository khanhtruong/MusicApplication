package com.truongkhanh.musicapplication.view.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment

class ArtistFragment : BaseFragment() {

    companion object {
        fun getInstance() = ArtistFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_artist, container, false)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
    }
}