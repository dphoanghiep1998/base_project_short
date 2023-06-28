package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallScheduleBinding

class FragmentCallSchedule : Fragment() {
    private lateinit var binding: FragmentCallScheduleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCallScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.wheelMinute.apply {
            wrapSelectorWheel = false
            maxValue = 59
            minValue = 0
            val displayValue = IntRange(minValue, maxValue).step(1).toList()
                .map { if (it.toString().length > 1) it.toString() else "0$it" }
            displayedValues = displayValue.toTypedArray()
            typeface = Typeface.createFromAsset(requireContext().assets, "app_font_600.ttf")
            setSelectedTypeface(
                Typeface.createFromAsset(
                    requireContext().assets, "app_font_600.ttf"
                )
            )
        }

        binding.wheelSecond.apply {
            wrapSelectorWheel = false
            maxValue = 59
            minValue = 0
            val displayValue = IntRange(minValue, maxValue).step(1).toList()
                .map { if (it.toString().length > 1) it.toString() else "0$it" }
            displayedValues = displayValue.toTypedArray()
            typeface = Typeface.createFromAsset(requireContext().assets, "app_font_600.ttf")
            setSelectedTypeface(
                Typeface.createFromAsset(
                    requireContext().assets, "app_font_600.ttf"
                )
            )
        }

        binding.imvCallVideo.clickWithDebounce {
            navigateToPage(R.id.fragmentCallSchedule, R.id.fragmentProgressCall)
        }
    }
}