package com.neko.hiepdph.skibyditoiletvideocall.view.main.othercall

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemOtherCallBinding

class AdapterOtherCall(
    private val onClickItem: (OtherCallModel) -> Unit,
    private val onClickAdsItem: (OtherCallModel) -> Unit
) : RecyclerView.Adapter<AdapterOtherCall.OtherCallViewHolder>() {
    private var data = mutableListOf<OtherCallModel>()

    fun setData(newData: MutableList<OtherCallModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    inner class OtherCallViewHolder(val binding: LayoutItemOtherCallBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherCallViewHolder {
        val binding =
            LayoutItemOtherCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OtherCallViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: OtherCallViewHolder, position: Int) {
        with(holder) {
            binding.imvAvatar.setImageResource(data[position].image)
            binding.root.clickWithDebounce {
                onClickItem(data[position])
            }
            if (position == 1 || position == 2) {
                binding.lockView.show()
                binding.lockView.clickWithDebounce {
                    onClickAdsItem?.invoke(data[position])
                }
            } else {
                binding.lockView.hide()
            }
        }
    }


}