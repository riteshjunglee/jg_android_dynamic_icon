import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'jg_android_dynamic_icon_method_channel.dart';

abstract class JgAndroidDynamicIconPlatform extends PlatformInterface {
  /// Constructs a JgAndroidDynamicIconPlatform.
  JgAndroidDynamicIconPlatform() : super(token: _token);

  static final Object _token = Object();

  static JgAndroidDynamicIconPlatform _instance = MethodChannelJgAndroidDynamicIcon();

  /// The default instance of [JgAndroidDynamicIconPlatform] to use.
  ///
  /// Defaults to [MethodChannelJgAndroidDynamicIcon].
  static JgAndroidDynamicIconPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [JgAndroidDynamicIconPlatform] when
  /// they register themselves.
  static set instance(JgAndroidDynamicIconPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> initialize({required List<String> classNames}) async {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  Future<void> changeIcon({required List<String> classNames}) {
    throw UnimplementedError('changeIcon() has not been implemented.');
  }
}
