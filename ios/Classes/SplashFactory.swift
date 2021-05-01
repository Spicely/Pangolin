//
//  SplashFactory.swift
//  pangolin
//
//  Created by Spicely on 2021/5/1.
//

import Foundation
import BUAdSDK

class SplashFactory: NSObject, FlutterPlatformViewFactory {
    var messenger: FlutterBinaryMessenger
    var flutterRegister:FlutterPluginRegistrar
    
    init(withMessenger registart: FlutterPluginRegistrar) {
       self.messenger = registart.messenger()
       self.flutterRegister = registart
       super.init()
    }
    
    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        return SplashView(withFrame: frame, viewIdentifier: viewId, arguments: args, binaryMessenger: flutterRegister)
    }
    
//    func createArgsCodec() -> FlutterMessageCodec & NSObjectProtocol {
//        return FlutterStandardMessageCodec.sharedInstance()
//    }
    
}

class SplashView: NSObject, FlutterPlatformView, BUSplashAdDelegate, FlutterStreamHandler {
   
    
    private var _frame: CGRect
    private var splashView: BUSplashAdView
    private var flutterRegister: FlutterPluginRegistrar
    private var event: FlutterEventChannel
    private var eventSink: FlutterResult?
    
    init(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?, binaryMessenger register: FlutterPluginRegistrar) {
        flutterRegister = register
        if (frame.width == 0 || frame.height == 0) {
            _frame = CGRect(x: 0, y: 0, width: 100, height: 100)
        } else {
            _frame = frame
        }
        var codeId: String? = ""
        event = FlutterEventChannel(name: "com.tongyangsheng.pangolin/pangolinSplashAd_\(viewId)", binaryMessenger: register.messenger())
       
        
        if let args = args as? [String: Any] {
            codeId = args["mCodeId"] as? String
        }
        
        splashView = BUSplashAdView(slotID: codeId!, frame: _frame)
        super.init()
        event.setStreamHandler(self)
        splashView.delegate = self
        splashView.loadAdData()
    }
    
    func view() -> UIView {
        return splashView
    }
    
    // 开屏广告加载失败
    func splashAd(_ splashAd: BUSplashAdView, didFailWithError error: Error?) {
        splashAd.removeFromSuperview()
    }
    
    // 开屏视频关闭
    func splashAdDidClose(_ splashAd: BUSplashAdView) {
        eventSink?("onAdTimeOver")
        splashAd.removeFromSuperview()
    }
    
    // 回调方法的修改, 需要拿到浮窗 view , 调用转场动画
    func splashAdDidLoad(_ splashAd: BUSplashAdView) {
        
        print("1111111111111111111111111111")
    }
    
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        return nil
    }
    
}
