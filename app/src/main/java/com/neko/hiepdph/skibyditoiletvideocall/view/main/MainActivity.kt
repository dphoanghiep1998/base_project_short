package com.neko.hiepdph.skibyditoiletvideocall.view.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.exoplayer2.ExoPlayer
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.ConnectionType
import com.neko.hiepdph.skibyditoiletvideocall.common.ConnectivityListener
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogInternet
import com.neko.hiepdph.skibyditoiletvideocall.common.NetworkUtils
import com.neko.hiepdph.skibyditoiletvideocall.common.buildMinVersionQ
import com.neko.hiepdph.skibyditoiletvideocall.common.changeStatusBarColor
import com.neko.hiepdph.skibyditoiletvideocall.common.createContext
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ActivityMainBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var connectivityListener: ConnectivityListener
    private var dialogInternet: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor()
        dialogInternet = DialogInternet(onPressPositive = {
            openConnectivitySetting()
        }).onCreateDialog(this)

        hideNavigationBar()
        checkInit()
        initData()
        observeConnectivityChange()
        observeConnectionType()
        registerConnectivityListener()
        if (viewModel.getPlayer() == null) {
            val mPlayer = ExoPlayer.Builder(this).setSeekForwardIncrementMs(15000).build()
            viewModel.setupPlayer(mPlayer)

        }

        if (viewModel.getPlayer2() == null) {
            val mPlayer = ExoPlayer.Builder(this).setSeekForwardIncrementMs(15000).build()
            viewModel.setupPlayer2(mPlayer)

        }
    }

    private fun checkInit() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)

        val controller = navHostFragment.navController
        if (!AppSharePreference.INSTANCE.getSetLangFirst(false)) {
            controller.navigate(R.id.fragmentLanguage)
        } else {
            if (!CustomApplication.app.isChangeLang) {
                controller.navigate(R.id.fragmentOnBoard)
            } else {
                controller.navigate(R.id.fragmentHome)
                CustomApplication.app.isChangeLang = false
            }
        }

    }

    private fun registerConnectivityListener() {
        connectivityListener = ConnectivityListener(applicationContext)
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        viewModel.addIsConnectivityChangedSource(connectivityListener.isConnectivityChanged)
        registerReceiver(connectivityListener, filter)
    }

    private fun observeConnectivityChange() {
        viewModel.mConnectivityChanged.observe(this) {
            onConnectivityChange()
        }
    }

    private fun observeConnectionType() {
        viewModel.typeNetwork.observe(this) {
            it?.let {
                if (it == ConnectionType.MOBILE || it == ConnectionType.WIFI) {
                    if (dialogInternet?.isShowing == true) {
                        dialogInternet?.dismiss()
                    }

                } else {
                    dialogInternet?.show()
                }
            }
        }
    }


    private fun initData() {
        if (!AppSharePreference.INSTANCE.getPassSetting(false)) {
            AppSharePreference.INSTANCE.saveListUnlockPos(
                mutableListOf(
                    2, 7, 12, 17, 22, 27
                )
            )

            AppSharePreference.INSTANCE.saveListVideoPlayed(
                mutableListOf(
                    1,
                    2,
                    3,
                    4,
                    5,
                    6,
                    7,
                    8,
                    9,
                    10,
                    11,
                    12,
                    13,
                    14,
                    15,
                    16,
                    17,
                    18,
                    19,
                    20,
                    21,
                    22,
                    23,
                    24,
                    25,
                    26,
                    27,
                    28,
                    29,
                    30,
                    31,
                    32,
                    33,
                    34,
                    35,
                    36,
                    37,
                    38,
                    39,
                )
            )
        }
    }


    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(
        newBase.createContext(
            Locale(
                AppSharePreference.INSTANCE.getSavedLanguage(
                    AppSharePreference.INSTANCE.getSavedLanguage(Locale.getDefault().language)
                )
            )
        )
    )

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    private fun hideNavigationBar() {
        val decorView: View = window.decorView
        val uiOptions: Int =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }

    private fun onConnectivityChange() {
        if (NetworkUtils.isWifiConnected(this)) {
            viewModel.typeNetwork.postValue(ConnectionType.WIFI)
        } else if (NetworkUtils.isMobileConnected(this)) {
            viewModel.typeNetwork.postValue(ConnectionType.MOBILE)
        } else {
            viewModel.typeNetwork.postValue(ConnectionType.UNKNOWN)
        }
    }

    private fun unregisterConnectivityListener() {
        viewModel.removeIsConnectivityChangedSource(connectivityListener.isConnectivityChanged)
        unregisterReceiver(connectivityListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterConnectivityListener()
        viewModel.resetPlayer()
        viewModel.resetPlayer2()
    }

    private fun openConnectivitySetting() {
        if (buildMinVersionQ()) startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        else startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

}