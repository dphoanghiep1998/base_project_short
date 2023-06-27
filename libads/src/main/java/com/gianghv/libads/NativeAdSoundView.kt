package com.gianghv.libads

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import com.gianghv.libads.MLBBaseNativeAdView
import com.gianghv.libads.databinding.MlbViewGoogleBigNativeGiftBinding
import com.gianghv.libads.databinding.MlbViewGoogleBigNativeSoundBinding
import com.gianghv.libads.databinding.MlbViewGoogleBignativeBinding

class NativeAdSoundView(context: Context, attrs: AttributeSet?) : MLBBaseNativeAdView(context, attrs) {

    var mViewBinding: MlbViewGoogleBigNativeSoundBinding =
        MlbViewGoogleBigNativeSoundBinding.inflate(LayoutInflater.from(context) , this , true)

    override fun getTitleView(): AppCompatTextView = mViewBinding.adHeadline
    override fun getSubTitleView(): AppCompatTextView = mViewBinding.adBody
    override fun getRatingView(): AppCompatRatingBar? = null

    override fun getIconView(): AppCompatImageView = mViewBinding.adAppIcon

    override fun getPriceView(): AppCompatTextView? = null
    override fun getCallActionButtonView(): AppCompatTextView = mViewBinding.adCallToAction
    override fun getMediaView(): MediaView = mViewBinding.adMedia

    override fun getAdView(): NativeAdView = mViewBinding.adView
    override fun getViewContainerRate_Price(): View? = null

    override fun getShimerView(): ShimmerFrameLayout = mViewBinding.shimmer.shimmerViewGift
    override fun getRootAds(): LinearLayout = mViewBinding.rootNativeAd
}