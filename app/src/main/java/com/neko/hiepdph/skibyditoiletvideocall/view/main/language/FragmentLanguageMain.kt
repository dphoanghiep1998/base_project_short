package com.neko.hiepdph.skibyditoiletvideocall.view.main.language

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.skibyditoiletvideocall.BuildConfig
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gianghv.libads.NativeAdsManager
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference.Companion.INSTANCE
import com.neko.hiepdph.skibyditoiletvideocall.common.NativeTypeEnum
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.pushEvent
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.common.showNativeAds
import com.neko.hiepdph.skibyditoiletvideocall.common.supportDisplayLang
import com.neko.hiepdph.skibyditoiletvideocall.common.supportedLanguages
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentLanguageBinding
import java.util.Locale

class FragmentLanguageMain : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    private var currentLanguage = Locale.getDefault().language
    private var adapter: AdapterLanguage? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
    }

    private fun initView() {
        initRecyclerView()
        initButton()
    }

    private fun initButton() {

        binding.btnCheck.clickWithDebounce {
            Log.d("TAG", "initButton: ")
            requireContext().pushEvent("click_language_save2")
            INSTANCE.saveLanguage(currentLanguage)
            startActivity(requireActivity().intent)
            CustomApplication.app.isChangeLang = true
            requireActivity().finish()
        }
    }

    private fun initRecyclerView() {
        val mLanguageList: MutableList<Any> = supportedLanguages().toMutableList()
        val mDisplayLangList: MutableList<Any> = supportDisplayLang().toMutableList()
        handleUnSupportLang(mLanguageList)
        adapter = AdapterLanguage(requireContext(), onClickLanguage = {
            currentLanguage = it.language
        })
        adapter?.setData(mLanguageList, mDisplayLangList)
        binding.rcvLanguage.adapter = adapter
        binding.rcvLanguage.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        adapter?.setCurrentLanguage(getCurrentLanguage())
        showNativeAds(binding.nativeAdMediumView, {
            binding.btnCheck.show()
            binding.loadingAds.hide()
        }, {
            binding.btnCheck.show()
            binding.loadingAds.hide()
        }, type = NativeTypeEnum.GALLERY
        )
    }

    private fun handleUnSupportLang(mLanguageList: MutableList<Any>) {
        var support = false
        mLanguageList.forEachIndexed { index, item ->
            if (item is Locale) {
                if (item.language == currentLanguage) {
                    support = true
                }
            }
        }
        if (!support) {
            currentLanguage = (mLanguageList[0] as Locale).language
        }
    }

    private fun getCurrentLanguage(): String {
        return INSTANCE.getSavedLanguage(Locale.getDefault().language)
    }


    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
}