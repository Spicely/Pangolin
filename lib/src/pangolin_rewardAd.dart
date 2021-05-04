part of pangolon;

const _pangolinRewardAd = 'com.tongyangsheng.pangolin/pangolinRewardAd';

class PangolinRewardAd extends StatefulWidget {
  final String mCodeId;

  /// 倒计时结束
  final PangolinSplashAdOnAdTimeOver? onAdTimeOver;

  /// 跳过倒计时
  final PangolinSplashAdOnAdSkip? onAdSkip;

  /// 广告显示
  final PangolinSplashAdOnAdShow? onAdShow;

  /// 点击广告
  final PangolinSplashAdOnAdClicked? onAdClicked;

  /// 只要广告看不到都会触发 用于跳转页面
  // final PangolinSplashAdOnAdOver onAdOver;

  const PangolinRewardAd({
    Key? key,
    required this.mCodeId,
    // required this.onAdOver,
    this.onAdTimeOver,
    this.onAdSkip,
    this.onAdShow,
    this.onAdClicked,
  }) : super(key: key);

  @override
  _PangolinRewardAdState createState() => _PangolinRewardAdState();
}

class _PangolinRewardAdState extends State<PangolinRewardAd> {
  EventChannel? _event;

  @override
  void initState() {
    SystemChrome.setEnabledSystemUIOverlays([]);
    super.initState();
  }

  @override
  void dispose() {
    SystemChrome.setEnabledSystemUIOverlays(SystemUiOverlay.values);

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    Map<String, dynamic> creationParams = {
      'mCodeId': widget.mCodeId,
    };
    return Container(
      height: double.infinity,
      width: double.infinity,
      child: Platform.isAndroid
          ? AndroidView(
              viewType: _pangolinRewardAd,
              // gestureRecognizers: gestureRecognizers,
              onPlatformViewCreated: onPlatformViewCreated,
              creationParams: creationParams,
              creationParamsCodec: const StandardMessageCodec(),
              // layoutDirection: widget.layoutDirection,
              layoutDirection: TextDirection.ltr,
              // hitTestBehavior: widget.hitTestBehavior,
            )
          : UiKitView(
              viewType: _pangolinRewardAd,
              // gestureRecognizers: gestureRecognizers,
              onPlatformViewCreated: onPlatformViewCreated,
              creationParams: creationParams,
              creationParamsCodec: const StandardMessageCodec(),
              layoutDirection: TextDirection.ltr,
              // hitTestBehavior: widget.hitTestBehavior,
            ),
    );
  }

  Future<void> onPlatformViewCreated(int id) async {
    _event = EventChannel('${_pangolinRewardAd}_$id');
    _event!.receiveBroadcastStream().listen((dynamic data) {
      // switch (data.toString()) {
      //   case 'onAdTimeOver':
      //     {
      //       widget.onAdTimeOver?.call();
      //       widget.onAdOver.call();
      //     }
      //     break;
      //   case 'onAdClicked':
      //     {
      //       widget.onAdClicked?.call();
      //       widget.onAdOver.call();
      //     }
      //     break;
      //   case 'onAdShow':
      //     {
      //       widget.onAdShow?.call();
      //     }
      //     break;
      //   case 'onAdSkip':
      //     {
      //       widget.onAdSkip?.call();
      //       widget.onAdOver.call();
      //     }
      //     break;
      // }
    });
  }
}
