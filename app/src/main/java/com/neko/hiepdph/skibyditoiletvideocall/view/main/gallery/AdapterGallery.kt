package com.neko.hiepdph.skibyditoiletvideocall.view.main.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.data.model.GalleryModel
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MonsterModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemGalleryBinding
import java.text.SimpleDateFormat

class AdapterGallery(
    private val imageData:MutableList<MonsterModel>,
    private val onClickItem: (GalleryModel) -> Unit,
    private val onClickShare: (GalleryModel) -> Unit,
    private val onClickDeleteItem: (GalleryModel) -> Unit
) : RecyclerView.Adapter<AdapterGallery.GalleryViewHolder>() {

    private var data = mutableListOf<GalleryModel>()

    fun setData(newData: List<GalleryModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    inner class GalleryViewHolder(val binding: LayoutItemGalleryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding =
            LayoutItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        with(holder) {
            val item = data[position]
            when (item.videoType) {
                4 -> {
                    binding.imvAvatar.setImageResource(imageData[item.videoId].image)
                }

                0 -> {
                    binding.imvAvatar.setImageResource(R.drawable.ic_gallrey_momo)
                }

                1 -> {
                    binding.imvAvatar.setImageResource(R.drawable.ic_gallrey_valak)
                }

                2 -> {
                    binding.imvAvatar.setImageResource(R.drawable.ic_gallrey_wednesday)
                }

                3 -> {
                    binding.imvAvatar.setImageResource(R.drawable.ic_gallrey_mmll)
                }
            }
            binding.tvVideo.text =
                String.format(itemView.context.getString(R.string.video), item.id.toString())
            binding.tvTime.text = getDateConvertedToResult(item.time)
//            binding.share.clickWithDebounce {
//                onClickShare.invoke(item)
//            }

            binding.delete.clickWithDebounce {
                onClickDeleteItem.invoke(item)
            }

            binding.root.clickWithDebounce {
                onClickItem.invoke(item)

            }
        }
    }
}

fun getDateConvertedToResult(time: Long): String {
    val formatter = SimpleDateFormat("MMM-dd-yyyy | HH:mm")
    return formatter.format(time).toString()
}