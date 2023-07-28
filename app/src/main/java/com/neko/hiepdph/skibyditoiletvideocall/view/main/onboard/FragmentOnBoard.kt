package com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gianghv.libads.NativeAdsManager
import com.neko.hiepdph.skibyditoiletvideocall.BuildConfig
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
        setStatusColor()
        changeBackPressCallBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        resetAdsWhenConnectivityChange()

    }

    private fun resetAdsWhenConnectivityChange(){
        viewModel.typeNetwork.observe(viewLifecycleOwner){conn ->
            conn?.let {
                if(it != ConnectionType.UNKNOWN && !NativeAdsManager.isLoadingAds){
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
        if (CustomApplication.app.mNativeAdManagerIntro == null) {
            CustomApplication.app.mNativeAdManagerIntro = NativeAdsManager(
                requireContext(), BuildConfig.native_intro_id1, BuildConfig.native_intro_id2
            )
        }
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
                Log.d("TAG", "insertAds: true")
                binding.btnNext.show()

            }, onLoadFail = {
                Log.d("TAG", "insertAds: fail")
                binding.nativeAdSmallView.isVisible = false
                binding.nativeAdSmallView.visibility = View.GONE
                binding.btnNext.show()
            })
        }
    }
}