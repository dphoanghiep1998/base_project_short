package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentScreenAcceptBinding


class FragmentScreenAccept : Fragment() {

    private lateinit var binding: FragmentScreenAcceptBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScreenAcceptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.btnDecline.clickWithDebounce{
            navigateToPage(R.id.fragmentScreenAccept,R.id.fragmentCallClose)
        }
    }


}