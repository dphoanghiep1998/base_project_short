package com.neko.hiepdph.skibyditoiletvideocall.view.main.home

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
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MonsterModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ItemHomeNativeAdsBinding
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemHomeBinding

class AdapterHome(
    private val onClickItem: (MonsterModel, position: Int) -> Unit,
    private val onClickRewardAdsItem: (MonsterModel, position: Int) -> Unit
//    private val onClickLockItem: (MonsterModel, pos: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data = mutableListOf<MonsterModel>()
    private var nativeInside: NativeAd? = null


    fun setData(rawData: MutableList<MonsterModel>) {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val binding: LayoutItemHomeBinding = LayoutItemHomeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HomeViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

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

    private fun checkPlayed(position: Int): Boolean {
        val dataPlayed =
            AppSharePreference.INSTANCE.getListVideoPlayed(mutableListOf()).toMutableList()
        return position !in dataPlayed
    }


    fun reloadData(position: Int) {
        notifyItemChanged(position)
    }
}