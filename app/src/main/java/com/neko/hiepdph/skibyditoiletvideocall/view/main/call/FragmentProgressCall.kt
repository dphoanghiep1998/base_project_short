package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentProgressCallBinding


class FragmentProgressCall : Fragment() {

    private lateinit var binding: FragmentProgressCallBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProgressCallBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val animator = ValueAnimator.ofInt(0,100)

        animator.interpolator = LinearInterpolator()
        animator.duration = 5000L
        animator.addUpdateListener {
            binding.progressChange.setProgress(it.animatedValue as Int)
        }
        animator.start()

    }


}