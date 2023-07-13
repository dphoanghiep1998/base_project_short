package com.neko.hiepdph.skibyditoiletvideocall.view.main.language

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ItemLanguageNativeAdsBinding
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemLanguageBinding

import java.util.Collections
import java.util.Locale

class AdapterLanguage(private val context: Context, private val onCLickItem: (Locale) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mRecyclerView: RecyclerView? = null
    private var insideNativeAd: NativeAd? = null
    private var listDataLang = mutableListOf<Any>()
    private var listDataDisplay = mutableListOf<Any>()
    private val diffCallback = DiffCallback(listDataLang, mutableListOf())

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun setData(mListLang: MutableList<Any>, mListDisplay: MutableList<Any>) {
        diffCallback.newList = mListLang.toMutableList()
        val diffUtil = DiffUtil.calculateDiff(diffCallback, true)
        diffUtil.dispatchUpdatesTo(this)
        listDataLang.clear()
        listDataLang.addAll(mListLang)
        listDataDisplay.clear()
        listDataDisplay.addAll(mListDisplay)
    }

    private fun swap(secondPosition: Int) {
        Collections.swap(listDataLang, 0, secondPosition)
        Collections.swap(listDataDisplay, 0, secondPosition)

        notifyItemChanged(0)
        notifyItemChanged(secondPosition)
        mRecyclerView?.smoothScrollToPosition(0)
    }

    fun setCurrentLanguage(language: String) {
        var pos = 0
        listDataLang.forEachIndexed { index, l ->
            if (l is Locale) {
                if (l.language == language) {
                    pos = index
                    onCLickItem.invoke(l)
                }
            }
        }
        swap(pos)
    }


    override fun getItemCount(): Int {
        return listDataLang.size
    }

    override fun getItemViewType(position: Int): Int {
        if (listDataLang[position] == "adsApp") {
            return 1
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as LanguageViewHolder) {
                    if (this.adapterPosition == 0) {
                        binding.tvCountry.setTextColor(
                            ContextCompat.getColor(
                                context, R.color.white
                            )
                        )
                        binding.container.background = ContextCompat.getDrawable(
                            context, R.drawable.infor_language_selected
                        )
                    } else {
                        binding.tvCountry.setTextColor(
                            ContextCompat.getColor(
                                context, R.color.white
                            )
                        )
                        binding.container.background = ContextCompat.getDrawable(
                            context, R.drawable.infor_language
                        )
                    }
                    with(listDataLang[position] as Locale) {
                        val display = listDataDisplay[position] as Pair<Int, Int>
                        binding.tvCountry.text = context.getString(display.first)
                        binding.imvFlag.setImageDrawable(
                            ContextCompat.getDrawable(
                                context, display.second
                            )
                        )
                        binding.root.clickWithDebounce {
                            if (adapterPosition == 0) {
                                return@clickWithDebounce
                            }
                            swap(adapterPosition)
                            onCLickItem(this)
                        }
                    }
                }

            }

            1 -> {
                with(holder as LanguageNativeAdsViewHolder) {
                    if (insideNativeAd != null) {
                        binding.nativeAdMediumView.showShimmer(false)
                        binding.nativeAdMediumView.setNativeAd(insideNativeAd!!)
                        binding.nativeAdMediumView.visibility = View.VISIBLE
                        binding.root.show()
                    } else {
                        binding.root.hide()
                        with(binding.nativeAdMediumView) {
                            visibility = View.GONE
                            showShimmer(true)
                        }
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = LayoutItemLanguageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LanguageViewHolder(binding)
            }

            else -> {
                val binding = ItemLanguageNativeAdsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LanguageNativeAdsViewHolder(binding)
            }

        }
    }

    fun insertAds(ads: NativeAd) {
        insideNativeAd = ads
        notifyItemChanged(1)
    }

    inner class LanguageViewHolder(val binding: LayoutItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LanguageNativeAdsViewHolder(val binding: ItemLanguageNativeAdsBinding) :
        RecyclerView.ViewHolder(binding.root)
}