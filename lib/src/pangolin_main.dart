part of pangolon;

class Pangolin {
  static const int NETWORK_STATE_MOBILE = 1;
  static const int NETWORK_STATE_2G = 2;
  static const int NETWORK_STATE_3G = 3;
  static const int NETWORK_STATE_WIFI = 4;
  static const int NETWORK_STATE_4G = 5;

  static Future _methodHandler(MethodCall methodCall) {
    var response = _BasePangolinResponse.create(methodCall.method, methodCall.arguments);
    _pangolinResponseEventHandlerController.add(response);
    return Future.value();
  }

  static MethodChannel _channel = MethodChannel('com.tongyangsheng.pangolin')..setMethodCallHandler(_methodHandler);

  static StreamController<_BasePangolinResponse> _pangolinResponseEventHandlerController = new StreamController.broadcast();

  static Stream<_BasePangolinResponse> get pangolinResponseEventHandler => _pangolinResponseEventHandlerController.stream;

  static Future<bool?> registerPangolin({
    required String appId,
    required bool useTextureView,
    required String appName,
    required bool allowShowNotify,
    required bool allowShowPageWhenScreenLock,
    required bool debug,
    required bool supportMultiProcess,
    List<int>? directDownloadNetworkType,
  }) async {
    return await _channel.invokeMethod("register", {
      "appId": appId,
      "useTextureView": useTextureView,
      "appName": appName,
      "allowShowNotify": allowShowNotify,
      "allowShowPageWhenScreenLock": allowShowPageWhenScreenLock,
      "debug": debug,
      "supportMultiProcess": supportMultiProcess,
      "directDownloadNetworkType":
          directDownloadNetworkType ?? [NETWORK_STATE_MOBILE, NETWORK_STATE_3G, NETWORK_STATE_4G, NETWORK_STATE_WIFI]
    });
  }

  static Future<bool?> loadSplashAd({required String mCodeId, required bool debug}) async {
    return await _channel.invokeMethod("loadSplashAd", {"mCodeId": mCodeId, "debug": debug});
  }

  static Future loadRewardAd({
    required String mCodeId,
    required bool debug,
    required bool supportDeepLink,
    required String rewardName,
    required int rewardAmount,
    required bool isExpress,
    double? expressViewAcceptedSizeH,
    double? expressViewAcceptedSizeW,
    required userID,
    String? mediaExtra,
    required bool isHorizontal,
  }) async {
    return await _channel.invokeMethod("loadRewardAd", {
      "mCodeId": mCodeId,
      "debug": debug,
      "supportDeepLink": supportDeepLink,
      "rewardName": rewardName,
      "rewardAmount": rewardAmount,
      "isExpress": isExpress,
      "expressViewAcceptedSizeH": expressViewAcceptedSizeH,
      "expressViewAcceptedSizeW": expressViewAcceptedSizeW,
      "userID": userID,
      "mediaExtra": mediaExtra,
      "isHorizontal": isHorizontal,
    });
  }

  static Future loadBannerAd({
    required String mCodeId,
    required bool supportDeepLink,
    double? expressViewWidth,
    double? expressViewHeight,
    bool? isCarousel,
    int? interval,
    int? topMargin,
  }) async {
    return await _channel.invokeMethod("loadBannerAd", {
      "mCodeId": mCodeId,
      "supportDeepLink": supportDeepLink,
      "expressViewWidth": expressViewWidth,
      "expressViewHeight": expressViewHeight,
      "isCarousel": isCarousel,
      "interval": interval,
      "topMargin": topMargin
    });
  }

  static Future loadInterstitialAd({
    required String mCodeId,
    double? expressViewWidth,
    double? expressViewHeight,
  }) async {
    return await _channel.invokeMethod("loadInterstitialAd", {
      "mCodeId": mCodeId,
      "expressViewWidth": expressViewWidth,
      "expressViewHeight": expressViewHeight,
    });
  }

  static Future removeBannerAd() async {
    await _channel.invokeMethod("removeBannerAd");
  }
}
