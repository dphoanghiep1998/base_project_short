package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.databinding.DialogInternetBinding


class DialogInternet(
    private val onPressPositive: (() -> Unit),
) : DialogFragment(), BackPressDialogCallBack {
    private lateinit var binding: DialogInternetBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = DialogCallBack(requireContext(),this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_load_ads)
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
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogInternetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.btnAccept.clickWithDebounce {
            onPressPositive.invoke()
        }
        binding.containerMain.clickWithDebounce {

        }

        binding.root.clickWithDebounce {}
    }

    override fun shouldInterceptBackPress(): Boolean {
        return true
    }

    override fun onBackPressIntercepted() {

    }


}