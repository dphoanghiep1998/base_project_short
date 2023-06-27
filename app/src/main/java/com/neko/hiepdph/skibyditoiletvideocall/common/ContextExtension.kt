package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.neko.hiepdph.skibyditoiletvideocall.R
import java.util.*

fun Context.createContext(newLocale: Locale): Context =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        createContextAndroidN(newLocale)
    } else {

        createContextLegacy(newLocale)
    }


private fun Context.createContextAndroidN(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(newLocale)
    return createConfigurationContext(configuration)
}
private fun Context.createContextLegacy(newLocale: Locale): Context {
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.locale = newLocale
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return this
}

fun Activity.changeStatusBarColor() {
    val window: Window = window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, R.color.black)
}