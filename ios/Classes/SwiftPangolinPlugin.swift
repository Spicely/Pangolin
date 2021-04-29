import Flutter
import UIKit
import BUAdSDK

public class SwiftPangolinPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "com.tongyangsheng.pangolin", binaryMessenger: registrar.messenger())
    let instance = SwiftPangolinPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    let args = call.arguments as? [String: Any]
    if (call.method == "register") {
        let appId = args!["appId"] as? String
        BUAdSDKManager.setAppID(appId!)
        result(true)
    }
  }
}
