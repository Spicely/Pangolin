//
//  RewardFactory.swift
//  pangolin
//
//  Created by Spicely on 2021/5/4.
//

import Foundation
import BUAdSDK

class RewardFactory: NSObject, FlutterPlatformViewFactory {
    var messenger: FlutterBinaryMessenger
    var flutterRegister:FlutterPluginRegistrar
    
    init(withMessenger registart: FlutterPluginRegistrar) {
       self.messenger = registart.messenger()
       self.flutterRegister = registart
       super.init()
    }
    
    func create(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?) -> FlutterPlatformView {
        return RewardView(withFrame: frame, viewIdentifier: viewId, arguments: args, binaryMessenger: flutterRegister)
    }
    
    func createArgsCodec() -> FlutterMessageCodec & NSObjectProtocol {
        return FlutterStandardMessageCodec.sharedInstance()
    }
    
}

class RewardView: NSObject, FlutterPlatformView, FlutterStreamHandler, BUNativeExpressRewardedVideoAdDelegate {
    private var _frame: CGRect
    private var rewardView: UIViewController
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
        event = FlutterEventChannel(name: "com.tongyangsheng.pangolin/pangolinRewardAd_\(viewId)", binaryMessenger: register.messenger())
        if let args = args as? [String: Any] {
            codeId = args["mCodeId"] as? String
        }
        rewardView = UIViewController()
        super.init()
        
        event.setStreamHandler(self)
        let model = BURewardedVideoModel()
//        model.userId = codeId
//        model.rewardName = rewardName
//        model.extra = mediaExtra
        
        let rewardedAd = BUNativeExpressRewardedVideoAd(slotID: codeId!, rewardedVideoModel: model)
        rewardedAd.loadData()
        rewardedAd.delegate = self
        rewardedAd.show(fromRootViewController: rewardView)
    }
    
    func view() -> UIView {
        return rewardView.view
    }
    
    
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        return nil
    }
    
}
