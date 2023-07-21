package com.gianghv.libads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gianghv.libads.utils.AdsConfigUtils
import com.gianghv.libads.utils.Constants
import com.gianghv.libads.utils.Utils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialPreloadAdManager constructor(
    private val context: Context,
    private val mIdAdsFull01: String,
    private val mIdAdsFull02: String
) {
    companion object {
        var isShowingAds = false
    }

    private var mInterstitialAd: InterstitialAd? = null
    var handler: Handler? = null
    var runable: Runnable? = null
    var loadAdsSuccess = false



    fun loadAds(
        onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        if (!Utils.isOnline(context)) {
            onAdLoadFail?.invoke()
            return
        }
        val requestConfiguration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        handler = Handler(Looper.getMainLooper())
        runable = Runnable {
            if (handler != null) {
                onAdLoadFail?.invoke()
            }
        }
        handler?.postDelayed(runable!!, Constants.TIME_OUT)

        if (AdsConfigUtils(context).getDefConfigNumber() == 1) {
            requestAdsPrepare(mIdAdsFull01, onAdLoader, onAdLoadFail = {
                requestAdsPrepare(mIdAdsFull02, onAdLoader, onAdLoadFail = {
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                    onAdLoadFail?.invoke()
                })
            })
        } else {
            requestAdsPrepare(mIdAdsFull02, onAdLoader, onAdLoadFail = {
                requestAdsPrepare(mIdAdsFull01, onAdLoader, onAdLoadFail = {
                    runable?.let { handler?.removeCallbacks(it) }
                    handler = null
                    onAdLoadFail?.invoke()
                })
            })
        }
    }

    private fun requestAdsPrepare(
        idAds: String, onAdLoader: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        if (handler == null) {
            return
        }
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, idAds, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                onAdLoadFail?.invoke()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                if (handler == null) {
                    return
                }
                runable?.let { handler?.removeCallbacks(it) }
                handler = null
                mInterstitialAd = interstitialAd
                onAdLoader?.invoke()
            }
        })
    }

    fun showAds(activity: Activity, callBack: InterstitialAdListener?) {
        isShowingAds = true
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    callBack?.onClose()
                    isShowingAds = false

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    callBack?.onError()
                    isShowingAds = false

                }

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                    isShowingAds = false
                }

            }
            mInterstitialAd?.setOnPaidEventListener {
                Utils.postRevenueAdjust(it, mInterstitialAd?.adUnitId)
            }
            mInterstitialAd?.show(activity) ?: callBack?.onError()
        } else {
            callBack?.onError()
        }
    }

    interface InterstitialAdListener {
        fun onClose()
        fun onError()
    }


}