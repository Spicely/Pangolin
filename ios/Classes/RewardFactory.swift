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
    private var rewardedAd: BUNativeExpressRewardedVideoAd
    
    init(withFrame frame: CGRect, viewIdentifier viewId: Int64, arguments args: Any?, binaryMessenger register: FlutterPluginRegistrar) {
        flutterRegister = register
        if (frame.width == 0 || frame.height == 0) {
            _frame = CGRect(x: 0, y: 0, width: 100, height: 100)
        } else {
            _frame = frame
        }
        var codeId: String? = ""
        event = FlutterEventChannel(name: "com.tongyangsheng.pangolin/pangolinRewardAd_\(viewId)", binaryMessenger: register.messenger())
        let args = args as? [String: Any]
        if args != nil {
            codeId = args!["mCodeId"] as? String
        }
        rewardView = UIViewController()
        let model = BURewardedVideoModel()
        model.userId = (args!["userId"] as! String)
        print(args!["rewardName"] as Any)
        print("---------------------------")
        if ((args!["rewardName"] as? String) != nil) {
            model.rewardName = (args!["rewardName"] as! String)
        }
        if ((args!["extra"] as? String) != nil) {
            model.extra = (args!["extra"] as! String)
        }

        if ((args!["rewardAmount"] as? Int) != nil) {
            model.rewardAmount = (args!["rewardAmount"] as! Int)
        }
        
        rewardedAd = BUNativeExpressRewardedVideoAd(slotID: codeId!, rewardedVideoModel: model)
        super.init()
        
        event.setStreamHandler(self)
        rewardedAd.loadData()
        rewardedAd.delegate = self
        
    }
    
    func view() -> UIView {
        return rewardView.view
    }
    
    func nativeExpressRewardedVideoAdDidDownLoadVideo(_ rewardedVideoAd: BUNativeExpressRewardedVideoAd) {
        rewardedAd.show(fromRootViewController: rewardView)
    }
    // 关闭
    func nativeExpressRewardedVideoAdDidClose(_ rewardedVideoAd: BUNativeExpressRewardedVideoAd) {
        eventSink?("onAdClose")
        rewardView.dismiss(animated: false)
    }
    
    
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        return nil
    }
    
}
