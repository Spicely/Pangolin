part of pangolon;

const _pangolinRewardAd = 'com.tongyangsheng.pangolin/pangolinRewardAd';

typedef void PangolinRewardAdOnAdClose();

class PangolinRewardAd extends StatefulWidget {
  final String mCodeId;

  final String userId;

  final String? rewardName;

  final String? extra;

  final int? rewardAmount;

  /// 点击关闭
  final PangolinRewardAdOnAdClose? onAdClose;

  const PangolinRewardAd({
    Key? key,
    required this.mCodeId,
    required this.userId,
    this.rewardName,
    this.extra,
    this.rewardAmount,
    this.onAdClose,
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
      'userId': widget.userId,
      'extra': widget.extra,
      'rewardName': widget.rewardName,
      'rewardAmount': widget.rewardAmount,
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
      print(data);
      switch (data.toString()) {
        case 'onAdClose':
          {
            widget.onAdClose?.call();
            Navigator.pop(context);
          }
          break;
      }
    });
  }
}
