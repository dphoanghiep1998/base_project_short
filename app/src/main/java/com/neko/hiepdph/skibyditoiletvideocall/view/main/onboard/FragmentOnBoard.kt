package com.neko.hiepdph.toiletseries.main.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentOnboardBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.adapter.ViewPagerAdapter
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.child_fragment.FragmentOnboardChild1
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.child_fragment.FragmentOnboardChild2
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.child_fragment.FragmentOnboardChild3


class FragmentOnBoard : Fragment() {
    private lateinit var binding: FragmentOnboardBinding
    private lateinit var viewpagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardBinding.inflate(inflater, container, false)
        setStatusColor()
        changeBackPressCallBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        insertAds()
    }


    private fun initView() {
        initViewPager()
        initButton()
    }
    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun setStatusColor() {
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }


    private fun initButton() {

        binding.btnNext.setOnClickListener {
            val currentItem = binding.vpOnboard.currentItem
            if (binding.vpOnboard.currentItem == 2) {
                AppSharePreference.INSTANCE.setPassSetting(true)
                navigateToPage(R.id.fragmentOnBoard, R.id.fragmentHome)
            } else {
                binding.vpOnboard.currentItem = currentItem + 1
            }

        }


    }

    private fun initViewPager() {
        viewpagerAdapter = ViewPagerAdapter(
            arrayListOf(
                FragmentOnboardChild1(), FragmentOnboardChild2(), FragmentOnboardChild3()
            ), childFragmentManager, lifecycle
        )
        binding.vpOnboard.adapter = viewpagerAdapter
        binding.dotsIndicator.attachTo(binding.vpOnboard)
    }

    private fun insertAds() {
        CustomApplication.app.nativeADIntro?.observe(viewLifecycleOwner) {
            it?.let {
                binding.nativeAdSmallView.setNativeAd(it)
                binding.nativeAdSmallView.isVisible = true
                binding.nativeAdSmallView.showShimmer(false)
            }
        }
        if (CustomApplication.app.nativeADIntro?.value == null) {
            CustomApplication.app.mNativeAdManagerIntro?.loadAds(onLoadSuccess = {
                CustomApplication.app.nativeADIntro?.value = it
            }, onLoadFail = {
                binding.nativeAdSmallView.isVisible = false
                binding.nativeAdSmallView.visibility = View.GONE
            })
        }
    }
}