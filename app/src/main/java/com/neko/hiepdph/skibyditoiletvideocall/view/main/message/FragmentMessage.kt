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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MessageModel
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentMessageBinding

class FragmentMessage : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private var adapterMessage: AdapterMessage? = null
    private var adapterScripted: AdapterScripted? = null
    private var action: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        action = {
            val direction = FragmentMessageDirections.actionFragmentMessageToFragmentCallScreen(
                OtherCallModel(
                    0,
                    "john pork",
                    R.raw.john_porn,
                    4
                )
            )
            findNavController().navigate(direction)
        }

    }

    private fun initView() {
        initButton()
        initRecyclerView()
    }

    private fun initButton() {
        binding.btnCall.clickWithDebounce {
            navigateToPage(R.id.fragmentMessage, R.id.fragmentCall)
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
            MessageModel("Hi", "Hi cc"),
            MessageModel("Im good, what about you", "you cc"),
            MessageModel("Lmao", "lmao cc"),
            MessageModel("DCM", "dcm cc"),
            MessageModel("F**", "f cc"),
            MessageModel("L~", "l cc"),
            MessageModel("C", "c cc"),
            MessageModel("B", "b cc"),
        )


        adapterMessage = AdapterMessage()
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rcvMessage.layoutManager = linearLayoutManager
        binding.rcvMessage.adapter = adapterMessage



        adapterScripted = AdapterScripted(onClickScriptedItem = {
            it.isSent = true
            adapterMessage?.insertMessage(it)
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.RECORD_AUDIO
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