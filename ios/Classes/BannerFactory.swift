//
//  BannerFactory.swift
//  pangolin
//
//  Created by Spicely on 2021/5/3.
//

import Foundation
import BUAdSDK

class BannerFactory: NSObject, FlutterPlatformViewFactory {
    var messenger: FlutterBinaryMessenger
    var flutterRegister:FlutterPluginRegistrar
    
    init(withMessenger registart: FlutterPluginRegistrar) {
       self.messenger = registart.messenger()
       self.flutterRegister = registart
       super.init()
    }
    
    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        return BannerView(withFrame: frame, viewIdentifier: viewId, arguments: args, binaryMessenger: flutterRegister)
    }
    
    func createArgsCodec() -> FlutterMessageCodec & NSObjectProtocol {
        return FlutterStandardMessageCodec.sharedInstance()
    }
    
}

class BannerView: NSObject, FlutterPlatformView, BUNativeExpressBannerViewDelegate, FlutterStreamHandler {
    private var _frame: CGRect
    private var bannerView: UIViewController
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
        var interval: Int? = 0
        var width: Float = 0
        var height: Float = 0
        event = FlutterEventChannel(name: "com.tongyangsheng.pangolin/pangolinBannerAd_\(viewId)", binaryMessenger: register.messenger())
        if let args = args as? [String: Any] {
            codeId = args["mCodeId"] as? String
            interval = args["interval"] as? Int
            width = args["width"] as! Float
            height = args["height"] as! Float
        }
        bannerView = UIViewController()
        super.init()
        BUAdSDKManager.setIsPaidApp(false)
        event.setStreamHandler(self)
        let bView = BUNativeExpressBannerView(slotID: codeId!, rootViewController: bannerView, adSize: CGSize(width: CGFloat(width), height: CGFloat(height)), interval: interval!)
        bView.frame = _frame
        bView.loadAdData()
        bannerView.view.addSubview(bView)
    }
    
    func view() -> UIView {
        return bannerView.view
    }
    
    
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        return nil
    }
    
}
