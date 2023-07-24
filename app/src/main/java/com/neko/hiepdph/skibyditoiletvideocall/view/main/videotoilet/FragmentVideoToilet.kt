package com.neko.hiepdph.skibyditoiletvideocall.view.main.videotoilet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.gianghv.libads.NativeAdsManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.neko.hiepdph.skibyditoiletvideocall.BuildConfig
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.InterAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.pushEvent
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.common.showInterAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MonsterModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentVideoToiletBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel

class FragmentVideoToilet : Fragment() {
    private lateinit var binding: FragmentVideoToiletBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var receiver: BroadcastReceiver? = null
    private var audioManager: AudioManager? = null
    private var intentFilter: IntentFilter? = null
    private var lastClickTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoToiletBinding.inflate(inflater, container, false)
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, mItent: Intent?) {
                lifecycleScope.launchWhenResumed {
                    val currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
                    if (currentVolume != null) {
                        if (currentVolume > 0) {
                            requireActivity().findViewById<ImageView>(R.id.volume_toggle)
                                .setImageResource(R.drawable.ic_volume_up)
                        } else {
                            requireActivity().findViewById<ImageView>(R.id.volume_toggle)
                                .setImageResource(R.drawable.ic_volume_mute)
                        }
                    }
                }


            }

        }
        intentFilter = IntentFilter()
        intentFilter?.addAction("android.media.VOLUME_CHANGED_ACTION")
        requireActivity().registerReceiver(receiver, intentFilter)
        changeBackPressCallBack()
        initAds()
        return binding.root
    }

    companion object {
        var firstTimeOpen = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayer()
        initButton()
        showBannerAds(binding.bannerAds)
    }

    private fun initAds() {
        CustomApplication.app.mNativeAdManagerHome = NativeAdsManager(
            requireContext(), BuildConfig.native_video_id1, BuildConfig.native_video_id2
        )
    }

    private fun initButton() {
        val mCurrentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (mCurrentVolume != null) {
            if (mCurrentVolume > 0) {
                requireActivity().findViewById<ImageView>(R.id.volume_toggle)
                    .setImageResource(R.drawable.ic_volume_up)
            } else {
                requireActivity().findViewById<ImageView>(R.id.volume_toggle)
                    .setImageResource(R.drawable.ic_volume_mute)
            }
        }
        requireActivity().findViewById<ImageView>(R.id.play_again).clickWithDebounce {
            viewModel.reloadVideo()
        }
        requireActivity().findViewById<ImageView>(R.id.next).clickWithDebounce(1000) {
            var index = viewModel.data.indexOf(viewModel.getCurrentModel())
            if (index < 39) {
                index++
                if (viewModel.data[index].isRewardContent) {
                    showInterAds(action = {
                        playVideo(viewModel.data[index])
                    }, type = InterAdsEnum.VIDEO)
                } else {
                    playVideo(viewModel.data[index])
                }
            } else {
                playVideo(viewModel.data[0])
            }
        }

        requireActivity().findViewById<ImageView>(R.id.list_video).clickWithDebounce {
            showInterAds(action = {
                viewModel.resetPlayer()
                findNavController().navigate(R.id.fragmentListVideoToilet)
            }, InterAdsEnum.VIDEO)
            CustomApplication.app.mNativeAdManagerHome?.loadAds(onLoadSuccess = {
                CustomApplication.app.nativeADHome?.value = it
            })
        }


        requireActivity().findViewById<ImageView>(R.id.btn_back_controller).clickWithDebounce {
            Log.d("TAG", "initButton: ")
            if (SystemClock.elapsedRealtime() - lastClickTime < 30000 && InterstitialSingleReqAdManager.isShowingAds) return@clickWithDebounce
            else showInterAds(action = {
                requireContext().pushEvent("click_back_video")
                findNavController().popBackStack()
            }, InterAdsEnum.FUNCTION)
            lastClickTime = SystemClock.elapsedRealtime()

        }

        requireActivity().findViewById<ImageView>(R.id.volume_toggle).clickWithDebounce {
            val mCurrentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (mCurrentVolume != null) {
                if (mCurrentVolume > 0) {
                    audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                    requireActivity().findViewById<ImageView>(R.id.volume_toggle)
                        .setImageResource(R.drawable.ic_volume_mute)
                } else {
                    audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0)
                    requireActivity().findViewById<ImageView>(R.id.volume_toggle)
                        .setImageResource(R.drawable.ic_volume_up)
                }
            }
        }
        if (firstTimeOpen) {
            val anim = ScaleAnimation(
                1.0f,
                0.6f,
                1.0f,
                0.6f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            val animRotate = RotateAnimation(
                30f, -30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )

            animRotate.duration = 300
            animRotate.repeatCount = 2
            animRotate.repeatMode = Animation.REVERSE
            animRotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    firstTimeOpen = false
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })
            anim.duration = 300
            anim.repeatCount = 2
            anim.repeatMode = Animation.REVERSE
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    requireActivity().findViewById<ImageView>(R.id.list_video)
                        .startAnimation(animRotate)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })


            requireActivity().findViewById<ImageView>(R.id.list_video).startAnimation(anim)
        }


    }

    private fun playVideo(monsterModel: MonsterModel) {
        viewModel.setCurrentModel(monsterModel)
        val dataPos =
            AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf()).toMutableList()
        dataPos.remove(viewModel.getCurrentModel().id)
        AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
        viewModel.playAudio(MediaItem.fromUri(viewModel.getCurrentModel().content), onEnd = {})
    }


    private fun setupPlayer() {
        binding.playerView.apply {
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            player = viewModel.getPlayer()
            keepScreenOn = true

        }
        try {
            viewModel.playAudio(MediaItem.fromUri(viewModel.getCurrentModel().content), onEnd = {
                Log.d("TAG", "setupPlayer: ")
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetPlayer()
        requireActivity().unregisterReceiver(receiver)

    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (SystemClock.elapsedRealtime() - lastClickTime < 30000 && InterstitialSingleReqAdManager.isShowingAds) return
                else showInterAds(action = {
                    requireContext().pushEvent("click_back_video")
                    findNavController().popBackStack()
                }, InterAdsEnum.FUNCTION)
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

}