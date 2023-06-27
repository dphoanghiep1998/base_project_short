package com.neko.hiepdph.skibyditoiletvideocall.view.main.onboard.child_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentOnboardChild1Binding

class FragmentOnboardChild1 :Fragment(){
    private lateinit var binding: FragmentOnboardChild1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardChild1Binding.inflate(inflater, container, false)
        return binding.root
    }
}