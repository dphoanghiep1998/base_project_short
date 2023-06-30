package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentScreenAcceptBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import java.util.Locale


class FragmentScreenAccept : Fragment() {

    private lateinit var binding: FragmentScreenAcceptBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var mPlayer: Player? = null
    private var countDownTimer: CountDownTimer? = null
    private var mTimeLeftInMillis: Long = 0
    private var timeRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScreenAcceptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (viewModel.getPlayer() == null) {
            mPlayer = ExoPlayer.Builder(requireContext()).setSeekForwardIncrementMs(15000).build()
            binding.playerView.apply {
                player = mPlayer
                keepScreenOn = true

            }
            viewModel.setupPlayer(mPlayer!!)

        } else {
            binding.playerView.apply {
                player = viewModel.getPlayer()
                keepScreenOn = true
            }
        }

        initButton()
        viewModel.playAudio(MediaItem.fromUri("https://github.com/ConfigNeko/FakeWC/raw/main/lv_38.mp4"),
            onEnd = {
                navigateToPage(R.id.fragmentScreenAccept, R.id.fragmentCallClose)
            },
            onPrepareDone = {
                startTimer(it)

            })
    }

    private fun initButton() {
        binding.btnDecline.clickWithDebounce {
            navigateToPage(R.id.fragmentScreenAccept, R.id.fragmentCallClose)
        }
    }

    private fun startTimer(time: Long) {
        var count = 0L
        countDownTimer = object : CountDownTimer(time, 1000L) {
            override fun onTick(p0: Long) {
                mTimeLeftInMillis = p0
                count += 1000L
                updateCountText(count)

            }

            override fun onFinish() {
                timeRunning = false
            }

        }
        timeRunning = true



        countDownTimer?.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timeRunning = false
    }

    private fun updateCountText(time: Long) {
        val minutes = (time / 1000).toInt() / 60
        val seconds = (time / 1000).toInt() % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        binding.time.text = timeLeftFormatted
    }

    override fun onPause() {
        super.onPause()
        pauseTimer()
    }

    override fun onStop() {
        super.onStop()
        pauseTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.pausePlayer()
    }


}