package com.truongkhanh.musicapplication.view.mainscreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.model.NowPlayingMetadata
import com.truongkhanh.musicapplication.util.*
import com.truongkhanh.musicapplication.view.album.AlbumFragment
import com.truongkhanh.musicapplication.view.artist.ArtistFragment
import com.truongkhanh.musicapplication.view.nowplaying.NowPlayingActivity
import com.truongkhanh.musicapplication.view.song.SongFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment() {

    private lateinit var mainFragmentViewModel: MainFragmentViewModel
    private var isPermissionGranted = false
    private var rootMediaID: String? = null

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_songs -> {
                    if(isPermissionGranted) {
                        rootMediaID?.let {
                            val songFragment = SongFragment.getInstance()
                            val bundle = Bundle()
                            bundle.putString(BUNDLE_MEDIA_ID, ALL_SONG_MAP_KEY)
                            songFragment.arguments = bundle
                            goToFragment(songFragment, BOTTOM_NAVIGATION_TAG, false)
                        }
                    } else {
                        requestStoragePermission()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_artist -> {
                    if(isPermissionGranted) {
                        rootMediaID?.let {
                            val artistFragment = ArtistFragment.getInstance()
                            val bundle = Bundle()
                            bundle.putString(BUNDLE_MEDIA_ID, ARTIST_MAP_KEY)
                            artistFragment.arguments = bundle
                            goToFragment(artistFragment, BOTTOM_NAVIGATION_TAG, false)
                        }
                    } else {
                        requestStoragePermission()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_album -> {
                    if(isPermissionGranted) {
                        rootMediaID?.let {
                            val albumFragment = AlbumFragment.getInstance()
                            val bundle = Bundle()
                            bundle.putString(BUNDLE_MEDIA_ID, ALBUM_MAP_KEY)
                            albumFragment.arguments = bundle
                            goToFragment(albumFragment, BOTTOM_NAVIGATION_TAG, false)
                        }
                    } else {
                        requestStoragePermission()
                    }
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
        bindingViewModel()
        initClickListener()
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
        mainFragmentViewModel = ViewModelProviders
            .of(this, getMainFragmentViewModelFactory(context))
            .get(MainFragmentViewModel::class.java)
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
                    isPermissionGranted = true
                }
                return
            }
        }
    }

    private fun bindingViewModel() {
        mainFragmentViewModel.rootMediaID.observe(this, Observer { rootMediaID ->
            rootMediaID?.let {
                this.rootMediaID = rootMediaID
                val songFragment = SongFragment.getInstance()
                val bundle = Bundle()
                bundle.putString(BUNDLE_MEDIA_ID, ALL_SONG_MAP_KEY)
                songFragment.arguments = bundle
                goToFragment(songFragment, BOTTOM_NAVIGATION_TAG, false)
            }
        })
        mainFragmentViewModel.mediaMetadata.observe(this, Observer { nowPlayingMetaData ->
            if (rlNowPlaying.visibility == View.GONE) {
                rlNowPlaying.visibility = View.VISIBLE
                isNowPlayingVisible = true
            }
            updateUI(nowPlayingMetaData)
        })
        mainFragmentViewModel.buttonPlayResource.observe(this, Observer { resource ->
            btnSongState.setImageResource(resource)
        })
    }

    private fun updateUI(nowPlayingMetadata: NowPlayingMetadata) {
        tvNowPlayingName.text = nowPlayingMetadata.title
        tvNowPlayingArtist.text = nowPlayingMetadata.subtitle
        Glide.with(this)
            .load(nowPlayingMetadata.displayIcon)
            .placeholder(R.drawable.ic_launcher_foreground)
            .apply(RequestOptions.circleCropTransform())
            .into(ivNowPlayingAvatar)
    }

    private fun initClickListener() {
        btnSongState.setOnClickListener {
            mainFragmentViewModel.onClickPlayButton()
        }
        rlNowPlaying.setOnClickListener {
            nowPlayingRootMediaID?.let {
                val intent = Intent(context, NowPlayingActivity::class.java)
                val bundle = Bundle()
                bundle.putString(BUNDLE_MEDIA_ID, it)
                intent.putExtras(bundle)
                context?.startActivity(intent)
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
                requestStoragePermission()
            } else {
                isPermissionGranted = true
            }
        }
    }

    private fun requestStoragePermission() {
        activity?.let {activity ->
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    private fun setUpBottomNavigation() {
        navigationMain.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navigationMain.selectedItemId = R.id.navigation_songs
    }

    private fun goToFragment(fragment: Fragment, tag: String, addToBackStack: Boolean) {
        replaceFragmentWithAnimation(R.id.layoutContainer, fragment, addToBackStack, tag)
    }
}