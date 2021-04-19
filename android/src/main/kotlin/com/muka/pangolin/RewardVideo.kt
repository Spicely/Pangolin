package com.muka.pangolin

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bytedance.sdk.openadsdk.*
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTRewardVideoAd.RewardAdInteractionListener
import io.flutter.app.FlutterActivity


import io.flutter.plugin.common.MethodChannel
import java.util.*

class RewardVideo : FlutterActivity() {
    private var mTTAdNative: TTAdNative? = null
    private var mttRewardVideoAd: TTRewardVideoAd? = null
    var mHorizontalCodeId: String? = null
    var mVerticalCodeId: String? = null
    var supportDeepLink = true
    var rewardName: String? = null
    var rewardAmount = 0
    var expressViewAcceptedSizeW = 500.0
    var expressViewAcceptedSizeH = 500.0
    var userID = "user123"
    var mediaExtra = "media_extra"
    var debug = false
    var mIsExpress = false //是否请求模板广告

    var context: Context? = null
    var activity: Activity? = null

    fun init() {

        //step1:初始化sdk
        val ttAdManager = TTAdManagerHolder.get()
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get()!!.requestPermissionIfNecessary(context)
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager!!.createAdNative(context)
        configAd()
    }

    private fun configAd() {
        if (mHorizontalCodeId != null) {
            loadAd(mHorizontalCodeId, supportDeepLink, rewardName, rewardAmount, expressViewAcceptedSizeW.toFloat(), expressViewAcceptedSizeH.toFloat(), userID, mediaExtra, TTAdConstant.HORIZONTAL)
        } else {
            loadAd(mVerticalCodeId, supportDeepLink, rewardName, rewardAmount, expressViewAcceptedSizeW.toFloat(), expressViewAcceptedSizeH.toFloat(), userID, mediaExtra, TTAdConstant.VERTICAL)
        }
    }

