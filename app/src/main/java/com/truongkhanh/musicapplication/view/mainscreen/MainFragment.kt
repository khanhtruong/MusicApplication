package com.truongkhanh.musicapplication.view.mainscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.media.GetMusicHelper
import com.truongkhanh.musicapplication.util.BOTTOM_NAVIGATION_TAG
import com.truongkhanh.musicapplication.util.REQUEST_PERMISSION_CODE
import com.truongkhanh.musicapplication.util.getMainFragmentViewModelFactory
import com.truongkhanh.musicapplication.view.album.AlbumFragment
import com.truongkhanh.musicapplication.view.artist.ArtistFragment
import com.truongkhanh.musicapplication.view.song.SongFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment() {

    private lateinit var getMusicHelper: GetMusicHelper
    private lateinit var viewModel: MainFragmentViewModel

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_songs -> {
                    val songFragment = SongFragment.getInstance()
                    val bundle = Bundle()
                    goToFragment(songFragment, BOTTOM_NAVIGATION_TAG, true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_artist -> {
                    val artistFragment = ArtistFragment.getInstance()
                    goToFragment(artistFragment, BOTTOM_NAVIGATION_TAG, true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_album -> {
                    val albumFragment = AlbumFragment.getInstance()
                    goToFragment(albumFragment, BOTTOM_NAVIGATION_TAG, true)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    companion object {
        fun getInstance() = MainFragment()
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        setUpBottomNavigation()
        checkPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders
            .of(this, getMainFragmentViewModelFactory(context))
            .get(MainFragmentViewModel::class.java)
        getMusicHelper = GetMusicHelper(context)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getMusicFromExternal()
                }
                return
            }
        }
    }

    private fun checkPermissions() {
        context?.let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activity?.let {activity ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION_CODE
                    )
                }
            } else {
                getMusicFromExternal()
            }
        }
    }

    private fun setUpBottomNavigation() {
        navigationMain.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navigationMain.selectedItemId = R.id.navigation_songs
    }

    private fun getMusicFromExternal() {
        context?.let { context ->
            viewModel.listMediaMetadata.postValue(getMusicHelper.getMusicFromExternal(context))
        }
    }

    private fun goToFragment(fragment: Fragment, tag: String, addToBackStack: Boolean) {
        replaceFragmentWithAnimation(R.id.layoutContainer, fragment, addToBackStack, tag)
    }
}