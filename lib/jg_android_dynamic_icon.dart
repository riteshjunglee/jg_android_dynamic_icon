import 'jg_android_dynamic_icon_platform_interface.dart';

class JgAndroidDynamicIcon {
  Future<String?> getPlatformVersion() {
    return JgAndroidDynamicIconPlatform.instance.getPlatformVersion();
  }

  static Future<void> initialize({required List<String> classNames}) async {
    await JgAndroidDynamicIconPlatform.instance
        .initialize(classNames: classNames);
  }

  Future<void> changeIcon({required List<String> classNames}) async {
    await JgAndroidDynamicIconPlatform.instance
        .changeIcon(classNames: classNames);
  }
}
