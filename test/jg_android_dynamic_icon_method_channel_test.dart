import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:jg_android_dynamic_icon/jg_android_dynamic_icon_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelJgAndroidDynamicIcon platform = MethodChannelJgAndroidDynamicIcon();
  const MethodChannel channel = MethodChannel('jg_android_dynamic_icon');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
