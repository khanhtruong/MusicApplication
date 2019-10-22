package com.truongkhanh.musicapplication.view.artist

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
import com.truongkhanh.musicapplication.util.FILTER_ARTIST
import com.truongkhanh.musicapplication.util.getArtistFragmentViewModelFactory
import com.truongkhanh.musicapplication.view.artist.adapter.ArtistAdapter
import com.truongkhanh.musicapplication.view.listsong.ListSongActivity
import com.truongkhanh.musicapplication.view.mainscreen.MainFragmentViewModel
import kotlinx.android.synthetic.main.layout_artist.*

class ArtistFragment : BaseFragment() {

    private lateinit var artistFragmentViewModel: ArtistFragmentViewModel

    private var mediaID: String? = null
    private var artistAdapter: ArtistAdapter? = null

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
        getData()
        initRecyclerView()
        onBindingViewModel()
        setUpListener()
    }

    private fun setUpListener() {
        textChangeListener()
    }

    private fun textChangeListener() {
        etSearchArtist.addTextChangedListener {
            artistAdapter?.filter?.filter(etSearchArtist.text)
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
        artistFragmentViewModel =
            ViewModelProviders.of(context, getArtistFragmentViewModelFactory(context, mediaID))
                .get(ArtistFragmentViewModel::class.java)

        artistFragmentViewModel.mediaItems.observe(this, Observer { mediaItemList ->
            artistAdapter?.submitList(mediaItemList)
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvArtists.layoutManager = layoutManager
        artistAdapter = ArtistAdapter { mediaItemData ->
            navigateListSongActivity(mediaItemData.mediaId)
        }
        rvArtists.adapter = artistAdapter
    }

    private fun navigateListSongActivity(mediaID: String) {
        val intent = Intent(context, ListSongActivity::class.java)
        val bundle = Bundle()
        bundle.putString(BUNDLE_MEDIA_ID, mediaID)
        bundle.putString(BUNDLE_FILTER, FILTER_ARTIST)
        intent.putExtras(bundle)
        context?.startActivity(intent)
    }
}