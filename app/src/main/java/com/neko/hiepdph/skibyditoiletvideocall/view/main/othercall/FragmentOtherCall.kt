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
            OtherCallModel(R.drawable.ic_momo, "MOMO"),
            OtherCallModel(R.drawable.ic_valak, "VALAK"),
            OtherCallModel(R.drawable.ic_wednesday, "WEDNESDAY"),
            OtherCallModel(R.drawable.ic_momy_longleg, "MOMMY"),
        )
        adapterOtherCall = AdapterOtherCall(onClickItem = {
            navigateToPage(R.id.fragmentOtherCall,R.id.fragmentCallScreen)
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