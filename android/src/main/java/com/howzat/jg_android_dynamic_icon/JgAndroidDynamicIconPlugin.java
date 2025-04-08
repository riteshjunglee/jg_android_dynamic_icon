package com.howzat.jg_android_dynamic_icon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import java.util.List;

public class JgAndroidDynamicIconPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    private static final String TAG = "JgAndroidDynamicIcon";
    private Context context;
    private Activity activity;
    private MethodChannel channel;
    private List<String> classNames;
    private List<String> args;
    private boolean iconChanged = false;
    private ScreenLockReceiver screenLockReceiver;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "jg_android_dynamic_icon");
        channel.setMethodCallHandler(this);
        this.context = flutterPluginBinding.getApplicationContext();
        registerScreenLockReceiver();
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        unregisterScreenLockReceiver();
        this.context = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        this.context = activity.getApplicationContext();
        registerScreenLockReceiver();
    }

    @Override
    public void onDetachedFromActivity() {
        unregisterScreenLockReceiver();
        this.activity = null;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        this.context = activity.getApplicationContext();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "initialize":
                classNames = call.arguments();
                result.success(null);
                break;
            case "changeIcon":
                changeIcon(call);
                result.success(null);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void changeIcon(MethodCall call) {
        if (classNames == null || classNames.isEmpty()) {
            Log.e(TAG, "Initialization Failed! Provide activity-alias class names.");
            return;
        }
        args = call.arguments();
        iconChanged = false;
    }

    private void scheduleIconUpdate() {
        if (!iconChanged)
            enqueueIconChange();
    }

    private void enqueueIconChange() {
        if (context == null || args == null || args.isEmpty()) {
            Log.e(TAG, "enqueueIconChange: Missing context or icon arguments.");
            return;
        }

        String newIconClass = args.get(0);
        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                Log.e(TAG, "enqueueIconChange: PackageManager is null!");
                return;
            }

            for (String iconClass : classNames) {
                pm.setComponentEnabledSetting(
                        new ComponentName(context, iconClass),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }

            pm.setComponentEnabledSetting(
                    new ComponentName(context, newIconClass),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            Log.i(TAG, "enqueueIconChange: Icon changed to " + newIconClass);
            iconChanged = true;

        } catch (Exception e) {
            Log.e(TAG, "enqueueIconChange: Failed to change icon.", e);
        }
    }

    private void registerScreenLockReceiver() {
        if (context == null || screenLockReceiver != null)
            return;

        screenLockReceiver = new ScreenLockReceiver();
        context.registerReceiver(screenLockReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        Log.i(TAG, "ScreenLockReceiver registered.");
    }

    private void unregisterScreenLockReceiver() {
        if (context != null && screenLockReceiver != null) {
            context.unregisterReceiver(screenLockReceiver);
            screenLockReceiver = null;
            Log.i(TAG, "ScreenLockReceiver unregistered.");
        }
    }

    private class ScreenLockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.i(TAG, "Screen Locked! Updating app icon...");
                scheduleIconUpdate();
            }
        }
    }
}
