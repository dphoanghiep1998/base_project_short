package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallScreenBinding

class FragmentCallScreen : Fragment() {
    private lateinit var binding: FragmentCallScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

}