package com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gianghv.libads.NativeAdsManager
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.ConnectionType
import com.neko.hiepdph.skibyditoiletvideocall.common.NativeTypeEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.common.showNativeAds
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentOnboardBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.adapter.ViewPagerAdapter
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.child_fragment.FragmentOnboardChild1
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.child_fragment.FragmentOnboardChild2
import com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.child_fragment.FragmentOnboardChild3
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel


class FragmentOnBoard : Fragment() {
    private lateinit var binding: FragmentOnboardBinding
    private lateinit var viewpagerAdapter: ViewPagerAdapter
    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardBinding.inflate(inflater, container, false)
        if (CustomApplication.app.isPassIntro) {
            findNavController().navigate(R.id.fragmentHome)
        }
        setStatusColor()
        changeBackPressCallBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (!CustomApplication.app.isPassIntro) {
            resetAdsWhenConnectivityChange()
        }

    }

    private fun resetAdsWhenConnectivityChange() {
        viewModel.typeNetwork.observe(viewLifecycleOwner) { conn ->
            conn?.let {
                if (it != ConnectionType.UNKNOWN && !NativeAdsManager.isLoadingAds) {
                    binding.btnNext.hide()
                    binding.loadingAds.show()
                    showNativeAds(binding.nativeAdSmallView, {
                        binding.btnNext.show()
                        binding.loadingAds.hide()
                    }, {
                        binding.btnNext.show()
                        binding.loadingAds.hide()
                    }, NativeTypeEnum.INTRO)
                }
            }
        }
    }

    private fun initView() {
        binding.btnNext.hide()
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
                CustomApplication.app.isPassIntro = true
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
}