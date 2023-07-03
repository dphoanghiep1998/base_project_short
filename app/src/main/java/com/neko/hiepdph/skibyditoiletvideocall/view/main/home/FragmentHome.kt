package com.neko.hiepdph.skibyditoiletvideocall.view.main.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        binding.imvToiletVideo.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentVideoToilet)
        }
        binding.call.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentCall)
        }
        binding.videoCall.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentCallScreen)
        }
        binding.message.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentMessage)
        }
        binding.imvOtherCall.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentOtherCall)
        }
        binding.gallery.clickWithDebounce {
            navigateToPage(R.id.fragmentHome,R.id.fragmentGallery)
        }
    }


}