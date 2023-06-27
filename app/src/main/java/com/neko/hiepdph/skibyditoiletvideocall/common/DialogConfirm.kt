package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.databinding.DialogConfirmBinding


class DialogConfirm(
    context: Context, private val onPressPositive: (() -> Unit),private val isCloseApp :Boolean = false,
) : Dialog(context) {
    private lateinit var binding: DialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.transparent)))

        window!!.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        binding = DialogConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.setLayout(width, height)
        initView()
    }


    private fun initView() {
        if(!isCloseApp){
            binding.tvContent.text = context.getString(R.string.confirm)
            binding.btnAccept.text = context.getString(R.string.watch)
        } else{
            binding.tvContent.text = context.getString(R.string.confirm_close)
            binding.btnAccept.text = context.getString(R.string.yes)
        }
        binding.btnDeny.setOnClickListener {
            dismiss()
        }

        binding.btnAccept.clickWithDebounce {
            onPressPositive.invoke()
            dismiss()
        }
        binding.containerMain.clickWithDebounce {

        }

        binding.root.clickWithDebounce {
            dismiss()
        }
    }


}