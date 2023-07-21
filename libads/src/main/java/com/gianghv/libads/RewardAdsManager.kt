package com.gianghv.libads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gianghv.libads.utils.AdsConfigUtils
import com.gianghv.libads.utils.Constants
import com.gianghv.libads.utils.Utils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAdsManager(
    private val context: Context,
    private val mIDReward01: String,
    private val mIDReward02: String,
) {
    private var rewardedAd: RewardedAd? = null
    var adImpression = false
    var handler: Handler? = null
    var runable: Runnable? = null

    companion object {
        var isShowing = false
    }


    fun showAds(
        activity: Activity,
        onLoadAdSuccess: (() -> Unit)? = null,
        onAdClose: (() -> Unit)? = null,
        onActionDoneWhenAdsNotComplete: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        val requestConfiguration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        if (!Utils.isOnline(context)) {
            onAdLoadFail?.invoke()
            return
        }

        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onAdLoadFail?.invoke()
            }
        }
        handler?.postDelayed(runable!!, Constants.TIME_OUT)

        loadAds(onAdLoader = {
            onLoadAdSuccess?.invoke()
            showRewardAds(activity, object : OnShowRewardAdListener {
                override fun onShowRewardSuccess() {
                }

                override fun onShowAdsFailed() {
                    onActionDoneWhenAdsNotComplete?.invoke()
                }

                override fun onShowRewardCompleteDone() {
                    onAdClose?.invoke()
                }
            })
        }, onAdLoadFail)
    }

    fun loadAds(
        onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        if (AdsConfigUtils(context).getDefConfigNumber() == 1) {
            requestAdsPrepare(mIDReward01, onAdLoader, onAdLoadFail = {
                requestAdsPrepare(mIDReward02, onAdLoader, onAdLoadFail = {
                    if (handler == null) {
                        onAdLoadFail?.invoke()
                    }
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                })
            })
        } else {
            requestAdsPrepare(mIDReward02, onAdLoader, onAdLoadFail = {
                requestAdsPrepare(mIDReward01, onAdLoader, onAdLoadFail = {
                    if (handler == null) {
                        onAdLoadFail?.invoke()
                    }
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                })
            })
        }
    }

    private fun requestAdsPrepare(
        idAds: String,
        onLoadAdSuccess: (() -> Unit)? = null,
        adLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        RewardedAd.load(context,
            idAds,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    rewardedAd = p0
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                    onLoadAdSuccess?.invoke()
                    adLoader?.invoke()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    onAdLoadFail?.invoke()
                }
            })
    }

    fun showRewardAds(activity: Activity, onShowRewardAdListener: OnShowRewardAdListener) {
        if (rewardedAd != null) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        runable?.let { handler?.removeCallbacks(it) }
                        handler = null
                        Log.d("TAG", "onAdShowedFullScreenContent: ")
                        // Code to be invoked when the ad showed full screen content.
                    }

                    override fun onAdDismissedFullScreenContent() {
                        rewardedAd = null
                        if(!adImpression){
                            onShowRewardAdListener.onShowAdsFailed()
                        }else{
                            onShowRewardAdListener.onShowRewardCompleteDone()
                        }
                        Log.d("TAG", "onAdDismissedFullScreenContent: ")

                        // Code to be invoked when the ad dismissed full screen content.
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        Log.d("TAG", "onAdFailedToShowFullScreenContent: ")
                        rewardedAd = null
                        isShowing = false
                        if(!adImpression){
                            onShowRewardAdListener.onShowAdsFailed()
                        }else{
                            onShowRewardAdListener.onShowRewardCompleteDone()
                        }
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        Log.d("TAG", "onAdImpression: ")
                    }
                }
            rewardedAd?.fullScreenContentCallback = fullScreenContentCallback
            rewardedAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, rewardedAd?.adUnitId)
            }
            rewardedAd?.show(
                activity
            ) {
                adImpression = true
            }
        } else {
//            loadAds()
//            onShowRewardAdListener.onShowRewardSuccess()
        }
    }

    interface OnShowRewardAdListener {
        fun onShowRewardSuccess()
        fun onShowAdsFailed()
        fun onShowRewardCompleteDone()
    }
}