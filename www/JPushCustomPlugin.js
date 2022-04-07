var exec = require('cordova/exec');

var JPushCustomPlugin = function () {
    // noop
};
// 保留测试用
JPushCustomPlugin.prototype.coolMethod = function (arg0, success, error) {
    exec(success, error, 'JPushCustomPlugin', 'coolMethod', [arg0]);
};

var receiveMessageInAndroidCallback = function (data) {
    var eventData = {
        extras: data.n_extras,
        alert: data.n_content,
        title: data.n_title
    };
    // 调用
    cordova.fireDocumentEvent("jpush.receiveNotification", eventData);
};

JPushCustomPlugin.prototype.checkReceiveMessage = function () {
    exec(receiveMessageInAndroidCallback, function (err) {
        console.log(err);
    }, 'JPushCustomPlugin', 'checkReceiveMessage', []);
};

/**
 * 自定义cordova插件
 *
 * @type {JPushCustomPlugin}
 */
module.exports = new JPushCustomPlugin();
