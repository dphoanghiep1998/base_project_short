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
import com.neko.hiepdph.skibyditoiletvideocall.common.InterAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.common.showInterAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentOtherCallBinding

class FragmentOtherCall : Fragment() {
    private lateinit var binding: FragmentOtherCallBinding
    private var adapterOtherCall: AdapterOtherCall? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        showBannerAds(binding.bannerAds)
    }

    private fun initRecyclerView() {
        val listData = mutableListOf(
            OtherCallModel(
                R.drawable.ic_momo, R.drawable.ic_momo_circle, "Spooky Momo", R.raw.momo,"", 0
            ),
            OtherCallModel(R.drawable.ic_nun, R.drawable.ic_nun_circle, "The Nun", R.raw.nun, "",1),
            OtherCallModel(
                R.drawable.ic_wednesday, R.drawable.ic_wed_circle, "Wednesday", R.raw.wednesday,"", 2
            ),
            OtherCallModel(
                R.drawable.ic_mm, R.drawable.ic_mm_circle, "Mommy Long Leg", R.raw.mommy, "",3
            ),
        )
        adapterOtherCall = AdapterOtherCall(onClickItem = {
            val direction =
                FragmentOtherCallDirections.actionFragmentOtherCallToFragmentCallScreen(it)
            findNavController().navigate(direction)
        }, onClickAdsItem = {
            showInterAds(action = {
                val direction =
                    FragmentOtherCallDirections.actionFragmentOtherCallToFragmentCallScreen(it)
                findNavController().navigate(direction)
            }, type = InterAdsEnum.CALL_VIDEO)
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