package com.neko.hiepdph.skibyditoiletvideocall.view.main.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.data.MessageModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentMessageBinding

class FragmentMessage : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private var adapterMessage: AdapterMessage? = null
    private var adapterScripted: AdapterScripted? = null

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
            navigateToPage(R.id.fragmentMessage, R.id.fragmentCallScreen)
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


}