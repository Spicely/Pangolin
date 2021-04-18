part of pangolon;

const String _errCode = "errCode";
const String _errStr = "errStr";

typedef _BasePangolinResponse _PangolinResponseInvoker(Map? argument);

Map<String, _PangolinResponseInvoker> _nameAndResponseMapper = {
  "onRewardResponse": (Map? argument) => PangolinOnRewardResponse.fromMap(argument!),
};

class _BasePangolinResponse {
  final int? errCode;
  final String? errStr;

  bool get isSuccessful => errCode == 0;

  _BasePangolinResponse._(this.errCode, this.errStr);

  /// create response from response pool
  factory _BasePangolinResponse.create(String name, Map? argument) => _nameAndResponseMapper[name]!(argument);
}

class PangolinOnRewardResponse extends _BasePangolinResponse {
  final bool? rewardVerify;
  final int? rewardAmount;
  final String? rewardName;

  PangolinOnRewardResponse.fromMap(Map map)
      : rewardVerify = map["rewardVerify"],
        rewardAmount = map["rewardAmount"],
        rewardName = map["rewardName"],
        super._(map[_errCode], map[_errStr]);
}
