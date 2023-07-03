package com.neko.hiepdph.skibyditoiletvideocall.view.main.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentPreviewBinding


class FragmentPreview : Fragment() {
    private lateinit var binding: FragmentPreviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }


}