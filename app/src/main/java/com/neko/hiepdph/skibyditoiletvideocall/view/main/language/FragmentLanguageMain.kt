package com.neko.hiepdph.skibyditoiletvideocall.view.main.language

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference.Companion.INSTANCE
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateBack
import com.neko.hiepdph.skibyditoiletvideocall.common.pushEvent
import com.neko.hiepdph.skibyditoiletvideocall.common.supportDisplayLang
import com.neko.hiepdph.skibyditoiletvideocall.common.supportedLanguages
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentLanguageBinding
import java.util.Locale

class FragmentLanguageMain : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    private var currentLanguage = INSTANCE.getSavedLanguage(Locale.getDefault().language)
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

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               navigateBack(R.id.fragmentLanguageMain)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }
    private fun initButton() {
        binding.btnCheck.clickWithDebounce {
            requireContext().pushEvent("click_language_save")
            CustomApplication.app.isChangeLanga = true
            INSTANCE.saveLanguage(currentLanguage)
            startActivity(requireActivity().intent)
            requireActivity().finish()
            INSTANCE.saveTimeLogin(0)
            INSTANCE.saveRateTimeLogin(0)
        }
    }

    private fun initRecyclerView() {
        val mLanguageList: MutableList<Any> = supportedLanguages().toMutableList()
        val mDisplayLangList: MutableList<Any> = supportDisplayLang().toMutableList()

        mLanguageList.add(1, "adsApp")
        mDisplayLangList.add(1, "adsApp")

        adapter = AdapterLanguage(requireContext(), onCLickItem = {
            Log.d("TAG", "initRecyclerView: ")
            currentLanguage = it.language
        })
        adapter?.setData(mLanguageList, mDisplayLangList)
        binding.rcvLanguage.adapter = adapter
        binding.rcvLanguage.layoutManager = LinearLayoutManager(requireContext())
        adapter?.setCurrentLanguage(getCurrentLanguage())

        insertAds()
    }



    private fun getCurrentLanguage(): String {
        return INSTANCE.getSavedLanguage(Locale.getDefault().language)
    }

    private fun insertAds() {
        CustomApplication.app.nativeADLanguage?.observe(viewLifecycleOwner) {
            it?.let {
                adapter?.insertAds(it)
            }
        }
        if (CustomApplication.app.nativeADLanguage?.value == null) {
            CustomApplication.app.mNativeAdManagerLanguage?.loadAds(onLoadSuccess = {
                CustomApplication.app.nativeADLanguage?.value = it
            })
        }
    }

}