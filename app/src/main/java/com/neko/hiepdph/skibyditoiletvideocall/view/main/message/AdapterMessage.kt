package com.neko.hiepdph.skibyditoiletvideocall.view.main.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.skibyditoiletvideocall.data.model.MessageModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemMessageReceivedBinding
import com.neko.hiepdph.skibyditoiletvideocall.databinding.LayoutItemMessageSendBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AdapterMessage(private val onLoadDone: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data: MutableList<MessageModel> = mutableListOf()
    private var loading = false
    private var scope = CoroutineScope(Dispatchers.Main)
    private var mRecyclerView: RecyclerView? = null
    private var job: Job? = null
    fun insertMessage(messageModel: MessageModel) {
        data.add(messageModel)
        notifyItemInserted(data.size)
        mRecyclerView?.smoothScrollToPosition(data.size)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun setLoading(status: Boolean) {
        loading = status
        notifyItemChanged(data.size - 1)
    }

    fun insertReceivedMessage(messageModel: MessageModel) {
        if (loading) {
            job?.cancel()
            data.removeAt(data.size - 2)
        }
        data.add(messageModel)
        notifyItemInserted(data.size)
        mRecyclerView?.smoothScrollToPosition(data.size)
    }


    inner class MessageReceivedViewHolder(val binding: LayoutItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    inner class MessageSendViewHolder(val binding: LayoutItemMessageSendBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isSent) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding = LayoutItemMessageSendBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return MessageSendViewHolder(binding)
            }

            1 -> {
                val binding = LayoutItemMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return MessageReceivedViewHolder(binding)
            }

            else -> {
                throw Exception()
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as MessageSendViewHolder) {
                    binding.tvSend.text = data[position].contentSent
                }
            }

            1 -> {
                with(holder as MessageReceivedViewHolder) {
                    job = scope.launch {
                        if (!loading) {
                            this.cancel()
                            return@launch
                        }
                        delay(500)
                        binding.tvReceived.text = "."
                        delay(500)
                        binding.tvReceived.text = ".."
                        delay(500)
                        binding.tvReceived.text = "..."
                        binding.tvReceived.text = data[data.size - 1].contentReceived
                        onLoadDone?.invoke()
                        loading = false
                    }
                    job?.start()
                }
            }
        }
    }

    private fun delayAnimation(binding: LayoutItemMessageReceivedBinding) {

    }

}