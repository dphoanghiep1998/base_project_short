package com.neko.hiepdph.skibyditoiletvideocall.view.main.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.data.MonsterModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ItemHomeNativeAdsBinding
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemHomeBinding

class AdapterHome(
    private val onClickItem: (MonsterModel, position: Int) -> Unit,
    private val onClickRewardAdsItem: (MonsterModel, position: Int) -> Unit
//    private val onClickLockItem: (MonsterModel, pos: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data = mutableListOf<Any>()
    private var nativeInside: NativeAd? = null


    fun setData(rawData: MutableList<Any>) {
        data.clear()
        data.addAll(rawData)
        notifyDataSetChanged()
    }

    fun insertAds(ads: NativeAd) {
        nativeInside = ads
        notifyItemChanged(3)
        notifyItemChanged(37)
    }


    inner class HomeViewHolder(val binding: LayoutItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun getItemViewType(position: Int): Int {
        return if (data[position] == "ads") {
            0
        } else {
            1
        }
    }

    inner class InfoNativeAdsViewHolder(val binding: ItemHomeNativeAdsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = ItemHomeNativeAdsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                InfoNativeAdsViewHolder(binding)
            }

            else -> {
                val binding: LayoutItemHomeBinding = LayoutItemHomeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HomeViewHolder(binding)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                with(holder as HomeViewHolder) {
                    val item = data[position] as MonsterModel
                    binding.imvMonster.setImageResource(item.image)
                    if (!checkPlayed(item.id)) {
                        binding.containerLock.show()
                        binding.icStar.hide()
                    } else {
                        binding.containerLock.hide()
                        binding.icStar.show()
                    }

                    if (item.isRewardContent) {
                        binding.icAd.show()
                        binding.icHot.show()
                    } else {
                        binding.icAd.hide()
                        binding.icHot.hide()
                    }

                    if (item.isRewardContent) {
                        if (checkPlayed(item.id)) {
                            binding.imvMonster.clickWithDebounce {
                                onClickRewardAdsItem(item, position)
                            }
                            binding.icHot.clickWithDebounce {
                                onClickRewardAdsItem(item, position)
                            }
                            binding.icStar.clickWithDebounce {
                                onClickRewardAdsItem(item, position)
                            }
                            binding.icAd.clickWithDebounce {
                                onClickRewardAdsItem(item, position)
                            }
                        } else {
                            binding.imvMonster.clickWithDebounce {}
                            binding.icHot.clickWithDebounce {}
                            binding.icStar.clickWithDebounce {}
                            binding.icAd.clickWithDebounce {}
                        }

                    } else {
                        if (checkPlayed(item.id)) {
                            binding.imvMonster.clickWithDebounce {
                                onClickItem(item, position)
                            }
                        } else {
                            binding.imvMonster.clickWithDebounce {}
                        }
                    }
                }

            }

            else -> {
                with(holder as InfoNativeAdsViewHolder) {
                    if (nativeInside != null) {
                        binding.nativeAdMediumView.showShimmer(false)
                        binding.nativeAdMediumView.setNativeAd(nativeInside!!)
                        binding.nativeAdMediumView.isVisible = true
                        binding.root.visibility = View.VISIBLE
                    } else {
                        binding.nativeAdMediumView.isVisible = false
                        binding.root.visibility = View.GONE
                    }

                }
            }
        }
    }

    private fun checkPlayed(position: Int): Boolean {
        val dataPlayed =
            AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf()).toMutableList()
        return position !in dataPlayed
    }


    fun reloadData(position: Int) {
        notifyItemChanged(position)
    }
}