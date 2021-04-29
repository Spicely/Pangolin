package com.muka.pangolin

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTSplashAd
import io.flutter.plugin.common.*
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class SplashFactory(private val activity: Activity, private val binaryMessenger: BinaryMessenger) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private var mTTAdNative: TTAdNative? = null
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        mTTAdNative = TTAdManagerHolder.get()?.createAdNative(activity)
        TTAdManagerHolder.get()?.requestPermissionIfNecessary(context)
        return SplashFactoryView(context!!, binaryMessenger, viewId, activity, mTTAdNative, args as Map<String, Any>)
    }
}

class SplashFactoryView(private val context: Context, private val binaryMessenger: BinaryMessenger, private val id: Int, private val activity: Activity, private val mTTAdNative: TTAdNative?, private val args: Map<String, Any>) : PlatformView, EventChannel.StreamHandler {
    private var mSplashContainer: FrameLayout = FrameLayout(context)

    private var eventSink: EventChannel.EventSink? = null

    private var eventChannel: EventChannel = EventChannel(binaryMessenger, "${PangolinPlugin.TAG_SPLASHAD_FRAGMENT}_$id")

    init {
        eventChannel.setStreamHandler(this)
        val adSlot = AdSlot.Builder()
                .setCodeId(args["mCodeId"] as String?)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build()
        mTTAdNative!!.loadSplashAd(adSlot, object : TTAdNative.SplashAdListener {
            override fun onError(code: Int, message: String) {
                TToast.show(context, message)
                mSplashContainer.removeAllViews()
            }

            override fun onTimeout() {
                eventSink?.success("onTimeout")
            }

            override fun onSplashAdLoad(ad: TTSplashAd) {
                if (ad == null) {
                    return
                }
                val view: View = ad.splashView
                bindAdListener(ad)
                mSplashContainer.removeAllViews()
                mSplashContainer.addView(view)
            }
        })
    }

    override fun getView(): FrameLayout {
        return mSplashContainer
    }


    override fun dispose() {
    }

    private fun bindAdListener(ad: TTSplashAd) {
        ad.setSplashInteractionListener(object : TTSplashAd.AdInteractionListener {
            override fun onAdClicked(view: View?, type: Int) {
//              Log.d(TAG, "onAdClicked")
                eventSink?.success("onAdClicked")
            }

            override fun onAdShow(view: View?, type: Int) {
//              Log.d(TAG, "onAdShow")
                eventSink?.success("onAdShow")
            }

            override fun onAdSkip() {
//              showToast("开屏广告跳过")
                eventSink?.success("onAdSkip")
            }

            override fun onAdTimeOver() {
//              showToast("开屏广告倒计时结束")
                eventSink?.success("onAdTimeOver")
            }
        })
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
    }

    override fun onCancel(arguments: Any?) {
//        eventSink?.success("onAdTimeOver")
    }
}