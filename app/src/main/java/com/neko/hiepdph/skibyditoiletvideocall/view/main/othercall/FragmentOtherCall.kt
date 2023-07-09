package com.neko.hiepdph.skibyditoiletvideocall.view.main.othercall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentOtherCallBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.call.FragmentCallScreenDirections

class FragmentOtherCall : Fragment() {
    private lateinit var binding: FragmentOtherCallBinding
    private var adapterOtherCall: AdapterOtherCall? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtherCallBinding.inflate(inflater, container, false)
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

    private fun initRecyclerView() {
        val listData = mutableListOf(
            OtherCallModel(R.drawable.ic_momo, "MOMO",R.raw.momo,0),
            OtherCallModel(R.drawable.ic_valak, "VALAK",R.raw.nun,1),
            OtherCallModel(R.drawable.ic_wednesday, "WEDNESDAY",R.raw.wednesday,2),
            OtherCallModel(R.drawable.ic_momy_longleg, "MOMMY",R.raw.mommy,3),
        )
        adapterOtherCall = AdapterOtherCall(onClickItem = {
            val direction = FragmentOtherCallDirections.actionFragmentOtherCallToFragmentCallScreen(it)
            findNavController().navigate(direction)
        })
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        binding.rcvOtherCall.layoutManager = gridLayoutManager
        binding.rcvOtherCall.adapter = adapterOtherCall
        adapterOtherCall?.setData(listData)
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
    }


}