package com.neko.hiepdph.skibyditoiletvideocall.view.main.videotoilet

import android.app.Dialog
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
import android.widget.Toast
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
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogLoadingProgress
import com.neko.hiepdph.skibyditoiletvideocall.common.DownloadManagerApp
import com.neko.hiepdph.skibyditoiletvideocall.common.InterAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.pushEvent
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.common.showInterAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MonsterModel
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentVideoToiletBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import java.io.File

class FragmentVideoToilet : Fragment() {
    private lateinit var binding: FragmentVideoToiletBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var receiver: BroadcastReceiver? = null
    private var audioManager: AudioManager? = null
    private var intentFilter: IntentFilter? = null
    private var lastClickTime: Long = 0
    private var count = 0
    private var dialogLoadingProgress: Dialog? = null

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

        showBannerAds(binding.bannerAds)
        dialogLoadingProgress = DialogLoadingProgress().onCreateDialog(requireContext())
        setupPlayer()
        initButton()
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
            count++
            if (viewModel.getCurrentModel() == null) {
                viewModel.setCurrentModel(viewModel.getData(requireContext())[0])
            }
            var index = viewModel.getData(requireContext()).indexOf(viewModel.getCurrentModel())
            Log.d("TAG", "initButton: " +index)
            if (index < 39) {
                index++
                if (count in AppSharePreference.INSTANCE.getListUnlockPos(
                        mutableListOf(
                            2, 7, 12, 17, 22, 27
                        )
                    )
                ) {
                    showInterAds(action = {

                        if (!isVideoExist(viewModel.getData(requireContext())[index])) {
                            dialogLoadingProgress?.show()

                            DownloadManagerApp.INSTANCE.makeRequestDownload(viewModel.getData(
                                requireContext()
                            )[index].content,
                                requireActivity().filesDir.path + "/video_source",
                                "video${viewModel.getData(requireContext())[index].id}",
                                onDownloadCompleted = {
                                    addIndexToListDownloadedVideo(viewModel.getCurrentModel()!!.id)
                                    showButtonCallAtController()
                                    dialogLoadingProgress?.dismiss()
                                    playVideoLocal(viewModel.getData(requireContext())[index])
                                },
                                onDownloadFailed = {
                                    hideButtonCallAtController()
                                    dialogLoadingProgress?.dismiss()
                                    playVideo(viewModel.getData(requireContext())[index])
                                })
                        } else {
                            showButtonCallAtController()
                            playVideoLocal(viewModel.getData(requireContext())[index])
                        }
                    }, type = InterAdsEnum.VIDEO)
                } else {
                    if (!isVideoExist(viewModel.getData(requireContext())[index])) {
                        dialogLoadingProgress?.show()
                        DownloadManagerApp.INSTANCE.makeRequestDownload(viewModel.getData(
                            requireContext()
                        )[index].content,
                            requireActivity().filesDir.path + "/video_source",
                            "video${viewModel.getData(requireContext())[index].id}",
                            onDownloadCompleted = {
                                addIndexToListDownloadedVideo(viewModel.getCurrentModel()!!.id)
                                showButtonCallAtController()
                                dialogLoadingProgress?.dismiss()
                                playVideoLocal(viewModel.getData(requireContext())[index])
                            },
                            onDownloadFailed = {
                                hideButtonCallAtController()
                                dialogLoadingProgress?.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.download_fail),
                                    Toast.LENGTH_SHORT
                                ).show()
                                playVideo(viewModel.getData(requireContext())[index])
                            })
                    } else {
                        showButtonCallAtController()
                        playVideoLocal(viewModel.getData(requireContext())[index])
                    }
                }
            } else {
                index = 0
                count = 0
                if (!isVideoExist(viewModel.getData(requireContext())[index])) {
                    dialogLoadingProgress?.show()
                    DownloadManagerApp.INSTANCE.makeRequestDownload(viewModel.getData(
                        requireContext()
                    )[index].content,
                        requireActivity().filesDir.path + "/video_source",
                        "video${viewModel.getData(requireContext())[index].id}",
                        onDownloadCompleted = {
                            addIndexToListDownloadedVideo(viewModel.getCurrentModel()!!.id)
                            showButtonCallAtController()
                            dialogLoadingProgress?.dismiss()
                            playVideoLocal(viewModel.getData(requireContext())[index])
                        },
                        onDownloadFailed = {
                            hideButtonCallAtController()
                            dialogLoadingProgress?.dismiss()
                            playVideo(viewModel.getData(requireContext())[index])
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.download_fail),
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                } else {
                    showButtonCallAtController()
                    playVideoLocal(viewModel.getData(requireContext())[index])
                }
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

        requireActivity().findViewById<ImageView>(R.id.video_call_toilet).clickWithDebounce {
            val model = OtherCallModel(
                0,
                R.drawable.ic_banner_progress_call,
                "Skibidi Toilet",
                0,
                viewModel.getCurrentModel()?.content_local.toString(),
                4
            )
            val direction =
                FragmentVideoToiletDirections.actionFragmentVideoToiletToFragmentCallScreen(model)
            findNavController().navigate(direction)
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

    private fun showButtonCallAtController() {
        requireActivity().findViewById<ImageView>(R.id.video_call_toilet).show()
    }

    private fun hideButtonCallAtController() {
        requireActivity().findViewById<ImageView>(R.id.video_call_toilet).hide()

    }

    private fun playVideo(monsterModel: MonsterModel) {
        viewModel.setCurrentModel(monsterModel)
        val dataPos =
            AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf()).toMutableList()
        dataPos.remove(viewModel.getCurrentModel()!!.id)
        AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
        viewModel.playAudio(MediaItem.fromUri(viewModel.getCurrentModel()!!.content), onEnd = {})
    }

    private fun playVideoLocal(monsterModel: MonsterModel) {
        viewModel.setCurrentModel(monsterModel)
        val dataPos =
            AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf()).toMutableList()
        dataPos.remove(viewModel.getCurrentModel()!!.id)
        AppSharePreference.INSTANCE.saveListVideoPlayed(dataPos)
        viewModel.playAudio(
            MediaItem.fromUri(viewModel.getCurrentModel()!!.content_local),
            onEnd = {})
    }


    private fun setupPlayer() {
        binding.playerView.apply {
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            player = viewModel.getPlayer()
            keepScreenOn = true
        }
        try {
            if (viewModel.getCurrentModel() == null) {
                viewModel.setCurrentModel(viewModel.getData(requireContext())[0])
            }
            if (!isVideoExist(viewModel.getCurrentModel()!!)) {
                dialogLoadingProgress?.show()
                DownloadManagerApp.INSTANCE.makeRequestDownload(viewModel.getCurrentModel()!!.content,
                    requireActivity().filesDir.path + "/video_source",
                    "video${viewModel.getCurrentModel()!!.id}",
                    onDownloadCompleted = {
                        addIndexToListDownloadedVideo(viewModel.getCurrentModel()!!.id)
                        showButtonCallAtController()
                        dialogLoadingProgress?.dismiss()
                        playVideoLocal(viewModel.getCurrentModel()!!)

                    },
                    onDownloadFailed = {
                        hideButtonCallAtController()
                        dialogLoadingProgress?.dismiss()
                        playVideo(viewModel.getCurrentModel()!!)
                    })
            } else {
                showButtonCallAtController()
                playVideoLocal(viewModel.getCurrentModel()!!)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addIndexToListDownloadedVideo(id: Int) {
        val listDownloadedVideo =
            AppSharePreference.INSTANCE.getListVideoDownloaded(mutableListOf()).toMutableList()
        if (id !in listDownloadedVideo) {
            listDownloadedVideo.add(id)
            AppSharePreference.INSTANCE.saveListVideoDownloaded(listDownloadedVideo)
            Log.d("TAG", "addIndexToListDownloadedVideo: "+AppSharePreference.INSTANCE.getListVideoDownloaded(mutableListOf()).toMutableList())
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

    private fun isVideoExist(file: MonsterModel): Boolean {
        return File(file.content_local).exists()
    }

}