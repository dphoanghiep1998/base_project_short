package com.gianghv.libads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.gianghv.libads.utils.AdsConfigUtils
import com.gianghv.libads.utils.Constants
import com.gianghv.libads.utils.DialogLoadingAds
import com.gianghv.libads.utils.Utils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialPlashAdManager constructor(
    private val context: Context,
    private val mIdAdsFull01: String,
    private val mIdAdsFull02: String
) {
    var mInterstitialAd: InterstitialAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null
    fun loadWaitShowAds(
        onLoadAdSuccess: (() -> Unit)? = null,
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
        }, onAdLoadFail)
    }


    fun loadAds(
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        if (AdsConfigUtils(context).getDefConfigNumber() == 1) {
            requestAdsPrepare(mIdAdsFull01, onAdLoader, onAdLoadFail = {
                requestAdsPrepare(mIdAdsFull02, onAdLoader, onAdLoadFail = {
                    if (handler == null) {
                        onAdLoadFail?.invoke()
                    }
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                })
            })
        } else {
            requestAdsPrepare(mIdAdsFull02, onAdLoader, onAdLoadFail = {
                requestAdsPrepare(mIdAdsFull01, onAdLoader, onAdLoadFail = {
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
        onAdLoader: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, idAds, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                onAdLoadFail?.invoke()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                runable?.let { handler?.removeCallbacks(it) }
                handler = null
                mInterstitialAd = interstitialAd
                onLoadAdSuccess?.invoke()
                onAdLoader?.invoke()
            }
        })
    }

    fun show(activity: Activity, listener: OnShowInterstitialCallBack?) {
        if (mInterstitialAd != null) {
            runable?.let { handler?.removeCallbacks(it) }
            handler = null
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    listener?.onAdsClose()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                }

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                }
            }
            mInterstitialAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, mInterstitialAd?.adUnitId)
            }
            mInterstitialAd?.show(activity)
        } else {
            listener?.onAdsClose()
        }
    }

    interface OnShowInterstitialCallBack {
        fun onAdsClose()
    }
}