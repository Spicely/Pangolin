part of pangolon;

const _pangolinBannerAd = 'com.tongyangsheng.pangolin/pangolinBannerAd';

class PangolinBannerAd extends StatefulWidget {
  final String mCodeId;

  final int count;

  final double height;

  final double? width;

  /// 计时器 按秒计时
  final int interval;

  const PangolinBannerAd({
    Key? key,
    required this.mCodeId,
    this.count = 1,
    required this.height,
    this.width = double.infinity,
    this.interval = 0,
  }) : super(key: key);

  @override
  _PangolinBannerAdState createState() => _PangolinBannerAdState();
}

class _PangolinBannerAdState extends State<PangolinBannerAd> {
  EventChannel? _event;

  @override
  Widget build(BuildContext context) {
    Map<String, dynamic> creationParams = {
      'mCodeId': widget.mCodeId,
      'count': widget.count,
      'width': widget.width == double.infinity ? MediaQuery.of(context).size.width : widget.width,
      'height': widget.height == double.infinity ? MediaQuery.of(context).size.height : widget.height,
      'interval': widget.interval,
    };
    return SizedBox(
      height: widget.height,
      width: widget.width,
      child: Platform.isAndroid
          ? AndroidView(
              viewType: _pangolinBannerAd,
              // gestureRecognizers: gestureRecognizers,
              onPlatformViewCreated: onPlatformViewCreated,
              creationParams: creationParams,
              creationParamsCodec: const StandardMessageCodec(),
              // layoutDirection: widget.layoutDirection,
              layoutDirection: TextDirection.ltr,
              // hitTestBehavior: widget.hitTestBehavior,
            )
          : UiKitView(
              viewType: _pangolinBannerAd,
              // gestureRecognizers: gestureRecognizers,
              onPlatformViewCreated: onPlatformViewCreated,
              creationParams: creationParams,
              creationParamsCodec: const StandardMessageCodec(),
              // layoutDirection: widget.layoutDirection,
              // hitTestBehavior: widget.hitTestBehavior,
            ),
    );
  }

  Future<void> onPlatformViewCreated(int id) async {
    _event = EventChannel('${_pangolinBannerAd}_$id');
    _event!.receiveBroadcastStream().listen((dynamic data) {
      switch (data.toString()) {
        // case 'onAdTimeOver':
        //   {
        //     widget.onAdTimeOver?.call();
        //     widget.onAdOver.call();
        //   }
        //   break;
        // case 'onAdClicked':
        //   {
        //     widget.onAdClicked?.call();
        //     widget.onAdOver.call();
        //   }
        //   break;
        // case 'onAdShow':
        //   {
        //     widget.onAdShow?.call();
        //   }
        //   break;
        // case 'onAdSkip':
        //   {
        //     widget.onAdSkip?.call();
        //     widget.onAdOver.call();
        //   }
        //   break;
      }
    });
  }
}
