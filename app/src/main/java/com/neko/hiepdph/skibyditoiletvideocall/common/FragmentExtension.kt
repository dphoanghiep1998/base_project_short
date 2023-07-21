package com.neko.hiepdph.skibyditoiletvideocall.common

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gianghv.libads.AdaptiveBannerManager
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.gianghv.libads.NativeAdGiftSoundView
import com.gianghv.libads.NativeAdSmallView
import com.gianghv.libads.NativeAdsManager
import com.gianghv.libads.RewardAdsManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.neko.hiepdph.skibyditoiletvideocall.BuildConfig
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication


fun Fragment.navigateToPage(fragmentId: Int, actionId: Int, bundle: Bundle? = null) {
    if (fragmentId == findNavController().currentDestination?.id) {
        findNavController().navigate(actionId, bundle)
    }
}

fun Fragment.navigateBack(id: Int) {
    if (findNavController().currentDestination?.id == id) {
        findNavController().popBackStack()
    }

}


fun Fragment.showBannerAds(view: ViewGroup, action: (() -> Unit)? = null) {
    CustomApplication.app.adaptiveBannerManager = AdaptiveBannerManager(
        requireActivity(),
        BuildConfig.banner_home_id,
        BuildConfig.banner_home_id2,
    )

    CustomApplication.app.adaptiveBannerManager?.loadBanner(view, onAdLoadFail = {
        view.visibility = View.GONE
        action?.invoke()
    }, onAdLoader = {
        view.visibility = View.VISIBLE

        action?.invoke()
    })
}

fun Activity.showBannerAds(view: ViewGroup, action: (() -> Unit)? = null) {
    val adaptiveBannerManager = AdaptiveBannerManager(
        this,
        BuildConfig.banner_home_id,
        BuildConfig.banner_home_id2,
    )

    adaptiveBannerManager.loadBanner(view, onAdLoadFail = {
        view.visibility = View.GONE
        action?.invoke()
    }, onAdLoader = {
        view.visibility = View.VISIBLE

        action?.invoke()
    })
}

fun Fragment.showNativeAds(
    view: NativeAdGiftSoundView?,
    view_small: NativeAdSmallView?,
    action: (() -> Unit)? = null,
    action_fail: (() -> Unit)? = null,
    type: NativeTypeEnum
) {
    val mNativeAdManager: NativeAdsManager?
    when (type) {
        NativeTypeEnum.INTRO -> {
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_intro_id,
                BuildConfig.native_intro_id2,
            )
        }
        else ->{
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_intro_id,
                BuildConfig.native_intro_id2,
            )
        }


    }
    view?.let {
        it.showShimmer(true)
        mNativeAdManager.loadAds(onLoadSuccess = { nativeAd ->
            it.visibility = View.VISIBLE
            action?.invoke()
            it.showShimmer(false)
            it.setNativeAd(nativeAd)
            it.isVisible = true
        }, onLoadFail = { _ ->
            action_fail?.invoke()
            it.errorShimmer()
            it.visibility = View.GONE
        })
    }

    view_small?.let {
        it.showShimmer(true)
        mNativeAdManager.loadAds(onLoadSuccess = { nativeAd ->
            it.visibility = View.VISIBLE
            action?.invoke()
            it.showShimmer(false)
            it.setNativeAd(nativeAd)
            it.isVisible = true
        }, onLoadFail = { _ ->
            it.errorShimmer()
            it.visibility = View.GONE
        })
    }

}

fun Context.pushEvent(key: String) {
    FirebaseAnalytics.getInstance(this).logEvent(key, null)
}

fun Fragment.showRewardAds(
    actionSuccess: () -> Unit,
    actionDoneWhenAdsNotComplete: () -> Unit,
    actionFailed: () -> Unit,
    type: RewardAdsEnum
) {

    if (!isInternetAvailable(requireContext())) {
        actionFailed()
        return
    }

    if (activity == null) {
        return
    }

    if (RewardAdsManager.isShowing) {
        return
    }

    val rewardAdsManager: RewardAdsManager
    when (type) {
        RewardAdsEnum.VIDEO -> {
            rewardAdsManager = RewardAdsManager(
                requireActivity(),
                BuildConfig.reward_app_id,
                BuildConfig.reward_app_id2,
            )
        }
    }

    RewardAdsManager.isShowing = true

    val dialogLoadingInterAds = DialogFragmentLoadingInterAds().onCreateDialog(requireContext())
    lifecycleScope.launchWhenResumed {
        dialogLoadingInterAds.show()
        rewardAdsManager.showAds(requireActivity(), onActionDoneWhenAdsNotComplete = {
            actionDoneWhenAdsNotComplete.invoke()
            RewardAdsManager.isShowing = false
            rewardAdsManager.adImpression = false
        }, onLoadAdSuccess = {
            RewardAdsManager.isShowing = true
            dialogLoadingInterAds.dismiss()
        }, onAdClose = {
            actionSuccess()
            RewardAdsManager.isShowing = false
            rewardAdsManager.adImpression = false
        }, onAdLoadFail = {
            RewardAdsManager.isShowing = false
            rewardAdsManager.adImpression = false
            actionFailed()
            dialogLoadingInterAds.dismiss()
        })
    }

}


fun Fragment.showInterAds(
    action: () -> Unit, type: InterAdsEnum
) {
    if (!isAdded) {
        action.invoke()
        return
    }
    if (!isInternetAvailable(requireContext())) {
        action.invoke()
        return
    }

    if (activity == null) {
        action.invoke()
        return
    }

    if (InterstitialSingleReqAdManager.isShowingAds) {
        return
    }
    val interstitialSingleReqAdManager: InterstitialSingleReqAdManager
    when (type) {
        InterAdsEnum.SPLASH -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_splash_id,
                BuildConfig.inter_splash_id2,
            )
        }
        else ->{
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_splash_id,
                BuildConfig.inter_splash_id2,
            )
        }
    }

    InterstitialSingleReqAdManager.isShowingAds = true

    val dialogLoadingInterAds = DialogFragmentLoadingInterAds().onCreateDialog(requireContext())
    lifecycleScope.launchWhenResumed {
        dialogLoadingInterAds.show()
        interstitialSingleReqAdManager.showAds(requireActivity(), onLoadAdSuccess = {
            dialogLoadingInterAds.dismiss()
        }, onAdClose = {
            InterstitialSingleReqAdManager.isShowingAds = false
            lifecycleScope.launchWhenResumed { action() }

        }, onAdLoadFail = {
            InterstitialSingleReqAdManager.isShowingAds = false
            lifecycleScope.launchWhenResumed { action() }
            dialogLoadingInterAds.dismiss()
        })
    }

}
fun isInternetAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    if (netInfo != null) {
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    return false

}
