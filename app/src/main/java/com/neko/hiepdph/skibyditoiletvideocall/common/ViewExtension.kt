package com.neko.hiepdph.skibyditoiletvideocall.common

import android.os.SystemClock
import android.view.View

fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun View.hide() {
    visibility = View.GONE
}
fun View.show(){
    visibility = View.VISIBLE
}
