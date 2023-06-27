package com.neko.hiepdph.skibyditoiletvideocall

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.facebook.appevents.AppEventsLogger
import com.gianghv.libads.AdaptiveBannerManager
import com.gianghv.libads.AppOpenAdManager
import com.gianghv.libads.InterstitialPreloadAdManager
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.gianghv.libads.NativeAdsManager
import com.gianghv.libads.RewardAdsManager
import com.gianghv.libads.utils.Constants
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.nativead.NativeAd
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogFragmentLoadingOpenAds
import com.neko.hiepdph.skibyditoiletvideocall.common.isInternetAvailable
import com.neko.hiepdph.skibyditoiletvideocall.view.main.MainActivity


class CustomApplication : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var currentActivity: Activity? = null
    private var appOpenAdsManager: AppOpenAdManager? = null
    var adsShowed = false
    var nativeADIntro: MutableLiveData<NativeAd>? = MutableLiveData(null)
    var nativeADHome: MutableLiveData<NativeAd>? = MutableLiveData(null)

    var mNativeAdManagerIntro: NativeAdsManager? = null
    var mNativeAdManagerHome: NativeAdsManager? = null

    var interstitialPreloadAdManager: InterstitialPreloadAdManager? = null

    var adaptiveBannerManager: AdaptiveBannerManager? = null
    private var dialog: DialogFragmentLoadingOpenAds? = null

    companion object {
        lateinit var app: CustomApplication
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AppSharePreference.getInstance(applicationContext)
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        MobileAds.initialize(this) { MobileAds.setAppMuted(true) }
        val requestConfiguration =
            RequestConfiguration.Builder().setTestDeviceIds(Constants.testDevices()).build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        initAdjust()
        initFBApp()
        initApplovinMediation()
        initOpenAds()
    }


    private fun initFBApp() {
//        AudienceNetworkInitializeHelper.initialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }

    private fun initApplovinMediation() {
        AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX
        AppLovinSdk.getInstance(this).initializeSdk {}
    }

    private fun initAdjust() {
        val config = AdjustConfig(
            this, getString(R.string.adjust_token_key), AdjustConfig.ENVIRONMENT_PRODUCTION
        )
        config.setLogLevel(LogLevel.WARN)
        Adjust.onCreate(config)
        registerActivityLifecycleCallbacks(
            this
        )
    }

    private fun initOpenAds() {
        appOpenAdsManager = AppOpenAdManager(
            this, BuildConfig.ads_open_id
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onMoveToForeground() {
        if (!InterstitialPreloadAdManager.isShowingAds && !InterstitialSingleReqAdManager.isShowingAds && !RewardAdsManager.isShowing && !AppOpenAdManager.isShowingAd && currentActivity !is AdActivity) {
            currentActivity?.let {
                if (currentActivity is MainActivity) {
                    if (isInternetAvailable(applicationContext)) {
                        dialog = DialogFragmentLoadingOpenAds()

                        dialog?.show(
                            (currentActivity as MainActivity).supportFragmentManager, dialog?.tag
                        )
                        appOpenAdsManager?.loadAd(onAdLoader = {
                            appOpenAdsManager?.showAdIfAvailable(it)
                            dialog?.dismiss()
                        }, onAdLoadFail = {
                            dialog?.dismiss()
                        })
                    }
                }

            }
        }
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
        Adjust.onResume()
    }

    override fun onActivityPaused(p0: Activity) {
        dialog?.dismiss()
        Adjust.onPause()
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        newConfig?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
    }
}