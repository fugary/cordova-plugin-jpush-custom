# cordova-plugin-jpush-custom
处理`jpush-phonegap-plugin`的`Android`厂商通道没有触发jpush.receiveNotification或者jpush.openNotification问题

依赖`jpush-phonegap-plugin`，因此需要先安装并配置好极光厂商通道。

使用方法如下

首先安装

```sh
cordova plugin add https://github.com/fugary/cordova-plugin-jpush-custom.git
```

然后使用，需要在`JPush.init()`等`JPush`相关代码之后

```js
if (window.JPushCustomPlugin) {
    window.JPushCustomPlugin.checkReceiveMessage()
}
```

