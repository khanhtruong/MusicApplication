package com.truongkhanh.musicapplication.view.album

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.util.BUNDLE_FILTER
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.FILTER_ALBUM
import com.truongkhanh.musicapplication.util.getAlbumFragmentViewModelFactory
import com.truongkhanh.musicapplication.view.album.adapter.AlbumAdapter
import com.truongkhanh.musicapplication.view.listsong.ListSongActivity
import kotlinx.android.synthetic.main.layout_album.*

class AlbumFragment : BaseFragment() {

    private lateinit var albumFragmentViewModel: AlbumFragmentViewModel

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
        setUpListener()
    }

    private fun setUpListener() {
        textChangeListener()
    }

    private fun textChangeListener() {
        etSearchAlbum.addTextChangedListener {
            albumAdapter?.filter?.filter(etSearchAlbum.text)
        }
    }

    private fun getData() {
        arguments?.let {
            this.mediaID = it.getString(BUNDLE_MEDIA_ID)
        }
    }

    private fun onBindingViewModel() {
        val context = activity ?: return

        mediaID?.let { mediaID ->
            onBindingArtistViewModel(context, mediaID)
        }
    }

    private fun onBindingArtistViewModel(context: FragmentActivity, mediaID: String) {
        albumFragmentViewModel =
            ViewModelProviders.of(context, getAlbumFragmentViewModelFactory(context, mediaID))
                .get(AlbumFragmentViewModel::class.java)

        albumFragmentViewModel.mediaItems.observe(this, Observer { mediaItemList ->
            albumAdapter?.submitList(mediaItemList)
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvAlbums.layoutManager = layoutManager
        albumAdapter = AlbumAdapter { mediaItemData ->
            navigateListSongActivity(mediaItemData.mediaId)
        }
        rvAlbums.adapter = albumAdapter
    }

    private fun navigateListSongActivity(mediaID: String) {
        val intent = Intent(context, ListSongActivity::class.java)
        val bundle = Bundle()
        bundle.putString(BUNDLE_MEDIA_ID, mediaID)
        bundle.putString(BUNDLE_FILTER, FILTER_ALBUM)
        intent.putExtras(bundle)
        context?.startActivity(intent)
    }

}