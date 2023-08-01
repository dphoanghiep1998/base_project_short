package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

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
import androidx.navigation.fragment.findNavController
import com.gianghv.libads.AppOpenResumeAdManager
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogConfirm
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.message.FragmentMessageDirections
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import java.io.File


class FragmentCall : Fragment() {
    private lateinit var binding: FragmentCallBinding
    private var action: (() -> Unit)? = null
    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        action = {
            AppOpenResumeAdManager.isShowingAd = false
            val listVideoDownloaded =
                AppSharePreference.INSTANCE.getListVideoDownloaded(mutableListOf())
                    .toMutableList()
            if (listVideoDownloaded.isEmpty()) {
                val model = OtherCallModel(
                    0,
                    R.drawable.ic_1,
                    R.drawable.ic_banner_progress_call,
                    "Skibidi Toilet",
                    R.raw.john_porn,
                    "",
                    4
                )
                val direction =
                    FragmentCallDirections.actionFragmentCallToFragmentCallScreen(model)
                findNavController().navigate(direction)
            } else {
                var index = 0
                listVideoDownloaded.shuffle()
                while (!File(viewModel.getData(requireContext())[listVideoDownloaded[0]].content_local).exists()) {
                    listVideoDownloaded.remove(listVideoDownloaded[0])
                    if (listVideoDownloaded.isEmpty()) {
                        break
                    } else {
                        listVideoDownloaded.shuffle()
                    }
                }
                AppSharePreference.INSTANCE.saveListVideoDownloaded(listVideoDownloaded)
                if (listVideoDownloaded.isNotEmpty()) {
                    index = listVideoDownloaded[0]
                    Log.d("TAG", "initButton: " + index)

                    val model = OtherCallModel(
                        0,
                        R.drawable.ic_1,
                        R.drawable.ic_banner_progress_call,
                        "Skibidi Toilet",
                        0,
                        viewModel.getData(requireContext())
                            .find { it.id == index }?.content_local.toString(),
                        4
                    )
                    val direction =
                        FragmentCallDirections.actionFragmentCallToFragmentCallScreen(model)
                    findNavController().navigate(direction)
                } else {
                    val model = OtherCallModel(
                        0,
                        R.drawable.ic_1,
                        R.drawable.ic_banner_progress_call,
                        "Skibidi Toilet",
                        R.raw.john_porn,
                        "",
                        4
                    )
                    val direction =
                        FragmentCallDirections.actionFragmentCallToFragmentCallScreen(model)
                    findNavController().navigate(direction)
                }
            }
        }
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.btnSchedule.clickWithDebounce {
            navigateToPage(R.id.fragmentCall, R.id.fragmentCallSchedule)
        }

        binding.btn15.clickWithDebounce {
            val direction =
                FragmentCallDirections.actionFragmentCallToFragmentProgressCall(15 * 1000)
            findNavController().navigate(direction)
        }

        binding.btn30.clickWithDebounce {
            val direction =
                FragmentCallDirections.actionFragmentCallToFragmentProgressCall(30 * 1000)
            findNavController().navigate(direction)

        }

        binding.btn60.clickWithDebounce {
            val direction =
                FragmentCallDirections.actionFragmentCallToFragmentProgressCall(60 * 1000)
            findNavController().navigate(direction)

        }

        binding.btnCallNow.clickWithDebounce {
            checkPermission()
        }

        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
    }

    private fun checkPermission() {

        if (
            requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            requireContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "checkPermission: true")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                val dialogPermission = DialogConfirm(
                    requireContext(), onPressPositive = {
                        AppOpenResumeAdManager.isShowingAd = true
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

}