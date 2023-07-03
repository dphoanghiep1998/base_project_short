package com.neko.hiepdph.skibyditoiletvideocall.view.main.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MessageModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemScriptedBinding

class AdapterScripted(private val onClickScriptedItem: (MessageModel) -> Unit) :
    RecyclerView.Adapter<AdapterScripted.ScriptedViewHolder>() {
    private var data: MutableList<MessageModel> = mutableListOf()

    fun setData(listMessage: MutableList<MessageModel>) {
        data.clear()
        data.addAll(listMessage)
        notifyDataSetChanged()
    }

    inner class ScriptedViewHolder(val binding: LayoutItemScriptedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScriptedViewHolder {
        val binding =
            LayoutItemScriptedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScriptedViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ScriptedViewHolder, position: Int) {
        with(holder) {
            binding.tvScripted.text = data[position].contentSent
            binding.root.clickWithDebounce(1500) {
                onClickScriptedItem(data[position])
            }
        }
    }


}