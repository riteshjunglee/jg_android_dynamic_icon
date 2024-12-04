import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'jg_android_dynamic_icon_platform_interface.dart';

/// An implementation of [JgAndroidDynamicIconPlatform] that uses method channels.
class MethodChannelJgAndroidDynamicIcon extends JgAndroidDynamicIconPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('jg_android_dynamic_icon');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> initialize({required List<String> classNames}) async {
    await methodChannel.invokeMethod("initialize", classNames);
  }

  @override
  Future<void> changeIcon({required List<String> classNames}) async {
    await methodChannel.invokeMethod("changeIcon", classNames);
  }
}
