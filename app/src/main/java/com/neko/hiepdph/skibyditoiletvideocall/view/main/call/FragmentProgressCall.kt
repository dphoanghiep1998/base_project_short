package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentProgressCallBinding
import java.util.Locale


class FragmentProgressCall : Fragment() {

    private lateinit var binding: FragmentProgressCallBinding
    private val args: FragmentProgressCallArgs by navArgs()
    private var countDownTimer: CountDownTimer? = null
    private var mTimeLeftInMillis: Long = 0
    private var timeRunning = false
    private var animator = ValueAnimator.ofInt(0, 100)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProgressCallBinding.inflate(inflater, container, false)
        changeBackPressCallBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTimeLeftInMillis = args.totalTime
        startTimer()

    }
    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(args.totalTime, 1000L) {
            override fun onTick(p0: Long) {
                mTimeLeftInMillis = p0
                updateCountdownText()
            }

            override fun onFinish() {
                timeRunning = false
                val model =  OtherCallModel(0,R.drawable.ic_banner_progress_call,"Skibidi Toilet",R.raw.john_porn,4)
                val direction =
                    FragmentProgressCallDirections.actionFragmentProgressCallToFragmentCallScreen(
                        model
                    )
                findNavController().navigate(direction)
            }

        }
        timeRunning = true


        animator.interpolator = LinearInterpolator()
        animator.duration = args.totalTime

        animator.addUpdateListener {
            binding.progressChange.setProgress(it.animatedValue as Int)
        }
        countDownTimer?.start()
        animator.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        animator.pause()
        timeRunning = false
    }

    override fun onPause() {
        super.onPause()
        pauseTimer()
    }

    private fun updateCountdownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        binding.progressChange.setTime(timeLeftFormatted)
    }


}