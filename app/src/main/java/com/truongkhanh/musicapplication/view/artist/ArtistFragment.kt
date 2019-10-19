package com.truongkhanh.musicapplication.view.artist

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
import com.truongkhanh.musicapplication.media.ACTION_SEARCH_ARTIST
import com.truongkhanh.musicapplication.model.MediaItemData
import com.truongkhanh.musicapplication.util.BUNDLE_MEDIA_ID
import com.truongkhanh.musicapplication.util.getArtistFragmentViewModelFactory
import com.truongkhanh.musicapplication.util.getMainFragmentViewModelFactory
import com.truongkhanh.musicapplication.view.artist.adapter.ArtistAdapter
import com.truongkhanh.musicapplication.view.mainscreen.MainFragmentViewModel
import kotlinx.android.synthetic.main.layout_artist.*

class ArtistFragment : BaseFragment() {

    private lateinit var artistFragmentViewModel: ArtistFragmentViewModel
    private lateinit var mainFragmentViewModel: MainFragmentViewModel

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
            artistFragmentViewModel =
                ViewModelProviders.of(context, getArtistFragmentViewModelFactory(context, it))
                    .get(ArtistFragmentViewModel::class.java)

            artistFragmentViewModel.mediaItems.observe(this, Observer { mediaItemList ->
                if (mediaItemList.isNotEmpty()) {
                    artistAdapter?.submitList(getOnlyArtists(mediaItemList))
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

    private fun getOnlyArtists(mediaItemList: List<MediaItemData>): List<MediaItemData> {
        val map = mutableMapOf<String, MediaItemData>()
        mediaItemList.forEach {itemData ->
            if(!map.containsKey(itemData.subtitle))
                map[itemData.subtitle] = itemData
        }
        return map.values.toList()
    }


    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvArtists.layoutManager = layoutManager
        artistAdapter = ArtistAdapter { mediaItemData ->
            mainFragmentViewModel.playMediaBySearch(mediaItemData.subtitle, ACTION_SEARCH_ARTIST)
        }
        rvArtists.adapter = artistAdapter
    }
}