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
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallScheduleBinding

class FragmentCallSchedule : Fragment() {
    private lateinit var binding: FragmentCallScheduleBinding
    private var minute = 0L
    private var second = 0L
    private var action: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCallScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        action = {
            val totalTime = minute + second
            val direction = FragmentCallScheduleDirections.actionFragmentCallScheduleToFragmentProgressCall(totalTime)
            findNavController().navigate(direction)
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
    private fun checkPermission(action: (() -> Unit)? = null) {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                )
            ) {
                cameraLauncher.launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                )
            } else {
                launcher.launch(Manifest.permission.CAMERA)

            }
        } else {
            action?.invoke()
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                action?.invoke()
            }
        }


    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            action?.invoke()
        }
    }
}