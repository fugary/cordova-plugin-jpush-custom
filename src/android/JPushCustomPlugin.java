package com.fugary.app.plugin.custom;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cordova插件，处理JPush厂商通道不能跳转问题
 *
 * @author gary.fu
 */
public class JPushCustomPlugin extends CordovaPlugin {

    private static final String TAG = JPushCustomPlugin.class.getSimpleName();
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
        extraMessage = getNotificationExtras(cordovaActivity.getIntent());
        LOG.i(TAG, "JPush Message: %s", extraMessage);
    }

    protected String getNotificationExtras(Intent intent) {
        String data = null;
        if (intent != null) {
            //获取华为平台附带的jpush信息
            if (intent.getData() != null) {
                data = intent.getData().toString();
            }
            //获取fcm/oppo/小米/vivo/魅族 平台附带的jpush信息
            if (TextUtils.isEmpty(data) && intent.getExtras() != null) {
                data = intent.getExtras().getString("JMessageExtra");
            }
        }
        return data;
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
