package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallDeclineBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel


class FragmentCallDecline : Fragment() {

    private lateinit var binding: FragmentCallDeclineBinding
    private val viewModel by activityViewModels<AppViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallDeclineBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
    }

    private fun initView() {
        viewModel.playAudio(
            MediaItem.fromUri(
            RawResourceDataSource.buildRawResourceUri(
                R.raw.sound_phone_decline
            )), onEnd = {}, repeat = ExoPlayer.REPEAT_MODE_ONE)

        val countDownTimer = object :CountDownTimer(2000,1000){
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                viewModel.pausePlayer()
                binding.containerBroken.show()
            }

        }
        countDownTimer.start()
        binding.containerBroken.clickWithDebounce {
            navigateToPage(R.id.fragmentCallDecline, R.id.fragmentHome)
        }
    }


    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

}