package com.neko.hiepdph.skibyditoiletvideocall.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.common.supportDisplayLang
import com.neko.hiepdph.skibyditoiletvideocall.common.supportedLanguages
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentHomeBinding
import java.util.Locale
import kotlin.system.exitProcess

class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        changeBackPressCallBack()
        return binding.root
    }
    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishActivity(0)
                exitProcess(-1)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initFlag() {
        val mLanguageList = supportedLanguages().toMutableList()
        val mDisplayLangList = supportDisplayLang().toMutableList()

        val currentLang = AppSharePreference.INSTANCE.getSavedLanguage(Locale.getDefault().language)

        mLanguageList.forEachIndexed { index, item ->
            if (item.language == currentLang) {
                binding.imvFlag.setImageResource(mDisplayLangList[index].second)
            }
        }
        binding.imvFlag.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentLanguageMain)
        }
    }

    private fun initView() {
        initFlag()
        initButton()
    }

    private fun initButton() {
        binding.imvToiletVideo.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentVideoToilet)
        }
        binding.call.clickWithDebounce {
            val model =  OtherCallModel(0,R.drawable.ic_banner_progress_call,"Skibidi Toilet",R.raw.john_porn,4)
            navigateToPage(R.id.fragmentHome, R.id.fragmentCall)
        }
        binding.videoCall.clickWithDebounce {
            val model = OtherCallModel(0,R.drawable.ic_banner_progress_call,"Skibidi Toilet",R.raw.john_porn,4)
            val direction = FragmentHomeDirections.actionFragmentHomeToFragmentCallScreen(model)
            findNavController().navigate(direction)
        }
        binding.message.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentMessage)
        }
        binding.imvOtherCall.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentOtherCall)
        }
        binding.gallery.clickWithDebounce {
            navigateToPage(R.id.fragmentHome, R.id.fragmentGallery)
        }
    }


}