package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.databinding.DialogInternetBinding


class DialogInternet(
    private val onPressPositive: (() -> Unit),
) {

    fun onCreateDialog(activity: Activity): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_internet)

        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        window.attributes = wlp
        dialog.window!!.setBackgroundDrawableResource(R.color.black)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        initView(dialog)
        return dialog
    }

    private fun initView(dialog: Dialog) {
        dialog.findViewById<TextView>(R.id.btnAccept).clickWithDebounce {
            onPressPositive.invoke()
        }
        dialog.findViewById<ConstraintLayout>(R.id.container_main).clickWithDebounce {

        }

    }


}