#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint pangolin.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'pangolin'
  s.version          = '0.0.1'
  s.summary          = 'A new flutter plugin project.'
  s.description      = <<-DESC
A new flutter plugin project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '9.0'
  # s.libraries = '-ObjC', '-l c++', '-l c++abi', '-l sqlite3', '-l z', '-l-ObjC'
  # s.frameworks = 'Accelerate', 'AdSupport', 'AudioToolbox', 'AVFoundation', 'CoreGraphics', 'CoreImage', 'CoreLocation', 'CoreMedia', 'CoreMotion', 'CoreTelephony', 'CoreText', 'ImageIO', 'JavaScriptCore', 'MapKit', 'MediaPlayer', 'MobileCoreServices', 'QuartzCore', 'Security', 'StoreKit', 'SystemConfiguration', 'UIKit', 'WebKit', 'libbz2', 'libc++', 'libiconv', 'libresolv', 'libsqlite3', 'libxml2', 'libz', 'libc++abi'
  s.dependency 'Bytedance-UnionAD'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
