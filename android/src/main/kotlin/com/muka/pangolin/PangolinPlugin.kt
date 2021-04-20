package com.muka.pangolin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import com.bytedance.sdk.openadsdk.*
import com.bytedance.sdk.openadsdk.TTAdNative.NativeExpressAdListener
import com.bytedance.sdk.openadsdk.TTNativeExpressAd.ExpressAdInteractionListener
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlin.math.roundToInt


/** PangolinPlugin */
class PangolinPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    private lateinit var activity: Activity

    private lateinit var applicationContext: Context

    private var mExpressContainer: FrameLayout? = null

    private var mTTAd: TTNativeExpressAd? = null

    private var startTime: Long = 0

    private lateinit var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding

    private var mTTAdNative: TTAdNative? = null

    companion object {
        private const val TAG_FLUTTER_FRAGMENT = "com.tongyangsheng.pangolin/pangolin_info_view"
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding
        this.applicationContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.tongyangsheng.pangolin")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "register") {
            val appId: String? = call.argument("appId")
            var useTextureView: Boolean? = call.argument("useTextureView")
            val appName: String? = call.argument("appName")
            var allowShowNotify: Boolean? = call.argument("allowShowNotify")
            var allowShowPageWhenScreenLock: Boolean? = call.argument("allowShowPageWhenScreenLock")
            var debug: Boolean? = call.argument("debug")
            var supportMultiProcess: Boolean? = call.argument("supportMultiProcess")
            var directDownloadNetworkType: List<Int>? = call.argument("directDownloadNetworkType")
            if (useTextureView == null) {
                useTextureView = false
            }
            if (allowShowNotify == null) {
                allowShowNotify = true
            }
            if (allowShowPageWhenScreenLock == null) {
                allowShowPageWhenScreenLock = true
            }
            if (debug == null) {
                debug = true
            }
            if (supportMultiProcess == null) {
                supportMultiProcess = false
            }
            if (directDownloadNetworkType == null) {
                directDownloadNetworkType = listOf()
            }
            if (appId == null || appId.trim().isEmpty()) {
                result.error("500", "appId can't be null", null)
            } else {
                if (appName == null || appName.trim { it <= ' ' }.isEmpty()) {
                    result.error("600", "appName can't be null", null)
                } else {
                    TTAdManagerHolder(activity, appId, useTextureView, appName, allowShowNotify, allowShowPageWhenScreenLock, debug, supportMultiProcess, directDownloadNetworkType)
                    result.success(true)
                }
            }
        } else if (call.method == "loadSplashAd") {
            // 开屏广告
            val mCodeId: String = call.argument("mCodeId")!!
            val deBug: Boolean = call.argument("debug")!!
            val intent = Intent()
            intent.setClass(activity, SplashActivity::class.java)
            intent.putExtra("mCodeId", mCodeId)
            intent.putExtra("debug", deBug)
            activity.startActivity(intent)
        } else if (call.method == "loadRewardAd") {
            // 激励视屏
            val isHorizontal: Boolean = call.argument("isHorizontal")!!
            val mCodeId: String = call.argument("mCodeId")!!
            val debug: Boolean = call.argument("debug")!!
            val supportDeepLink: Boolean = call.argument("supportDeepLink")!!
            val rewardName: String = call.argument("rewardName")!!
            val rewardAmount: Int = call.argument("rewardAmount")!!
            val isExpress: Boolean = call.argument("isExpress")!!

            var expressViewAcceptedSizeH: Double? = call.argument("expressViewAcceptedSizeH")
            expressViewAcceptedSizeH = if (expressViewAcceptedSizeH == null) {
                500.0
            } else {
                call.argument("expressViewAcceptedSizeH")!!
            }
            var expressViewAcceptedSizeW: Double? = call.argument("expressViewAcceptedSizeW")
            expressViewAcceptedSizeW = if (expressViewAcceptedSizeW == null) {
                500.0
            } else {
                call.argument("expressViewAcceptedSizeW")!!
            }

            val userID: String = call.argument("userID")!!
            var mediaExtra: String? = call.argument("mediaExtra")
            mediaExtra = if (mediaExtra == null) {
                "media_extra"
            } else {
                call.argument("mediaExtra")!!
            }

            val rewardVideo = RewardVideo()
            RewardVideo._channel = channel
            rewardVideo.activity = activity
            rewardVideo.context = applicationContext
            if (isHorizontal!!) {
                rewardVideo.mHorizontalCodeId = mCodeId
            } else {
                rewardVideo.mVerticalCodeId = mCodeId
            }

            if (debug != null) {
                rewardVideo.debug = debug
            } else {
                rewardVideo.debug = false
            }

            if (isExpress != null) {
                rewardVideo.mIsExpress = isExpress
            } else {
                rewardVideo.mIsExpress = false
            }

            rewardVideo.supportDeepLink = supportDeepLink
            rewardVideo.expressViewAcceptedSizeH = expressViewAcceptedSizeH!!
            rewardVideo.expressViewAcceptedSizeW = expressViewAcceptedSizeW!!
            rewardVideo.rewardName = rewardName
            rewardVideo.rewardAmount = rewardAmount
            rewardVideo.userID = userID
            rewardVideo.mediaExtra = mediaExtra!!
            rewardVideo.init()
        } else if (call.method == "loadBannerAd") {
            // banner广告
            val mCodeId: String = call.argument("mCodeId")!!
            val supportDeepLink: Boolean? = call.argument("supportDeepLink")
            val isCarousel: Boolean? = call.argument("isCarousel")
            var interval = 0
            var expressViewWidth = 0f
            var expressViewHeight = 0f
            var topMargin = 0
            if (call.argument<Double?>("expressViewWidth") != null) {
                val expressViewWidthDouble: Double? = call.argument("expressViewWidth")
                expressViewWidth = expressViewWidthDouble!!.toFloat()
            }

            if (call.argument<Double?>("expressViewHeight") != null) {
                val expressViewHeightDouble: Double? = call.argument("expressViewHeight")
                expressViewHeight = expressViewHeightDouble!!.toFloat()
            }
            if (call.argument<Int?>("interval") != null && isCarousel!!) {
                interval = call.argument("interval")!!
            }
            if (call.argument<Int?>("topMargin") != null) {
                topMargin = call.argument("topMargin")!!
            }

            val rootView: ViewGroup = activity.findViewById<View>(android.R.id.content) as ViewGroup
            val view: View = View.inflate(activity, R.layout.activity_native_express_banner, null)
            mExpressContainer = view.findViewById(R.id.express_container) as FrameLayout
            if (mExpressContainer!!.parent != null) {
                (mExpressContainer!!.parent as ViewGroup).removeView(mExpressContainer)
            }

            // 设置banner 广告参数
            val params: RelativeLayout.LayoutParams = mExpressContainer!!.layoutParams as RelativeLayout.LayoutParams
            params.height = expressViewHeight.toInt() * 2
            params.width = expressViewWidth.toInt() * 2
            // 到顶部距离
            params.topMargin = topMargin
            mExpressContainer!!.layoutParams = params
            rootView.addView(mExpressContainer)
            initTTSDKConfig()
            this.loadExpressBannerAd(mCodeId, expressViewWidth.roundToInt(), expressViewHeight.roundToInt(), interval)
        } else if (call.method == "removeBannerAd") {
            // 删除banner广告
            val params = mExpressContainer!!.layoutParams as FrameLayout.LayoutParams
            params.height = 0
            params.width = 0
            mExpressContainer!!.layoutParams = params
            result.success(true);
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        flutterPluginBinding.platformViewRegistry.registerViewFactory(TAG_FLUTTER_FRAGMENT, PangolinInfoViewFactory(activity))
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
    }

    private fun initTTSDKConfig() {
        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get()?.createAdNative(activity)
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get()?.requestPermissionIfNecessary(activity)
    }

    // banner广告 加载
    private fun loadExpressBannerAd(codeId: String, expressViewWidth: Int, expressViewHeight: Int, interval: Int) {
        mExpressContainer!!.removeAllViews()
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        val adSlot = AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setAdCount(1)
                .setExpressViewAcceptedSize(expressViewWidth.toFloat(), expressViewHeight.toFloat()) //期望模板广告view的size,单位dp
                .build()
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative!!.loadBannerExpressAd(adSlot, object : NativeExpressAdListener {
            override fun onError(code: Int, message: String) {
                mExpressContainer!!.removeAllViews()
            }

            override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
                if (ads == null || ads.isEmpty()) {
                    return
                }
                mTTAd = ads[0]
                mTTAd!!.setSlideIntervalTime(interval * 1000)
                bindBannerAdListener(mTTAd!!)
                startTime = System.currentTimeMillis()
                mTTAd!!.render()
            }
        })
    }

    // banner广告 监听
    private fun bindBannerAdListener(ad: TTNativeExpressAd) {
        ad.setExpressInteractionListener(object : ExpressAdInteractionListener {
            override fun onAdClicked(view: View, type: Int) {
//        TToast.show(mContext, "广告被点击");
            }

            override fun onAdShow(view: View, type: Int) {
//        TToast.show(mContext, "广告展示");
            }

            override fun onRenderFail(view: View, msg: String, code: Int) {
                //        TToast.show(mContext, msg + " code:" + code);
            }

            override fun onRenderSuccess(view: View, width: Float, height: Float) {
                mExpressContainer!!.removeAllViews()
                mExpressContainer!!.addView(view)
            }
        })
        if (ad.interactionType != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return
        }
        ad.setDownloadListener(object : TTAppDownloadListener {
            override fun onIdle() {}
            override fun onDownloadActive(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
            }

            override fun onDownloadPaused(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
//        TToast.show(BannerExpressActivity.this, "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            override fun onDownloadFailed(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
//        TToast.show(BannerExpressActivity.this, "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            override fun onInstalled(fileName: String, appName: String) {
//        TToast.show(BannerExpressActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            override fun onDownloadFinished(totalBytes: Long, fileName: String, appName: String) {
//        TToast.show(BannerExpressActivity.this, "点击安装", Toast.LENGTH_LONG);
            }
        })
    }

}
