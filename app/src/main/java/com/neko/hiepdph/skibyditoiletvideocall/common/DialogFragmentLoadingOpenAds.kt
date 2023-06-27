package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.skibyditoiletvideocall.R

class DialogFragmentLoadingOpenAds : DialogFragment() {
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
        mDialog.setContentView(R.layout.dialog_load_open_ads)
        mDialog.setCancelable(false)
        mDialog.setCanceledOnTouchOutside(false)
        window.attributes = wlp
        mDialog.window!!.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        mDialog.window?.setBackgroundDrawableResource(R.color.transparent)

        mDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        return mDialog
    }
}

