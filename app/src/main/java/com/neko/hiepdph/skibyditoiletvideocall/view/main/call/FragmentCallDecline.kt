package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallDeclineBinding


class FragmentCallDecline : Fragment() {

    private lateinit var binding: FragmentCallDeclineBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallDeclineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
    }

    private fun initView() {
        val countDownTimer = object :CountDownTimer(2000,1000){
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                binding.containerBroken.show()
            }

        }
        countDownTimer.start()
        binding.containerBroken.clickWithDebounce {
            navigateToPage(R.id.fragmentCallDecline, R.id.fragmentHome)
        }
    }


    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

}