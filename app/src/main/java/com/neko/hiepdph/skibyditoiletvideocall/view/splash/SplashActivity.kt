package com.neko.hiepdph.skibyditoiletvideocall.view.splash

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gianghv.libads.InterstitialPreloadAdManager
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.gianghv.libads.NativeAdsManager
import com.gianghv.libads.RewardAdsManager
import com.gianghv.libads.utils.AdsConfigUtils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.neko.hiepdph.skibyditoiletvideocall.BuildConfig
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogFragmentLoadingInterAds
import com.neko.hiepdph.skibyditoiletvideocall.common.changeStatusBarColor
import com.neko.hiepdph.skibyditoiletvideocall.common.createContext
import com.neko.hiepdph.skibyditoiletvideocall.common.isInternetAvailable
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ActivitySplashBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.system.exitProcess

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private var status = 0
    private val initDone = AppSharePreference.INSTANCE.getSetLangFirst(false)
    private lateinit var app: CustomApplication
    private var dialogLoadingInterAds: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as CustomApplication
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogLoadingInterAds = DialogFragmentLoadingInterAds().onCreateDialog(this)
        fetchRemoteConfig()
        initAds()
        handleAds()
        changeStatusBarColor()
        CustomApplication.app.adsShowed = false
    }
    private fun fetchRemoteConfig() {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                val defPos = FirebaseRemoteConfig.getInstance()
                    .getLong(AdsConfigUtils.DEF_POS)
                AdsConfigUtils(this).putDefConfigNumber(defPos.toInt())
                checkFinishPermission()
            }.addOnFailureListener {
                AdsConfigUtils(this).putDefConfigNumber(AdsConfigUtils.DEF_POS_VALUE)
                checkFinishPermission()
            }
    }

    private fun checkFinishPermission() {
        status++
        checkAdsLoad()
    }

    private fun initAds() {
        CustomApplication.app.interstitialPreloadAdManager = InterstitialPreloadAdManager(
            this,
            BuildConfig.inter_splash_id,
            BuildConfig.inter_splash_id2,
        )


        CustomApplication.app.mNativeAdManagerIntro = NativeAdsManager(
            this,
            BuildConfig.native_intro_id,
            BuildConfig.native_intro_id2,
        )

        CustomApplication.app.mNativeAdManagerHome = NativeAdsManager(
            this,
            BuildConfig.native_video_id,
            BuildConfig.native_video_id2,
        )
    }

    private val callback = object : InterstitialPreloadAdManager.InterstitialAdListener {
        override fun onClose() {
            CustomApplication.app.adsShowed = true
            navigateMain()
        }

        override fun onError() {
            CustomApplication.app.adsShowed = true
            navigateMain()
        }
    }

    private fun checkAdsLoad() {
        if (status == 2) {
            if (CustomApplication.app.interstitialPreloadAdManager?.loadAdsSuccess == true) {
                handleAtLeast3Second(action = {
                    lifecycleScope.launchWhenResumed {
                        dialogLoadingInterAds?.show()
                        delay(500)
                        CustomApplication.app.interstitialPreloadAdManager?.showAds(
                            this@SplashActivity, callback
                        )
                    }
                })

            } else {
                navigateMain()
            }
        }
    }


    private fun handleAds() {
        if (!isInternetAvailable(this)) {
            handleWhenAnimationDone(action = {
                navigateMain()
            })
        } else {
            Handler().postDelayed({
                loadSplashAds()
            }, 1000)
        }
    }

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(
        newBase.createContext(
            Locale(
                AppSharePreference.INSTANCE.getSavedLanguage(
                    Locale.getDefault().language
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





    private fun handleAtLeast3Second(action: () -> Unit) {
        Handler().postDelayed({
            action.invoke()
        }, 1000)
    }

    private fun loadSplashAds() {
        CustomApplication.app.interstitialPreloadAdManager?.loadAds(onAdLoadFail = {
            status++
            CustomApplication.app.interstitialPreloadAdManager?.loadAdsSuccess = false
            if (!CustomApplication.app.adsShowed) {
                checkAdsLoad()
            }
        }, onAdLoader = {
            status++
            CustomApplication.app.interstitialPreloadAdManager?.loadAdsSuccess = true
            if (!CustomApplication.app.adsShowed) {
                checkAdsLoad()
            }
        })
    }

    private fun handleWhenAnimationDone(action: () -> Unit) {
        Handler().postDelayed({
            action.invoke()

        }, 3000)

    }

    private fun navigateMain() {
        val i = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        exitProcess(-1)

    }

    override fun onDestroy() {
        super.onDestroy()
        InterstitialPreloadAdManager.isShowingAds = false
        InterstitialSingleReqAdManager.isShowingAds = false
        RewardAdsManager.isShowing = false
        CustomApplication.app.interstitialPreloadAdManager = null
        dialogLoadingInterAds?.dismiss()

    }

}