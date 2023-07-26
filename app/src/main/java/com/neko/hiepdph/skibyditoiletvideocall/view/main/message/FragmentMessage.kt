package com.neko.hiepdph.skibyditoiletvideocall.view.main.message

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogConfirm
import com.neko.hiepdph.skibyditoiletvideocall.common.InterAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.common.showInterAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MessageModel
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentMessageBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentMessage : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private var adapterMessage: AdapterMessage? = null
    private var adapterScripted: AdapterScripted? = null
    private var action: (() -> Unit)? = null
    private val viewModel by activityViewModels<AppViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        action = {
            showInterAds(action = {
                val direction = FragmentMessageDirections.actionFragmentMessageToFragmentCallScreen(
                    OtherCallModel(
                        0, R.drawable.ic_banner_progress_call, "Skibidi Toilet", R.raw.john_porn, 4
                    )
                )
                findNavController().navigate(direction)
            }, InterAdsEnum.CALL_VIDEO)

        }

    }

    private fun initView() {
        initButton()
        initRecyclerView()
    }

    private fun initButton() {
        binding.btnCall.clickWithDebounce {
            showInterAds(action = {
                navigateToPage(R.id.fragmentMessage, R.id.fragmentCall)
            }, InterAdsEnum.CALL_VIDEO)
        }
        binding.btnCallVideo.clickWithDebounce {
            checkPermission(action)
        }
        binding.icBack.clickWithDebounce {
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView() {
        val data = mutableListOf(
            MessageModel(getString(R.string.question_1), getString(R.string.answer_1)),
            MessageModel(getString(R.string.question_2), getString(R.string.answer_2)),
            MessageModel(getString(R.string.question_3), getString(R.string.answer_3)),
            MessageModel(getString(R.string.question_4), getString(R.string.answer_4)),
            MessageModel(getString(R.string.question_5), getString(R.string.answer_5)),
            MessageModel(getString(R.string.question_6), getString(R.string.answer_6)),
            MessageModel(getString(R.string.question_7), getString(R.string.answer_7)),
            MessageModel(getString(R.string.question_8), getString(R.string.answer_8)),
            MessageModel(getString(R.string.question_9), getString(R.string.answer_9)),
            MessageModel(getString(R.string.question_10), getString(R.string.answer_10)),
            MessageModel(getString(R.string.question_11), getString(R.string.answer_11)),
            MessageModel(getString(R.string.question_12), getString(R.string.answer_12)),
            MessageModel(getString(R.string.question_13), getString(R.string.answer_13)),
            MessageModel(getString(R.string.question_14), getString(R.string.answer_14)),
            MessageModel(getString(R.string.question_15), getString(R.string.answer_15)),
            MessageModel(getString(R.string.question_16), getString(R.string.answer_16)),
            MessageModel(getString(R.string.question_17), getString(R.string.answer_17)),
            MessageModel(getString(R.string.question_18), getString(R.string.answer_18)),
            MessageModel(getString(R.string.question_19), getString(R.string.answer_19)),
            MessageModel(getString(R.string.question_20), getString(R.string.answer_20)),
            MessageModel(getString(R.string.question_21), getString(R.string.answer_21)),
        )


        adapterMessage = AdapterMessage(onLoadDone = {
            lifecycleScope.launchWhenResumed {
                viewModel.playAudio(MediaItem.fromUri(
                    RawResourceDataSource.buildRawResourceUri(
                        R.raw.sound_received
                    )
                ), onEnd = {})
            }

        })
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rcvMessage.layoutManager = linearLayoutManager
        binding.rcvMessage.adapter = adapterMessage
        autoMaticAnswer(MessageModel("", getString(R.string.answer)))

        adapterScripted = AdapterScripted(onClickScriptedItem = {

            it.isSent = true
            adapterMessage?.insertMessage(it)
            lifecycleScope.launch {
                viewModel.playAudio(MediaItem.fromUri(
                    RawResourceDataSource.buildRawResourceUri(
                        R.raw.sound_send
                    )
                ), onEnd = {})
            }

            autoMaticAnswer(it)
        })
        val linearLayoutManagerScripted =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rcvScripted.layoutManager = linearLayoutManagerScripted
        binding.rcvScripted.adapter = adapterScripted

        adapterScripted?.setData(data)
    }

    private fun autoMaticAnswer(model: MessageModel) {
        val newModel = model.copy()
        newModel.isSent = false
        adapterMessage?.insertReceivedMessage(newModel)
        adapterMessage?.setLoading(true)

    }

    private fun checkPermission(action: (() -> Unit)? = null) {

        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || requireContext().checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "checkPermission: true")
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                ) || !ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.RECORD_AUDIO
                )
            ) {
                val dialogPermission = DialogConfirm(
                    requireContext(), onPressPositive = {
                        cameraLauncher.launch(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null)
                            )
                        )
                    }, isCloseApp = false, isDelete = false, permission = false
                )
                dialogPermission.show()
            } else {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                    )
                )
            }
        } else {
            Log.d("TAG", "checkPermission: false")
            action?.invoke()
        }
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && requireContext().checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                action?.invoke()
            }
        }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && requireContext().checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                action?.invoke()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        adapterMessage?.cancelJob()
    }

}