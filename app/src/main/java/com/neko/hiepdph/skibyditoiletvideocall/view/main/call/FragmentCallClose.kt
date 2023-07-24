package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.InterAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.common.showInterAds
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallCloseBinding
import kotlin.system.exitProcess


class FragmentCallClose : Fragment() {

    private lateinit var binding: FragmentCallCloseBinding
    private val navArg by navArgs<FragmentCallCloseArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallCloseBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        changeBackPressCallBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.imvBanner.setImageResource(navArg.model.circleImage)
        binding.title.text = navArg.model.name
        initButton()
    }

    private fun initButton() {
        binding.btnClose.clickWithDebounce {
            showInterAds(action = {
                navigateToPage(R.id.fragmentCallClose, R.id.fragmentHome)
            }, type = InterAdsEnum.CALL_VIDEO)

        }
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
}