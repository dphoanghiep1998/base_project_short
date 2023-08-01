package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
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
import com.gianghv.libads.AppOpenResumeAdManager
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogConfirm
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallScheduleBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import java.io.File

class FragmentCallSchedule : Fragment() {
    private lateinit var binding: FragmentCallScheduleBinding
    private var minute = 0L
    private var second = 0L
    private var action: (() -> Unit)? = null
    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCallScheduleBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        action = {
            AppOpenResumeAdManager.isShowingAd = false
            val totalTime = minute + second
            if (totalTime == 0L) {
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
                        FragmentCallScheduleDirections.actionFragmentCallScheduleToFragmentCallScreen(
                            model
                        )
                    lifecycleScope.launchWhenResumed {
                        findNavController().navigate(direction)
                    }
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
                            index,
                            viewModel.getData(requireContext())[index].image,
                            R.drawable.ic_banner_progress_call,
                            "Skibidi Toilet",
                            0,
                            viewModel.getData(requireContext())
                                .find { it.id == index }?.content_local.toString(),
                            4
                        )
                        val direction =
                            FragmentCallScheduleDirections.actionFragmentCallScheduleToFragmentCallScreen(
                                model
                            )
                        lifecycleScope.launchWhenResumed {
                            findNavController().navigate(direction)
                        }
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
                            FragmentCallScheduleDirections.actionFragmentCallScheduleToFragmentCallScreen(
                                model
                            )
                        lifecycleScope.launchWhenResumed {
                            findNavController().navigate(direction)
                        }
                    }
                }
            } else {
                val direction =
                    FragmentCallScheduleDirections.actionFragmentCallScheduleToFragmentProgressCall(
                        totalTime
                    )
                findNavController().navigate(direction)
            }

        }
    }

    private fun initView() {
        binding.wheelMinute.apply {
            wrapSelectorWheel = false
            maxValue = 59
            minValue = 0
            value = 0

            val displayValue = IntRange(minValue, maxValue).step(1).toList()
                .map { if (it.toString().length > 1) it.toString() else "0$it" }
            displayedValues = displayValue.toTypedArray()
            typeface = Typeface.createFromAsset(requireContext().assets, "app_font_600.ttf")
            setSelectedTypeface(
                Typeface.createFromAsset(
                    requireContext().assets, "app_font_600.ttf"
                )
            )
            setOnValueChangedListener { picker, oldVal, newVal ->
                minute = binding.wheelMinute.value * 60000L
            }
        }

        binding.wheelSecond.apply {
            wrapSelectorWheel = false
            maxValue = 59
            minValue = 0
            value = 0
            val displayValue = IntRange(minValue, maxValue).step(1).toList()
                .map { if (it.toString().length > 1) it.toString() else "0$it" }
            displayedValues = displayValue.toTypedArray()
            typeface = Typeface.createFromAsset(requireContext().assets, "app_font_600.ttf")
            setSelectedTypeface(
                Typeface.createFromAsset(
                    requireContext().assets, "app_font_600.ttf"
                )
            )
            setOnValueChangedListener { picker, oldVal, newVal ->
                second = binding.wheelSecond.value * 1000L
            }
        }

        binding.imvCallVideo.clickWithDebounce {
            checkPermission()
        }

        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
    }

    private fun checkPermission() {

        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || requireContext().checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "checkPermission: false")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.RECORD_AUDIO
                )
            ) {
                Log.d("TAG", "checkPermission: rational")
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
                Log.d("TAG", "checkPermission: rational false")
                launcher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                    )
                )
            }
        } else {
            Log.d("TAG", "checkPermission: true")
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