package com.muka.pangolin

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** PangolinPlugin */
class PangolinPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    private lateinit var activity: Activity

    private lateinit var applicationContext: Context


    private lateinit var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding

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

//    private fun initTTSDKConfig() {
//        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
//        mTTAdNative = TTAdManagerHolder.get()?.createAdNative(activity)
//        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get()?.requestPermissionIfNecessary(activity)
//    }

}
