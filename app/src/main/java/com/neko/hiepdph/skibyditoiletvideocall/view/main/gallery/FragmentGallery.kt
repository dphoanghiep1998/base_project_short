package com.neko.hiepdph.skibyditoiletvideocall.view.main.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeGallery()
    }

    private fun observeGallery() {
        viewModel.getListGallery().observe(viewLifecycleOwner){
            adapterGallery?.setData(it)
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
        adapterGallery = AdapterGallery(onClickItem = {}, onClickDeleteItem = {}, onClickShare = {})
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rcvGallery.layoutManager = linearLayoutManager

        binding.rcvGallery.adapter = adapterGallery

    }


}