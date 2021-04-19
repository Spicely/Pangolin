#import "PangolinPlugin.h"
#if __has_include(<pangolin/pangolin-Swift.h>)
#import <pangolin/pangolin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "pangolin-Swift.h"
#endif

@implementation PangolinPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPangolinPlugin registerWithRegistrar:registrar];
}
@end
