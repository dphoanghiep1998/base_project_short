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
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallBinding


class FragmentCall : Fragment() {
    private lateinit var binding: FragmentCallBinding
    private var action: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        action = {
            navigateToPage(R.id.fragmentCall, R.id.fragmentCallScreen)
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

    private fun checkPermission(action: (() -> Unit)? = null) {

        if (
            requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            requireContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "checkPermission: true")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                cameraLauncher.launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                )
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