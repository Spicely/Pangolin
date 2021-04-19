part of pangolon;

const _viewType = 'com.tongyangsheng.pangolin/pangolin_info_view';

class PangoinInfoView extends StatefulWidget {
  final double height;

  final double width;

  final String mCodeId;

  const PangoinInfoView({
    Key? key,
    required this.height,
    required this.width,
    required this.mCodeId,
  }) : super(key: key);

  @override
  _PangoinInfoViewState createState() => _PangoinInfoViewState();
}

class _PangoinInfoViewState extends State<PangoinInfoView> {
  @override
  Widget build(BuildContext context) {
    Map<String, dynamic> creationParams = {
      'width': widget.width == double.infinity ? MediaQuery.of(context).size.width : widget.width,
      'height': widget.height == double.infinity ? MediaQuery.of(context).size.height : widget.height,
      'mCodeId': widget.mCodeId,
    };
    return SizedBox(
      width: widget.width,
      height: widget.height,
      child: Platform.isAndroid
          ? AndroidView(
              viewType: _viewType,
              // gestureRecognizers: gestureRecognizers,
              // onPlatformViewCreated: onPlatformViewCreated,
              creationParams: creationParams,
              creationParamsCodec: const StandardMessageCodec(),
              // layoutDirection: widget.layoutDirection,
              layoutDirection: TextDirection.ltr,
              // hitTestBehavior: widget.hitTestBehavior,
            )
          : UiKitView(
              viewType: _viewType,
              // gestureRecognizers: gestureRecognizers,
              // onPlatformViewCreated: onPlatformViewCreated,
              creationParams: creationParams,
              creationParamsCodec: const StandardMessageCodec(),
              // layoutDirection: widget.layoutDirection,
              // hitTestBehavior: widget.hitTestBehavior,
            ),
    );
  }

  // void onPlatformViewCreated(int id) {
  //   _markerChannel = PangoinInfoViewMarkerController(_viewType, id);
  // }
}
