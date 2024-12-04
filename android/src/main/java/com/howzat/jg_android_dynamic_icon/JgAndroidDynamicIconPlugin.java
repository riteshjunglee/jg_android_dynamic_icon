package com.howzat.jg_android_dynamic_icon;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * JgAndroidDynamicIconPlugin
 */
public class JgAndroidDynamicIconPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private static final String TAG = "JgAndroidDynamicIcon";
    private Context context;
    private Activity activity;

    private static List<String> classNames = null;
    private static boolean iconChanged = false;
    private static List<String> args = new ArrayList<>();

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "jg_android_dynamic_icon");
        channel.setMethodCallHandler(this);
        this.context = flutterPluginBinding.getApplicationContext();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        Log.i(TAG, "onMethodCall");
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "initialize":
                classNames = call.arguments();
                break;
            case "changeIcon":
                changeIcon(call);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        this.context = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        Log.i(TAG, "onAttachedToActivity");
        this.activity = binding.getActivity();

        Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();

        // Observe the lifecycle
        lifecycle.addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_PAUSE) {
                updateIcon();
            }
        });
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        Log.i(TAG, "onDetachedFromActivityForConfigChanges");
        this.activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        Log.i(TAG, "onReattachedToActivityForConfigChanges");
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        Log.i(TAG, "onDetachedFromActivity");
        this.activity = null;
    }

    private void changeIcon(MethodCall call) {
        if (classNames == null || classNames.isEmpty()) {
            Log.e(TAG, "Initialization Failed!");
            Log.i(TAG, "List all the activity-alias class names in initialize()");
            return;
        }

        args = call.arguments();
        iconChanged = true;
    }

    void updateIcon() {
        if (iconChanged) {
            String className = args.get(0);
            PackageManager pm = activity.getPackageManager();
            String packageName = activity.getPackageName();
            int componentState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            int i = 0;
            for (String alias : classNames) {
                ComponentName cn = new ComponentName(packageName, alias);
                componentState = className.equals(alias) ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                pm.setComponentEnabledSetting(cn, componentState, PackageManager.DONT_KILL_APP);
            }

            if (i > classNames.size()) {
                Log.e(TAG, "class name " + className + " did not match in the initialized list.");
                return;
            }
            iconChanged = false;
            Log.d(TAG, "Icon switched to " + className);
        }
    }
}
