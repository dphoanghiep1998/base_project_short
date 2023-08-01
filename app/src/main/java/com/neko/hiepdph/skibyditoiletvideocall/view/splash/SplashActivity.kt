package com.neko.hiepdph.skibyditoiletvideocall.view.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.gianghv.libads.AppOpenStartAdManager
import com.gianghv.libads.NativeAdsManager
import com.gianghv.libads.utils.AdsConfigUtils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.neko.hiepdph.skibyditoiletvideocall.BuildConfig
import com.neko.hiepdph.skibyditoiletvideocall.CustomApplication
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.AppSharePreference
import com.neko.hiepdph.skibyditoiletvideocall.common.DialogFragmentLoadingOpenAds
import com.neko.hiepdph.skibyditoiletvideocall.common.createContext
import com.neko.hiepdph.skibyditoiletvideocall.common.isInternetAvailable
import com.neko.hiepdph.skibyditoiletvideocall.databinding.ActivitySplashBinding
import com.neko.hiepdph.skibyditoiletvideocall.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var openSplashAds: AppOpenStartAdManager? = null
    private val initDone = AppSharePreference.INSTANCE.getInitDone(false)
    private var dialogLoadingOpenAds: Dialog? = null
    private var status = 0
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogLoadingOpenAds = DialogFragmentLoadingOpenAds().onCreateDialog(this)
        initAds()
        fetchRemoteConfig()
        setStatusColor()
        handleAds()
        handler = Handler()
        runnable = Runnable {
            val defPos = FirebaseRemoteConfig.getInstance().getLong(AdsConfigUtils.DEF_POS)
            AdsConfigUtils(this@SplashActivity).putDefConfigNumber(defPos.toInt())
            status++
            checkAdsLoad()
            handler = null
        }
        handler?.postDelayed(runnable!!, 10000)
    }


    private val callback = object : AppOpenStartAdManager.OnShowAdCompleteListener {

        override fun onShowAdComplete() {
            dialogLoadingOpenAds?.dismiss()
            navigateToMain()
        }

    }

    private fun fetchRemoteConfig() {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            val defPos = FirebaseRemoteConfig.getInstance().getLong(AdsConfigUtils.DEF_POS)
            AdsConfigUtils(this).putDefConfigNumber(defPos.toInt())
            checkFinishPermission()
        }.addOnFailureListener {
            AdsConfigUtils(this).putDefConfigNumber(AdsConfigUtils.DEF_POS_VALUE)
            checkFinishPermission()
        }
    }

    private fun checkFinishPermission() {
        handler?.removeCallbacks(runnable!!)
        handler = null
        status++
        checkAdsLoad()
    }

    private fun checkAdsLoad() {
        Log.d("TAG", "checkAdsLoad: " +status)
        if (status == 2) {
            if (openSplashAds?.isAdLoaded == true) {
                handleAtLeast2Second(action = {
                    lifecycleScope.launchWhenResumed {
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(500)
                            openSplashAds?.showAdIfAvailable(
                                this@SplashActivity, callback
                            )
                        }
                    }

                })
            } else {
                navigateToMain()
            }
        }
    }


    private fun handleAds() {
        if (!isInternetAvailable(this)) {
            binding.loadingSplash.repeatCount = 0
            handleWhenAnimationDone(action = {
                navigateToMain()
            })
        } else {
            binding.loadingSplash.repeatCount = LottieDrawable.INFINITE

            Log.d("TAG", "handleAds:2 ")
            handleAtLeast2Second(action = {
                loadSplashAds()
            })
        }
    }

    private fun loadSplashAds() {
        Log.d("TAG", "handleAds:3 ")

        openSplashAds?.loadAd(onAdLoadFail = {
            Log.d("TAG", "loadSplashAds: fail")
            if (!CustomApplication.app.isPassLang) {
                openSplashAds?.isAdLoaded = false
                status++
                checkAdsLoad()
            }
        }, onAdLoader = {
            Log.d("TAG", "loadSplashAds: true")
            if (!CustomApplication.app.isPassLang) {
                openSplashAds?.isAdLoaded = true
                status++
                checkAdsLoad()
            }

        })
    }


    private fun initAds() {
        openSplashAds = AppOpenStartAdManager(
            this,
            BuildConfig.open_splash_id1,
            BuildConfig.open_splash_id2,
        )
        if (!initDone) {
            CustomApplication.app.mNativeAdManagerLanguage = NativeAdsManager(
                this,
                BuildConfig.native_language_id1,
                BuildConfig.native_language_id2,
            )
        }
    }

    private fun handleAtLeast2Second(action: () -> Unit) {
        Handler().postDelayed({
            action.invoke()
        }, 2000)
    }

    private fun setStatusColor() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
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
        super.applyOverrideConfiguration(overrideConfiguration)
        overrideConfiguration?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
    }


    private fun handleWhenAnimationDone(action: () -> Unit) {
        binding.loadingSplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                action.invoke()

            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
        dialogLoadingOpenAds?.dismiss()
        openSplashAds = null
        runnable?.let { handler?.removeCallbacks (it) }
    }


    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}