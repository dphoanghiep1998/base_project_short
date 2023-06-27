package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.skibyditoiletvideocall.R


class DialogFragmentLoadingInterAds : DialogFragment() {
    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
            Log.d("TAG", "onBackPressIntercepted: ")
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mDialog = DialogCallBack(requireContext(),callback)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = mDialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        mDialog.setContentView(R.layout.dialog_load_ads)
        mDialog.setCancelable(false)
        mDialog.setCanceledOnTouchOutside(false)
        window.attributes = wlp
        mDialog.window!!.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        mDialog.window?.setBackgroundDrawableResource(R.color.black)
        mDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        return mDialog
    }
}

interface BackPressDialogCallBack {
    fun shouldInterceptBackPress(): Boolean
    fun onBackPressIntercepted()
}

open class DialogCallBack(
    context: Context, private val callback: BackPressDialogCallBack
) : Dialog(context) {

    override fun onBackPressed() {
        if (callback.shouldInterceptBackPress()) callback.onBackPressIntercepted()
        else super.onBackPressed()
    }
}