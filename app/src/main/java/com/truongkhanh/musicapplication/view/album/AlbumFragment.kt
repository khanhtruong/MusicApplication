package com.truongkhanh.musicapplication.view.album

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.media.ACTION_SEARCH_ALBUM
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.getAlbumFragmentViewModelFactory
import com.truongkhanh.musicapplication.util.getMainFragmentViewModelFactory
import com.truongkhanh.musicapplication.view.album.adapter.AlbumAdapter
import com.truongkhanh.musicapplication.view.mainscreen.MainFragmentViewModel
import kotlinx.android.synthetic.main.layout_album.*

class AlbumFragment() : BaseFragment() {

    private lateinit var albumFragmentViewModel: AlbumFragmentViewModel
    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    private var mediaID: String? = null
    private var albumAdapter: AlbumAdapter? = null

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
        getData()
        initRecyclerView()
        onBindingViewModel()
    }

    private fun getData() {
        arguments?.let {
            this.mediaID = it.getString(BUNDLE_MEDIA_ID)
        }
    }

    private fun onBindingViewModel() {
        val context = activity ?: return

        mainFragmentViewModel =
            ViewModelProviders.of(context, getMainFragmentViewModelFactory(context))
                .get(MainFragmentViewModel::class.java)

        onBindingArtistViewModel(context)
    }

    private fun onBindingArtistViewModel(context: FragmentActivity) {
        mediaID?.let {
            albumFragmentViewModel =
                ViewModelProviders.of(context, getAlbumFragmentViewModelFactory(context, it))
                    .get(AlbumFragmentViewModel::class.java)

            albumFragmentViewModel.mediaItems.observe(this, Observer { mediaItemList ->
                if (mediaItemList.isNotEmpty()) {
                    albumAdapter?.submitList(getOnlyAlbums(mediaItemList))
                }
            })

            mainFragmentViewModel.navigateToActivity.observe(this, Observer { event ->
                event?.getContentIfNotHandled()?.let { activity ->
                    val intent = Intent(context, activity::class.java)
                    context.startActivity(intent)
                }
            })
        }
    }

    private fun getOnlyAlbums(mediaItemList: List<MediaItemData>): List<MediaItemData> {
        val map = mutableMapOf<String, MediaItemData>()
        mediaItemList.forEach {itemData ->
            if(!map.containsKey(itemData.album))
                map[itemData.album] = itemData
        }
        return map.values.toList()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvAlbums.layoutManager = layoutManager
        albumAdapter = AlbumAdapter { mediaItemData ->
            mainFragmentViewModel.playMediaBySearch(mediaItemData.subtitle, ACTION_SEARCH_ALBUM)
        }
        rvAlbums.adapter = albumAdapter
    }
}