package com.muka.pangolin

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.bytedance.sdk.openadsdk.*
import io.flutter.plugin.common.*
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class BannerFactory(private val activity: Activity, private val binaryMessenger: BinaryMessenger) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private var mTTAdNative: TTAdNative? = null
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        mTTAdNative = TTAdManagerHolder.get()?.createAdNative(activity)
        TTAdManagerHolder.get()?.requestPermissionIfNecessary(context)
        return BannerFactoryView(context!!, binaryMessenger, viewId, activity, mTTAdNative, args as Map<String, Any>)
    }
}

class BannerFactoryView(private val context: Context, private val binaryMessenger: BinaryMessenger, private val id: Int, private val activity: Activity, private val mTTAdNative: TTAdNative?, private val args: Map<String, Any>) : PlatformView, EventChannel.StreamHandler {
    private var frameView: FrameLayout = FrameLayout(context)

    private var eventSink: EventChannel.EventSink? = null

    private var mTTAd: TTNativeExpressAd? = null

    private var eventChannel: EventChannel = EventChannel(binaryMessenger, "${PangolinPlugin.TAG_SPLASHAD_FRAGMENT}_$id")

    init {
        eventChannel.setStreamHandler(this)
        val adSlot = AdSlot.Builder()
                .setCodeId(args["mCodeId"] as String)
                .setSupportDeepLink(true)
                .setAdCount(args["count"] as Int)
                .setExpressViewAcceptedSize((args["width"] as Double).toFloat(), (args["height"] as Double).toFloat()) //期望模板广告view的size,单位dp
                .build()
        mTTAdNative!!.loadBannerExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onError(code: Int, message: String) {
                TToast.show(context, message)
                frameView.removeAllViews()
            }

            override fun onNativeExpressAdLoad(ads: MutableList<TTNativeExpressAd>?) {
                if (ads == null || ads.isEmpty()) {
                    return
                }
                mTTAd = ads[0]
                mTTAd!!.setSlideIntervalTime((args["interval"] as Int) * 1000)
                bindAdListener(mTTAd!!)
                mTTAd!!.render()
            }
        })
    }

    override fun getView(): FrameLayout {
        return frameView
    }


    override fun dispose() {
    }

    private fun bindAdListener(ad: TTNativeExpressAd) {
        ad.setExpressInteractionListener(object : TTNativeExpressAd.AdInteractionListener {
            override fun onAdClicked(view: View?, type: Int) {
//                Log.d(TAG, "onAdClicked")
                eventSink?.success("onAdClicked")
            }

            override fun onAdShow(view: View?, type: Int) {
//                Log.d(TAG, "onAdShow")
                eventSink?.success("onAdShow")
            }

            override fun onRenderFail(p0: View?, p1: String?, p2: Int) {
                eventSink?.success("onRenderFail")
            }

            override fun onRenderSuccess(view: View?, width: Float, height: Float) {
                frameView.removeAllViews()
                frameView.addView(view!!)
                eventSink?.success("onRenderSuccess")
            }

            override fun onAdDismiss() {
                eventSink?.success("onAdDismiss")
            }
        })
        //dislike设置
//        bindDislike(ad, false)
//        if (ad.interactionType != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
//            return
//        }
//        ad.setDownloadListener(object : TTAppDownloadListener {
//            override fun onIdle() {
//                TToast.show(this@NativeExpressActivity, "点击开始下载", Toast.LENGTH_LONG)
//            }
//
//            override fun onDownloadActive(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
//                if (!mHasShowDownloadActive) {
//                    mHasShowDownloadActive = true
//                    TToast.show(this@NativeExpressActivity, "下载中，点击暂停", Toast.LENGTH_LONG)
//                }
//            }
//
//            override fun onDownloadPaused(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
//                TToast.show(this@NativeExpressActivity, "下载暂停，点击继续", Toast.LENGTH_LONG)
//            }
//
//            override fun onDownloadFailed(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
//                TToast.show(this@NativeExpressActivity, "下载失败，点击重新下载", Toast.LENGTH_LONG)
//            }
//
//            override fun onInstalled(fileName: String, appName: String) {
//                TToast.show(this@NativeExpressActivity, "安装完成，点击图片打开", Toast.LENGTH_LONG)
//            }
//
//            override fun onDownloadFinished(totalBytes: Long, fileName: String, appName: String) {
//                TToast.show(this@NativeExpressActivity, "点击安装", Toast.LENGTH_LONG)
//            }
//        })
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
    }

    override fun onCancel(arguments: Any?) {
//        eventSink?.success("onAdTimeOver")
    }
}