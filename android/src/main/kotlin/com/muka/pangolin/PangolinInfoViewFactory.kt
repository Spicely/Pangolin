package com.muka.pangolin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.bytedance.sdk.openadsdk.*
import com.bytedance.sdk.openadsdk.TTAdNative.NativeExpressAdListener
import com.bytedance.sdk.openadsdk.TTNativeExpressAd.ExpressAdInteractionListener
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory


class PangolinInfoViewFactory(private val activity: Activity) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private var mTTAdNative: TTAdNative? = null

    override fun create(context: Context, viewId: Int, args: Any): PlatformView {
        mTTAdNative = TTAdManagerHolder.get()?.createAdNative(activity)
        TTAdManagerHolder.get()?.requestPermissionIfNecessary(context)
        return InfoView(context, viewId, activity, mTTAdNative, args as Map<String, Any>)
    }
}

class InfoView(private val context: Context, private val id: Int,private val activity: Activity,private val mTTAdNative: TTAdNative?, private val args: Map<String, Any>) : PlatformView {
    private var frameView: FrameLayout = FrameLayout(context)

    private var mTTAd: TTNativeExpressAd? = null

    private var startTime: Long = 0

    init {
        val adSlot = AdSlot.Builder()
                .setCodeId(args["mCodeId"] as String?)
                .setAdCount(1)
                .setExpressViewAcceptedSize((args["width"] as Double).toFloat(), (args["height"] as Double).toFloat())
                .build()
        mTTAdNative!!.loadNativeExpressAd(adSlot, object : NativeExpressAdListener {
            override fun onError(code: Int, message: String) {
                TToast.show(context, message)
                frameView.removeAllViews()
            }

            override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
                if (ads == null || ads.isEmpty()) {
                    return
                }
                mTTAd = ads[0]
                bindAdListener(mTTAd!!)
                startTime = System.currentTimeMillis()
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
        ad.setExpressInteractionListener(object : ExpressAdInteractionListener {
            override fun onAdClicked(view: View?, type: Int) {
//                TToast.show(mContext, "广告被点击")
            }

            override fun onAdShow(view: View?, type: Int) {
//                TToast.show(mContext, "广告展示")
            }

            override fun onRenderFail(view: View?, msg: String, code: Int) {
//                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime))
//                TToast.show(context, "$msg code:$code")
            }

            override fun onRenderSuccess(view: View?, width: Float, height: Float) {
//                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime))
                //返回view的宽高 单位 dp
                TToast.show(context, "渲染成功")
                frameView.removeAllViews()
                frameView.addView(view!!)
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
}
