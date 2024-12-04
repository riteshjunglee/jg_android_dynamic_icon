import 'package:flutter_test/flutter_test.dart';
import 'package:jg_android_dynamic_icon/jg_android_dynamic_icon.dart';
import 'package:jg_android_dynamic_icon/jg_android_dynamic_icon_platform_interface.dart';
import 'package:jg_android_dynamic_icon/jg_android_dynamic_icon_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockJgAndroidDynamicIconPlatform
    with MockPlatformInterfaceMixin
    implements JgAndroidDynamicIconPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final JgAndroidDynamicIconPlatform initialPlatform = JgAndroidDynamicIconPlatform.instance;

  test('$MethodChannelJgAndroidDynamicIcon is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelJgAndroidDynamicIcon>());
  });

  test('getPlatformVersion', () async {
    JgAndroidDynamicIcon jgAndroidDynamicIconPlugin = JgAndroidDynamicIcon();
    MockJgAndroidDynamicIconPlatform fakePlatform = MockJgAndroidDynamicIconPlatform();
    JgAndroidDynamicIconPlatform.instance = fakePlatform;

    expect(await jgAndroidDynamicIconPlugin.getPlatformVersion(), '42');
  });
}
