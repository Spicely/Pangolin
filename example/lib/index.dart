import 'package:flutter/material.dart';
import 'package:pangolin/pangolin.dart';

class Index extends StatefulWidget {
  @override
  _IndexState createState() => _IndexState();
}

class _IndexState extends State<Index> {
  bool _status = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView(
        children: [
          Center(
            child: ElevatedButton(
              onPressed: () async {
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (BuildContext context) => PangolinRewardAd(
                      mCodeId: "945637758",
                    ),
                  ),
                );
              },
              child: Text('激励视频'),
            ),
          ),
          // Center(
          //   child: ElevatedButton(
          //     onPressed: () async {
          //       await Pangolin.loadBannerAd(
          //         mCodeId: "946029747",
          //         supportDeepLink: true,
          //         expressViewWidth: 640,
          //         expressViewHeight: 260,
          //       );
          //     },
          //     child: Text('Banner'),
          //   ),
          // ),
          // PangolinInfoView(
          //   mCodeId: '946025338',
          //   width: double.infinity,
          //   height: 320,
          // ),
          Center(
            child: ElevatedButton(
              onPressed: () {
                setState(() {
                  _status = !_status;
                });
              },
              child: Text(_status ? '隐藏banner' : '显示banner'),
            ),
          ),
          _status
              ? PangolinBannerAd(
                  mCodeId: '946029747',
                  width: double.infinity,
                  height: 150,
                  count: 1,
                )
              : Container(),
        ],
      ),
    );
  }
}
