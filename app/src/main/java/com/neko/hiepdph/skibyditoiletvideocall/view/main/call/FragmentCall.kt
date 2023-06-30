package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallBinding


class FragmentCall : Fragment() {
    private lateinit var binding: FragmentCallBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallBinding.inflate(inflater, container, false)
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
        binding.btnSchedule.clickWithDebounce {
            navigateToPage(R.id.fragmentCall, R.id.fragmentCallSchedule)
        }

        binding.btn15.clickWithDebounce {
            val direction =
                FragmentCallDirections.actionFragmentCallToFragmentProgressCall(15 * 1000)
            findNavController().navigate(direction)
        }

        binding.btn30.clickWithDebounce {
            val direction =
                FragmentCallDirections.actionFragmentCallToFragmentProgressCall(30 * 1000)
            findNavController().navigate(direction)

        }

        binding.btn60.clickWithDebounce {
            val direction =
                FragmentCallDirections.actionFragmentCallToFragmentProgressCall(60 * 1000)
            findNavController().navigate(direction)

        }

        binding.btnCallNow.clickWithDebounce {
            navigateToPage(R.id.fragmentCall, R.id.fragmentCallScreen)
        }

        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
    }


}