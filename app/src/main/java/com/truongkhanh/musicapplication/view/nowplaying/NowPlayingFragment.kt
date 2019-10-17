package com.truongkhanh.musicapplication.view.nowplaying

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.truongkhanh.musicapplication.R
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.musicapplication.util.*
import com.truongkhanh.musicapplication.view.mainscreen.MainFragmentViewModel
import kotlinx.android.synthetic.main.fragment_now_playing.*

class NowPlayingFragment : BaseFragment() {

    private lateinit var nowPlayingFragmentViewModel: NowPlayingFragmentViewModel
    private lateinit var mainFragmentViewModel: MainFragmentViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private var isAllowUpdateSeekBar = true
    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            isAllowUpdateSeekBar = false
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            seekBar?.progress?.let { newProgress ->
                nowPlayingFragmentViewModel.updatePosition(newProgress.toLong())
            }
            isAllowUpdateSeekBar = true
        }
    }

    companion object {
        fun getInstance() = NowPlayingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = getSharedPreferences(context)
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        bindingViewModel()
        initListener()
    }

    private fun bindingViewModel() {
        val context = activity ?: return
        nowPlayingFragmentViewModel =
            ViewModelProviders.of(context, getNowPlayingViewModelFactory(context))
                .get(NowPlayingFragmentViewModel::class.java)
        mainFragmentViewModel =
            ViewModelProviders.of(context, getMainFragmentViewModelFactory(context))
                .get(MainFragmentViewModel::class.java)

        nowPlayingFragmentViewModel.mediaMetadata.observe(this, Observer { nowPlayingMetaData ->
            updateUI(nowPlayingMetaData)
        })
        nowPlayingFragmentViewModel.mediaPosition.observe(this, Observer { currentPosition ->
            updateCurrentPosition(currentPosition)
            if (isAllowUpdateSeekBar)
                sbPosition.progress = currentPosition.toInt()
        })
        nowPlayingFragmentViewModel.totalPosition.observe(this, Observer { maxPosition ->
            sbPosition.max = maxPosition.toInt()
        })
        nowPlayingFragmentViewModel.buttonPlayResource.observe(this, Observer { buttonResource ->
            btnPlayPause.setImageResource(buttonResource)
        })
        nowPlayingFragmentViewModel.buttonNextEnable.observe(this, Observer { enable ->
            btnNext.isEnabled = enable
            btnNext.setColorFilter(getButtonColor(enable))
        })
        nowPlayingFragmentViewModel.buttonPreviousEnable.observe(this, Observer { enable ->
            btnPrevious.isEnabled = enable
            btnPrevious.setColorFilter(getButtonColor(enable))
        })
    }

    private fun initListener() {
        seekBarListener()
        buttonListener()
    }

    private fun seekBarListener() {
        sbPosition.setOnSeekBarChangeListener(seekBarChangeListener)
    }

    private fun buttonListener() {
        btnRepeat.setOnClickListener {
            var repeatMode = getRepeatMode(sharedPreferences)
            repeatMode = when (repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ALL -> {
                    PlaybackStateCompat.REPEAT_MODE_ONE
                }
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    PlaybackStateCompat.REPEAT_MODE_NONE
                }
                else -> {
                    PlaybackStateCompat.REPEAT_MODE_ALL
                }
            }
            nowPlayingFragmentViewModel.changeRepeatMode(repeatMode)
            setRepeatMode(sharedPreferences, repeatMode)
            context?.let { context ->
                btnRepeat.setImageDrawable(
                    getRepeatDrawable(
                        repeatMode,
                        context
                    )
                )
            }
        }
        btnShuffle.setOnClickListener {
            var shuffleMode = getShuffleMode(sharedPreferences)
            shuffleMode = when (shuffleMode) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                    PlaybackStateCompat.SHUFFLE_MODE_NONE
                }
                else -> {
                    PlaybackStateCompat.SHUFFLE_MODE_ALL
                }
            }
            nowPlayingFragmentViewModel.changeShuffleMode(shuffleMode)
            setShuffleMode(sharedPreferences, shuffleMode)
            btnShuffle.setColorFilter(getShuffleColor(shuffleMode))
        }
        btnPlayPause.setOnClickListener {
            nowPlayingFragmentViewModel.playOrPause()
        }
        btnNext.setOnClickListener {
            nowPlayingFragmentViewModel.playNext()
        }
        btnPrevious.setOnClickListener {
            nowPlayingFragmentViewModel.playPrevious()
        }
    }

    private fun updateCurrentPosition(currentPosition: Long) {
        if (currentPosition > 0) {
            context?.let { context ->
                val currentPositionString =
                    NowPlayingFragmentViewModel.NowPlayingMetadata.timestampToMSS(
                        context,
                        currentPosition
                    )
                tvCurrentPosition.text = currentPositionString
            }
        }
    }

    private fun updateUI(nowPlayingMetadata: NowPlayingFragmentViewModel.NowPlayingMetadata) {
        Glide.with(this)
            .load(nowPlayingMetadata.displayIcon)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(ivSongAvatar)

        tvSongName.text = nowPlayingMetadata.title
        tvSongArtist.text = nowPlayingMetadata.subtitle
        tvTotalPosition.text = nowPlayingMetadata.duration
    }

}