    private var mHasShowDownloadActive = false
    private fun loadAd(codeId: String?, supportDeepLink: Boolean, rewardName: String?, rewardAmount: Int, expressViewAcceptedSizeW: Float, expressViewAcceptedSizeH: Float, userID: String, mediaExtra: String, orientation: Int) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        val adSlot: AdSlot = if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，
            AdSlot.Builder()
                    .setCodeId(codeId)
                    .setSupportDeepLink(supportDeepLink)
                    .setRewardName(rewardName) //奖励的名称
                    .setRewardAmount(rewardAmount) //奖励的数量
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(expressViewAcceptedSizeW, expressViewAcceptedSizeH)
                    .setUserID(userID) //用户id,必传参数
                    .setMediaExtra(mediaExtra) //附加参数，可选
                    .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build()
        } else {
            //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
            AdSlot.Builder()
                    .setCodeId(codeId)
                    .setSupportDeepLink(supportDeepLink)
                    .setRewardName(rewardName) //奖励的名称
                    .setRewardAmount(rewardAmount) //奖励的数量
                    .setUserID(userID) //用户id,必传参数
                    .setMediaExtra(mediaExtra) //附加参数，可选
                    .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build()
        }
        //step5:请求广告
        mTTAdNative!!.loadRewardVideoAd(adSlot, object : RewardVideoAdListener {
            override fun onError(code: Int, message: String) {
                Log.e(TAG, "onError: $code, $message")
                if (debug) {
                    TToast.show(context!!, message)
                }
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            override fun onRewardVideoCached() {
                if (debug) {
                    Log.e(TAG, "onRewardVideoCached")
                    TToast.show(context!!, "rewardVideoAd video cached")
                }
                mttRewardVideoAd!!.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test")
                mttRewardVideoAd = null
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            override fun onRewardVideoAdLoad(ad: TTRewardVideoAd) {
                if (debug) {
                    Log.e(TAG, "onRewardVideoAdLoad")
                    TToast.show(context!!, "rewardVideoAd loaded 广告类型：" + getAdType(ad.rewardVideoAdType))
                }
                mttRewardVideoAd = ad
                mttRewardVideoAd!!.setRewardAdInteractionListener(object : RewardAdInteractionListener {
                    override fun onAdShow() {
                        if (debug) {
                            TToast.show(context!!, "rewardVideoAd show")
                        }
                    }

                    override fun onAdVideoBarClick() {
                        if (debug) {
                            TToast.show(context!!, "rewardVideoAd bar click")
                        }
                    }

                    override fun onAdClose() {
                        if (debug) {
                            TToast.show(context!!, "rewardVideoAd close")
                        }
                        val rewardVideoCallBack: MutableMap<String, Any> = HashMap()
                        rewardVideoCallBack["rewardVerify"] = false
                        rewardVideoCallBack["rewardAmount"] = 0
                        rewardVideoCallBack["rewardName"] = "rewardVideo Close"
                        _channel!!.invokeMethod("onRewardResponse", rewardVideoCallBack)
                    }

                    //视频播放完成回调
                    override fun onVideoComplete() {
                        if (debug) {
                            TToast.show(context!!, "rewardVideoAd complete")
                        }
                    }

                    override fun onVideoError() {
                        if (debug) {
                            TToast.show(context!!, "rewardVideoAd error")
                        }
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    override fun onRewardVerify(rewardVerify: Boolean, rewardAmount: Int, rewardName: String, errorCode: Int, errorMsg: String) {
                        if (debug) {
                            TToast.show(context!!, "verify:" + rewardVerify + " amount:" + rewardAmount +
                                    " name:" + rewardName)
                        }
                        val rewardVideoCallBack: MutableMap<String, Any> = HashMap()
                        rewardVideoCallBack["rewardVerify"] = rewardVerify
                        rewardVideoCallBack["rewardAmount"] = rewardAmount
                        rewardVideoCallBack["rewardName"] = rewardName
                        _channel!!.invokeMethod("onRewardResponse", rewardVideoCallBack)
                    }

                    override fun onSkippedVideo() {
                        if (debug) {
                            TToast.show(context!!, "rewardVideoAd has onSkippedVideo")
                        }
                    }
                })
                mttRewardVideoAd!!.setDownloadListener(object : TTAppDownloadListener {
                    override fun onIdle() {
                        mHasShowDownloadActive = false
                    }

                    override fun onDownloadActive(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
                        if (debug) {
                            Log.d("DML", "onDownloadActive==totalBytes=$totalBytes,currBytes=$currBytes,fileName=$fileName,appName=$appName")
                        }
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true
                            if (debug) {
                                TToast.show(context!!, "下载中，点击下载区域暂停", Toast.LENGTH_LONG)
                            }
                        }
                    }

                    override fun onDownloadPaused(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
                        if (debug) {
                            Log.d("DML", "onDownloadPaused===totalBytes=$totalBytes,currBytes=$currBytes,fileName=$fileName,appName=$appName")
                            TToast.show(context!!, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG)
                        }
                    }

                    override fun onDownloadFailed(totalBytes: Long, currBytes: Long, fileName: String, appName: String) {
                        if (debug) {
                            Log.d("DML", "onDownloadFailed==totalBytes=$totalBytes,currBytes=$currBytes,fileName=$fileName,appName=$appName")
                            TToast.show(context!!, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG)
                        }
                    }

                    override fun onDownloadFinished(totalBytes: Long, fileName: String, appName: String) {
                        if (debug) {
                            Log.d("DML", "onDownloadFinished==totalBytes=$totalBytes,fileName=$fileName,appName=$appName")
                            TToast.show(context!!, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG)
                        }
                    }

                    override fun onInstalled(fileName: String, appName: String) {
                        if (debug) {
                            Log.d("DML", "onInstalled==,fileName=$fileName,appName=$appName")
                            TToast.show(context!!, "安装完成，点击下载区域打开", Toast.LENGTH_LONG)
                        }
                    }
                })
            }
        })
    }

    private fun getAdType(type: Int): String {
        when (type) {
            TTAdConstant.AD_TYPE_COMMON_VIDEO -> return "普通激励视频，type=$type"
            TTAdConstant.AD_TYPE_PLAYABLE_VIDEO -> return "Playable激励视频，type=$type"
            TTAdConstant.AD_TYPE_PLAYABLE -> return "纯Playable，type=$type"
        }
        return "未知类型+type=$type"
    }

    companion object {
        private const val TAG = "RewardVideo"
        var _channel: MethodChannel? = null
    }
}