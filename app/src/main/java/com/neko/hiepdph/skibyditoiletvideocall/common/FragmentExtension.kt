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
        BuildConfig.banner_home_id3,
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
        BuildConfig.banner_home_id3,
    )
    if (AdaptiveBannerManager.isBannerLoaded) {
        adaptiveBannerManager.loadAdViewToParent(view)
        return
    }

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
                BuildConfig.native_intro_id3,
            )
        }

        NativeTypeEnum.VIDEO -> {
            mNativeAdManager = NativeAdsManager(
                requireActivity(),
                BuildConfig.native_video_id,
                BuildConfig.native_video_id2,
                BuildConfig.native_video_id3,
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
    actionSuccess: () -> Unit, actionFailed: () -> Unit, type: RewardAdsEnum
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
                BuildConfig.reward_app_id3,
            )
        }
    }

    RewardAdsManager.isShowing = true

    val dialogLoadingInterAds = DialogFragmentLoadingInterAds()
    lifecycleScope.launchWhenResumed {
        dialogLoadingInterAds.show(childFragmentManager, dialogLoadingInterAds.tag)
        rewardAdsManager.showAds(requireActivity(), onLoadAdSuccess = {
            dialogLoadingInterAds.dismissAllowingStateLoss()
        }, onAdClose = {
            RewardAdsManager.isShowing = false
            actionSuccess()
        }, onAdLoadFail = {
            RewardAdsManager.isShowing = false
            actionFailed()
            dialogLoadingInterAds.dismissAllowingStateLoss()
        })
    }

}

fun Fragment.showInterAds(
    action: () -> Unit, type: InterAdsEnum
) {
    Log.d("TAG", "showInterAds: ")
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
                BuildConfig.inter_splash_id3,
            )
        }

        InterAdsEnum.VIDEO -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_video_id,
                BuildConfig.inter_video_id2,
                BuildConfig.inter_video_id3,
            )
        }
    }

    InterstitialSingleReqAdManager.isShowingAds = true

    val dialogLoadingInterAds = DialogFragmentLoadingInterAds()
    lifecycleScope.launchWhenResumed {
        dialogLoadingInterAds.show(childFragmentManager, dialogLoadingInterAds.tag)
        interstitialSingleReqAdManager.showAds(requireActivity(), onLoadAdSuccess = {
            dialogLoadingInterAds.dismissAllowingStateLoss()
        }, onAdClose = {
            InterstitialSingleReqAdManager.isShowingAds = false
            action()
        }, onAdLoadFail = {
            InterstitialSingleReqAdManager.isShowingAds = false
            action()
            dialogLoadingInterAds.dismissAllowingStateLoss()
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
