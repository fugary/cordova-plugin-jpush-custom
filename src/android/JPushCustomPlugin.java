package com.fugary.app.plugin.custom;

import android.app.Activity;
import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Cordova插件，处理JPush厂商通道不能跳转问题
 *
 * @author gary.fu
 */
public class JPushCustomPlugin extends CordovaPlugin {

    private static final String TAG = JPushCustomPlugin.class.getSimpleName();
    private static final List<String> IGNORED_EXTRAS_KEYS = Arrays.asList("cn.jpush.android.APPKEY");
    private Activity cordovaActivity;
    private JPushCustomPlugin instance;
    private String extraMessage;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaActivity = cordova.getActivity();
        instance = this;
        LOG.i(TAG, "init JPushCustomPlugin ...................................");
        processJPushMessage(cordovaActivity);
    }

    protected void processJPushMessage(Activity cordovaActivity) {
        if (instance == null) {
            return;
        }
        Map<String, Object> extrasMap = getNotificationExtras(cordovaActivity.getIntent());
        extraMessage = (String) extrasMap.get("JMessageExtra");
        LOG.i(TAG, "JPush Message: %s", extraMessage);
    }

    protected Map<String, Object> getNotificationExtras(Intent intent) {
        Map<String, Object> extrasMap = new HashMap<String, Object>();
        if (intent != null && intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                if (!IGNORED_EXTRAS_KEYS.contains(key)) {
                    if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                        extrasMap.put(key, intent.getIntExtra(key, 0));
                    } else {
                        extrasMap.put(key, intent.getStringExtra(key));
                    }
                }
            }
        }
        return extrasMap;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if (action.equals("checkReceiveMessage")) {
            this.checkReceiveMessage(callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void checkReceiveMessage(CallbackContext callbackContext) throws JSONException {
        if (this.extraMessage != null && !this.extraMessage.isEmpty()) {
            JSONObject json = new JSONObject(this.extraMessage);
            callbackContext.success(json);
            this.extraMessage = null;
        } else {
            callbackContext.error("No push message.");
        }
    }
}
