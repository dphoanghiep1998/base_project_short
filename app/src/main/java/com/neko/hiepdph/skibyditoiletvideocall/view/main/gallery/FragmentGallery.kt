package com.neko.hiepdph.skibyditoiletvideocall.view.main.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogConfirm
import com.neko.hiepdph.skibyditoiletvideocall.common.NativeTypeEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.common.showNativeAds
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentGalleryBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel

class FragmentGallery : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private var adapterGallery: AdapterGallery? = null
    private val viewModel by activityViewModels<AppViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        showNativeAds(binding.nativeView,null,null,NativeTypeEnum.GALLERY)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeGallery()
    }

    private fun observeGallery() {
        viewModel.getListGallery().observe(viewLifecycleOwner) {
            adapterGallery?.setData(it)
            if (it.isEmpty()) {
                viewModel.deleteAll()
                viewModel.vacuumDb()
                binding.tvEmpty.show()
            } else {
                binding.tvEmpty.hide()
            }
        }
    }

    private fun initView() {
        initRecyclerView()
        initButton()
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView() {
        adapterGallery = AdapterGallery(imageData = viewModel.getData(requireContext()),onClickItem = {
            Log.d("TAG", "initRecyclerView: "+it.cameraVideoPath)
            val direction = FragmentGalleryDirections.actionFragmentGalleryToFragmentPreview(it)
            findNavController().navigate(direction)
        }, onClickDeleteItem = {
            val dialogConfirm = DialogConfirm(requireContext(), onPressPositive = {
                viewModel.deleteItemGallery(it)
            }, isCloseApp = false, true)
            dialogConfirm.show()
        }, onClickShare = {})
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rcvGallery.layoutManager = linearLayoutManager

        binding.rcvGallery.adapter = adapterGallery

    }


}