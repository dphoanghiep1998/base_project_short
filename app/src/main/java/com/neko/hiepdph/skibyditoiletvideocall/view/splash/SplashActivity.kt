package com.neko.hiepdph.skibyditoiletvideocall.view.splash

import android.annotation.SuppressLint
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
import com.neko.hiepdph.skibyditoiletvideocall.BuildConfig
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogFragmentLoadingInterAds
import com.neko.hiepdph.skibyditoiletvideocall.common.changeStatusBarColor
import com.neko.hiepdph.skibyditoiletvideocall.common.createContext
import com.neko.hiepdph.skibyditoiletvideocall.common.isInternetAvailable
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ActivitySplashBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.system.exitProcess


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private var status = 0
    private val initDone = AppSharePreference.INSTANCE.getSetLangFirst(false)
    private lateinit var app: CustomApplication
    private var dialogLoadingInterAds: DialogFragmentLoadingInterAds? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as CustomApplication
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogLoadingInterAds = DialogFragmentLoadingInterAds()
        initAds()
        handleAds()
        changeStatusBarColor()
    }

    private fun initAds() {
        CustomApplication.app.interstitialPreloadAdManager = InterstitialPreloadAdManager(
            this,
            BuildConfig.inter_splash_id,
            BuildConfig.inter_splash_id2,
            BuildConfig.inter_splash_id3,
        )


        CustomApplication.app.mNativeAdManagerIntro = NativeAdsManager(
            this,
            BuildConfig.native_intro_id,
            BuildConfig.native_intro_id2,
            BuildConfig.native_intro_id3,
        )

        CustomApplication.app.mNativeAdManagerHome = NativeAdsManager(
            this,
            BuildConfig.native_video_id,
            BuildConfig.native_video_id2,
            BuildConfig.native_video_id3,
        )
    }

    private val callback = object : InterstitialPreloadAdManager.InterstitialAdListener {
        override fun onClose() {
            navigateMain()
        }

        override fun onError() {
            navigateMain()
        }
    }

    private fun checkAdsLoad() {

        if (initDone && status >= 2) {
            if (CustomApplication.app.interstitialPreloadAdManager?.loadAdsSuccess == true) {
                handleAtLeast3Second(action = {
                    lifecycleScope.launchWhenResumed {
                        dialogLoadingInterAds?.show(
                            supportFragmentManager, dialogLoadingInterAds?.tag
                        )
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(500)
                            CustomApplication.app.interstitialPreloadAdManager?.showAds(
                                this@SplashActivity, callback
                            )
                        }
                    }

                })
            } else {
                navigateMain()
            }
        } else if (status >= 3) {
            if (CustomApplication.app.interstitialPreloadAdManager?.loadAdsSuccess == true) {
                handleAtLeast3Second(action = {
                    lifecycleScope.launchWhenResumed {
                        dialogLoadingInterAds?.show(
                            supportFragmentManager, dialogLoadingInterAds?.tag
                        )
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
            if (initDone) {
                Handler().postDelayed({
                    loadSplashAds()
                    loadNativeHomeAds()
                }, 1000)
            } else {
                Handler().postDelayed({
                    loadSplashAds()
                    loadNativeIntroAds()
                    loadNativeHomeAds()
                }, 1000)
            }
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


    private fun loadNativeIntroAds() {
        CustomApplication.app.mNativeAdManagerIntro?.loadAds(onLoadSuccess = {
            app.nativeADIntro?.value = it
            status++
            if (!CustomApplication.app.adsShowed) {
                checkAdsLoad()
            }
        }, onLoadFail = {
            status++
            if (!CustomApplication.app.adsShowed) {
                checkAdsLoad()
            }
        })
    }

    private fun loadNativeHomeAds() {
        CustomApplication.app.mNativeAdManagerHome?.loadAds(onLoadSuccess = {
            app.nativeADHome?.value = it
            status++
            if (!CustomApplication.app.adsShowed) {
                checkAdsLoad()
            }
        }, onLoadFail = {
            status++
            if (!CustomApplication.app.adsShowed) {
                checkAdsLoad()
            }
        })
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
            checkAdsLoad()
        }, onAdLoader = {
            status++
            CustomApplication.app.interstitialPreloadAdManager?.loadAdsSuccess = true
            checkAdsLoad()
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
    }
}