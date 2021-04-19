package com.muka.pangolin

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.MainThread
import com.bytedance.sdk.openadsdk.*
import com.bytedance.sdk.openadsdk.TTAdNative.SplashAdListener
import io.flutter.Log


class SplashActivity : Activity() {

    private val TAG = "SplashActivity"
    private var mTTAdNative: TTAdNative? = null
    private var mSplashContainer: FrameLayout? = null

    //是否强制跳转到主页面
    private var mForceGoMain = false

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private val AD_TIME_OUT = 3000
    private var mCodeId = ""
    private var mIsExpress = false //是否请求模板广告


    //是否开启调试模式查看日志
    private var debug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mSplashContainer = findViewById<View>(R.id.splash_container) as FrameLayout
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get()!!.createAdNative(this)
        val intent = intent
        mCodeId = intent.getStringExtra("mCodeId").toString()
        debug = intent.getBooleanExtra("debug", true)
        getExtraInfo()
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
        // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
        //加载开屏广告
        loadSplashAd()
    }

    private fun getExtraInfo() {
        val intent = intent ?: return
        val codeId: String? = intent.getStringExtra("splash_rit")
        if (!TextUtils.isEmpty(codeId)) {
            if (codeId != null) {
                mCodeId = codeId
            }
        }
        mIsExpress = intent.getBooleanExtra("is_express", false)
    }

    override fun onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain) {
            goToMainActivity()
        }
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        mForceGoMain = true
    }

    /**
     * 加载开屏广告
     */
    fun loadSplashAd() {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        var adSlot: AdSlot? = null
        adSlot = if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
            val expressViewWidth: Float = UIUtils.getScreenWidthDp(this)
            val expressViewHeight: Float = UIUtils.getHeight(this)
            AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920) //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                    .build()
        } else {
            AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    .build()
        }

        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative!!.loadSplashAd(adSlot, object : SplashAdListener {
            @MainThread
            override fun onError(code: Int, message: String) {
                Log.d(TAG, message)
                showToast(message)
                goToMainActivity()
            }

            @MainThread
            override fun onTimeout() {
                showToast("开屏广告加载超时")
                goToMainActivity()
            }

            @MainThread
            override fun onSplashAdLoad(ad: TTSplashAd) {
                Log.d(TAG, "开屏广告请求成功")
                if (ad == null) {
                    return
                }
                //获取SplashView
                val view: View = ad.splashView
                if (view != null && mSplashContainer != null && !this@SplashActivity.isFinishing) {
                    mSplashContainer!!.removeAllViews()
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer!!.addView(view)
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    goToMainActivity()
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(object : TTSplashAd.AdInteractionListener {
                    override fun onAdClicked(view: View?, type: Int) {
                        Log.d(TAG, "onAdClicked")
                        showToast("开屏广告点击")
                    }

                    override fun onAdShow(view: View?, type: Int) {
                        Log.d(TAG, "onAdShow")
                        showToast("开屏广告展示")
                    }

                    override fun onAdSkip() {
                        Log.d(TAG, "onAdSkip")
                        showToast("开屏广告跳过")
                        goToMainActivity()
                    }

                    override fun onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver")
                        showToast("开屏广告倒计时结束")
                        goToMainActivity()
                    }
                })
                if (ad.interactionType == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(object : TTAppDownloadListener {
                        var hasShow = false
                        override fun onIdle() {}
                        override fun onDownloadActive(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
                            if (!hasShow) {
                                showToast("下载中...")
                                hasShow = true
                            }
                        }

                        override fun onDownloadPaused(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
                            showToast("下载暂停...")
                        }

                        override fun onDownloadFailed(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
                            showToast("下载失败...")
                        }

                        override fun onDownloadFinished(totalBytes: Long, fileName: String, appName: String) {
                            showToast("下载完成...")
                        }

                        override fun onInstalled(fileName: String, appName: String) {
                            showToast("安装完成...")
                        }
                    })
                }
            }
        }, AD_TIME_OUT)
    }

    /**
     * 跳转到主页面
     */
    private fun goToMainActivity() {
        finish()
    }

    private fun showToast(msg: String) {
        if (debug) {
            TToast.show(this, msg)
        }
    }
}