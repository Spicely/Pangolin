package com.tongyangsheng.pangolin;

import android.app.Activity;
import android.content.Context;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

import io.flutter.plugin.common.StandardMessageCodec;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class InfoViewFactory extends PlatformViewFactory {

    private final BinaryMessenger messenger;

    public InfoViewFactory(BinaryMessenger messenger, Activity activity, TTAdNative mTTAdNative) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
//        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId("946025338") //广告位id
//                .setSupportDeepLink(true)
//                .setAdCount(1) //请求广告数量为1到3条
//                .setExpressViewAcceptedSize(720,120) //期望模板广告view的size,单位dp
//                .build();
//        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
//
//
//            @Override
//            public void onError(int i, String s) {
//
//            }
//
//            @Override
//            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
//
//            }
//        });
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return null;
    }
}
