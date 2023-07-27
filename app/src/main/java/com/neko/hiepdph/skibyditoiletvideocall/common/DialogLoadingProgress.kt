package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.skibyditoiletvideocall.R


class DialogLoadingProgress  {

    fun onCreateDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_load_video)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        window.attributes = wlp
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        return dialog
    }
}