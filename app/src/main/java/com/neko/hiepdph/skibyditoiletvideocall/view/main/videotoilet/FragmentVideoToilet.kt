package com.neko.hiepdph.skibyditoiletvideocall.view.main.videotoilet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogConfirm
import com.neko.hiepdph.skibyditoiletvideocall.common.InterAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.RewardAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.pushEvent
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.common.showInterAds
import com.neko.hiepdph.skibyditoiletvideocall.common.showRewardAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MonsterModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentVideoToiletBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel

class FragmentVideoToilet : Fragment() {
    private lateinit var binding: FragmentVideoToiletBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var receiver: BroadcastReceiver? = null
    private var audioManager: AudioManager? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoToiletBinding.inflate(inflater, container, false)
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, mItent: Intent?) {
                val currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
                if (currentVolume != null) {
                    if(currentVolume > 0){
                        requireActivity().findViewById<ImageView>(R.id.volume_toggle).setImageResource(R.drawable.ic_volume_up)
                    }else{
                        requireActivity().findViewById<ImageView>(R.id.volume_toggle).setImageResource(R.drawable.ic_volume_mute)
                    }
                }

            }

        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION")
        requireActivity().registerReceiver(receiver, intentFilter)
        return binding.root
    }

    companion object {
        var clickTurn = 0
        var firstTimeOpen = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayer()
        initButton()
        showBannerAds(binding.bannerAds)
    }

    private fun initButton() {
        val mCurrentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (mCurrentVolume != null) {
            if(mCurrentVolume > 0){
                requireActivity().findViewById<ImageView>(R.id.volume_toggle).setImageResource(R.drawable.ic_volume_up)
            }else{
                requireActivity().findViewById<ImageView>(R.id.volume_toggle).setImageResource(R.drawable.ic_volume_mute)
            }
        }
        requireActivity().findViewById<ImageView>(R.id.play_again).clickWithDebounce {
            viewModel.reloadVideo()
        }
        requireActivity().findViewById<ImageView>(R.id.next).clickWithDebounce(1000) {
            var index = viewModel.data.indexOf(viewModel.getCurrentModel())
            if (index < 41) {
                clickTurn++
                index++
                if (viewModel.data[index] == "ads") {
                    index++
                }
                if ((viewModel.data[index] as MonsterModel).isRewardContent) {
                    val dialogConfirm = DialogConfirm(requireContext(), onPressPositive = {
                        showRewardAds(actionSuccess = {
                            viewModel.setCurrentModel(viewModel.data[index] as MonsterModel)

                            val dataPos =
                                AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf())
                                    .toMutableList()
                            dataPos.remove(viewModel.getCurrentModel().id)
                            AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
                            requireContext().pushEvent("click_next_reward")
                            viewModel.playAudio(
                                MediaItem.fromUri(viewModel.getCurrentModel().content),
                                onEnd = {})
                        }, actionFailed = {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.require_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        }, RewardAdsEnum.VIDEO)
                    })
                    dialogConfirm.show()
                } else {
                    if (clickTurn > 0 && clickTurn % 5 == 0) {
                        showInterAds(action = {
                            viewModel.setCurrentModel(viewModel.data[index] as MonsterModel)
                            val dataPos =
                                AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf())
                                    .toMutableList()
                            dataPos.remove(viewModel.getCurrentModel().id)
                            AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
                            viewModel.playAudio(
                                MediaItem.fromUri(viewModel.getCurrentModel().content),
                                onEnd = {})
                        }, InterAdsEnum.VIDEO)

                    } else {
                        viewModel.setCurrentModel(viewModel.data[index] as MonsterModel)
                        val dataPos =
                            AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf())
                                .toMutableList()
                        dataPos.remove(viewModel.getCurrentModel().id)
                        AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
                        viewModel.playAudio(
                            MediaItem.fromUri(viewModel.getCurrentModel().content),
                            onEnd = {})
                    }
                }
            } else {
                clickTurn++
                if (clickTurn > 0 && clickTurn % 5 == 0) {
                    showInterAds(action = {
                        viewModel.setCurrentModel(viewModel.data[0] as MonsterModel)

                        val dataPos =
                            AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf())
                                .toMutableList()
                        dataPos.remove(viewModel.getCurrentModel().id)
                        AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
                        viewModel.playAudio(
                            MediaItem.fromUri(viewModel.getCurrentModel().content),
                            onEnd = {})
                    }, InterAdsEnum.VIDEO)

                } else {
                    viewModel.setCurrentModel(viewModel.data[0] as MonsterModel)

                    val dataPos = AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf())
                        .toMutableList()
                    dataPos.remove(viewModel.getCurrentModel().id)
                    AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
                    viewModel.playAudio(
                        MediaItem.fromUri(viewModel.getCurrentModel().content),
                        onEnd = {})
                }

            }
        }

        requireActivity().findViewById<ImageView>(R.id.list_video).clickWithDebounce {
            showInterAds(action = {
                viewModel.resetPlayer()
                findNavController().navigate(R.id.fragmentListVideoToilet)
            }, InterAdsEnum.VIDEO)
        }


        requireActivity().findViewById<ImageView>(R.id.btn_back_controller).clickWithDebounce {
            Log.d("TAG", "initButton: ")
           findNavController().navigate(R.id.action_fragmentVideoToilet_to_fragmentHome )
        }

        requireActivity().findViewById<ImageView>(R.id.volume_toggle).clickWithDebounce {
            val mCurrentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (mCurrentVolume != null) {
                if(mCurrentVolume > 0){
                    audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                    requireActivity().findViewById<ImageView>(R.id.volume_toggle).setImageResource(R.drawable.ic_volume_mute)
                }else{
                    audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0)
                    requireActivity().findViewById<ImageView>(R.id.volume_toggle).setImageResource(R.drawable.ic_volume_up)
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

}