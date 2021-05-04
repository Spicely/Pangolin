import Flutter
import UIKit
import BUAdSDK

public class SwiftPangolinPlugin: NSObject, FlutterPlugin, BUSplashAdDelegate {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "com.tongyangsheng.pangolin", binaryMessenger: registrar.messenger())
    let instance = SwiftPangolinPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
    
    let splashInstance = SplashFactory(withMessenger: registrar)
    registrar.register(splashInstance, withId: "com.tongyangsheng.pangolin/pangolinSplashAd")
    
    let bannerInstance = BannerFactory(withMessenger: registrar)
    registrar.register(bannerInstance, withId: "com.tongyangsheng.pangolin/pangolinBannerAd")
    
    let rewardInstance = RewardFactory(withMessenger: registrar)
    registrar.register(rewardInstance, withId: "com.tongyangsheng.pangolin/pangolinRewardAd")
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    let args = call.arguments as? [String: Any]
    if (call.method == "register") {
        let appId = args!["appId"] as? String
        BUAdSDKManager.setAppID(appId!)
        result(true)
    } else if (call.method == "loadSplashAd") {
        let mCodeId = args!["mCodeId"] as? String

        BUAdSDKManager.setIsPaidApp(false)
        let frame = UIScreen.main.bounds
        let splashView = BUSplashAdView(slotID: mCodeId!, frame: frame)
        splashView.delegate = self
        let keyWindow = UIApplication.shared.windows.first
        splashView.loadAdData()
        keyWindow!.rootViewController!.view.addSubview(splashView)
        splashView.rootViewController = keyWindow!.rootViewController
        result(true)
    }
  }
}
