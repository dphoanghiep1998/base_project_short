package com.neko.hiepdph.skibyditoiletvideocall.view.main.listvideotoilet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogConfirm
import com.neko.hiepdph.skibyditoiletvideocall.common.RewardAdsEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.showRewardAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MonsterModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentListVideoToiletBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.home.AdapterHome
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel


class FragmentListVideoToilet : Fragment() {
    private lateinit var binding: FragmentListVideoToiletBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var adapterHome: AdapterHome? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListVideoToiletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
    }

    private fun initView() {
        initButton()
        initRecyclerView()
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView() {
        adapterHome = AdapterHome(onClickItem = { model: MonsterModel, position: Int ->
            viewModel.setCurrentModel(model)
            findNavController().popBackStack()
        }, onClickRewardAdsItem = { monsterModel, position ->
            val dialogConfirm = DialogConfirm(requireContext(), onPressPositive = {
                showRewardAds(actionDoneWhenAdsNotComplete = {}, actionSuccess = {
                    viewModel.setCurrentModel(monsterModel)
                    findNavController().popBackStack()
                }, actionFailed = {
                    Toast.makeText(
                        requireContext(), getString(R.string.require_internet), Toast.LENGTH_SHORT
                    ).show()
                }, type = RewardAdsEnum.VIDEO)
            })
            dialogConfirm.show()
        })
        adapterHome?.setData(viewModel.data)
        val gridlayoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        gridlayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position != 3 && position != 37) {
                    1
                } else {
                    gridlayoutManager.spanCount
                }
            }
        }
        binding.rcvHome.layoutManager = gridlayoutManager
        binding.rcvHome.adapter = adapterHome
        loadAds()
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun loadAds() {
        CustomApplication.app.nativeADHome?.observe(viewLifecycleOwner) {
            it?.let {
                adapterHome?.insertAds(it)
            }
        }
        if (CustomApplication.app.nativeADHome?.value == null) {
            CustomApplication.app.mNativeAdManagerHome?.loadAds(onLoadSuccess = {
                CustomApplication.app.nativeADHome?.value = it
            })
        }
    }

}