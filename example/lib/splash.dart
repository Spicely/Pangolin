import 'package:flutter/material.dart';
import 'package:pangolin/pangolin.dart';

import 'index.dart';

class Splash extends StatefulWidget {
  @override
  _SplashState createState() => _SplashState();
}

class _SplashState extends State<Splash> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: PangolinSplashAd(
        mCodeId: '887407013',
        onAdOver: () {
          Navigator.pushReplacement(context, MaterialPageRoute(builder: (BuildContext context) => Index()));
        },
      ),
    );
  }
}
