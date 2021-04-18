part of pangolon;

const _viewType = 'com.tongyangsheng.pangolin_info_view';

class PangoinInfoView extends StatefulWidget {
  final dynamic creationParams;

  const PangoinInfoView({
    Key? key,
    this.creationParams,
  }) : super(key: key);

  @override
  _PangoinInfoViewState createState() => _PangoinInfoViewState();
}

class _PangoinInfoViewState extends State<PangoinInfoView> {
  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid) {
      return AndroidView(
        viewType: _viewType,
        // gestureRecognizers: gestureRecognizers,
        // onPlatformViewCreated: onPlatformViewCreated,
        creationParams: {
          'markers': [],
        },
        creationParamsCodec: const StandardMessageCodec(),
        // layoutDirection: widget.layoutDirection,
        layoutDirection: TextDirection.ltr,
        // hitTestBehavior: widget.hitTestBehavior,
      );
    } else {
      return UiKitView(
        viewType: _viewType,
        // gestureRecognizers: gestureRecognizers,
        // onPlatformViewCreated: onPlatformViewCreated,
        // creationParams: creationParams,
        creationParamsCodec: const StandardMessageCodec(),
        // layoutDirection: widget.layoutDirection,
        // hitTestBehavior: widget.hitTestBehavior,
      );
    }
  }

  // void onPlatformViewCreated(int id) {
  //   _markerChannel = PangoinInfoViewMarkerController(_viewType, id);
  // }
}
