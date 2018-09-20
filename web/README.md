

## AgentWeb 介绍  

AgentWeb 是一个基于的 Android WebView ，极度容易使用以及功能强大的库，提供了 Android WebView 一系列的问题解决方案 ，并且轻量和极度灵活


## AgentWeb 功能

* 支持进度条以及自定义进度条
* 支持文件下载
* 支持文件下载断点续传
* 支持下载通知形式提示进度
* 简化 Javascript 通信
* 支持 Android 4.4 Kitkat 以及其他版本文件上传
* 支持注入 Cookies
* 加强 Web 安全
* 支持全屏播放视频
* 兼容低版本 Js 安全通信
* 更省电 。
* 支持调起微信支付
* 支持调起支付宝（请参照sample）
* 默认支持定位
* 支持传入 WebLayout（下拉回弹效果）
* 支持自定义 WebView
* 支持 JsBridge
	

## 使用
#### 基础用法

```java
mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(-1, -1))                
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("http://www.jd.com");

```





* #### 调用 Javascript 方法拼接太麻烦 ？ 请看 。
```javascript
function callByAndroid(){
      console.log("callByAndroid")
  }
mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroid");
```

* #### Javascript 调 Java ?
```java
mAgentWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface(mAgentWeb,this));
window.android.callAndroid();
```


* #### 事件处理
```java
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
```

* #### 跟随 Activity Or Fragment 生命周期 ， 释放 CPU 更省电 。
```java
    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause(); 
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }
    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }    
```


* #### 全屏视频播放
```
<!--如果你的应用需要用到视频 ， 那么请你在使用 AgentWeb 的 Activity 对应的清单文件里加入如下配置-->
android:hardwareAccelerated="true"
android:configChanges="orientation|screenSize"
```

* #### 定位
```
<!--AgentWeb 是默认允许定位的 ，如果你需要该功能 ， 请在你的 AndroidManifest 文件里面加入如下权限 。-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

* #### WebChromeClient 与 WebViewClient 
```java
AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout,new LinearLayout.LayoutParams(-1,-1) )
                .useDefaultIndicator()
                .setReceivedTitleCallback(mCallback)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setSecutityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()
                .ready()
                .go(getUrl());
private WebViewClient mWebViewClient=new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
           //do you  work
        }
    };
private WebChromeClient mWebChromeClient=new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };                
```
* #### 返回上一页
```java
if (!mAgentWeb.back()){
       AgentWebFragment.this.getActivity().finish();
}
```

* #### 获取 WebView
```java
	mAgentWeb.getWebCreator().getWebView();
```




* #### 查看 Cookies
```java
String cookies=AgentWebConfig.getCookiesByUrl(targetUrl);
```

* #### 同步 Cookie
```java
AgentWebConfig.syncCookie("http://www.jd.com","ID=XXXX");
```

* #### MiddlewareWebChromeBase 支持多个 WebChromeClient
```java
//略，请查看 Sample
```
* #### MiddlewareWebClientBase 支持多个 WebViewClient
```java
//略，请查看 Sample
```

* ####  清空缓存 
```java
AgentWebConfig.clearDiskCache(this.getContext());
```

* #### 权限拦截
```java
protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            Log.i(TAG, "url:" + url + "  permission:" + permissions + " action:" + action);
            return false;
        }
    };
```

* #### AgentWeb 完整用法
```java
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(-1, 3)
                .setAgentWebWebSettings(getSettings())
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
                .setPermissionInterceptor(mPermissionInterceptor) 
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) 
                .setAgentWebUIController(new UIController(getActivity())) 
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .useMiddlewareWebChrome(getMiddlewareWebChrome())
                .useMiddlewareWebClient(getMiddlewareWebClient()) 
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl() 
                .createAgentWeb()
                .ready()
                .go(getUrl()); 
```

* #### AgentWeb 所需要的权限(在你工程中根据需求选择加入权限)
```
    <uses-permission android:name="android.permission.INTERNET"></uses-permission>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
    <uses-permission android:name="android.permission.CAMERA"></uses-permission>
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"></uses-permission>
```

* #### AgentWeb 所依赖的库
```
    compile "com.android.support:design:${SUPPORT_LIB_VERSION}" // (3.0.0开始该库可选)
    compile "com.android.support:support-v4:${SUPPORT_LIB_VERSION}"
    SUPPORT_LIB_VERSION=27.0.2(该值会更新)
```


## 混淆
如果你的项目需要加入混淆 ， 请加入如下配置

```java
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

```
Java 注入类不要混淆 ， 例如 sample 里面的 AndroidInterface 类 ， 需要 Keep 。

```java
-keepclassmembers class com.just.agentweb.sample.common.AndroidInterface{ *; }
```

## 注意事项
* 支付宝使用需要引入支付宝SDK ，并在项目中依赖 ， 微信支付不需要做任何操作。
* AgentWeb 内部使用了 `AlertDialog` 需要依赖 `AppCompat` 主题 。 
* `setAgentWebParent` 不支持  `ConstraintLayout` 。
* `mAgentWeb.getWebLifeCycle().onPause();`会暂停应用内所有`WebView` 。
* `minSdkVersion` 低于等于16以下自定义`WebView`请注意与 `JS` 之间通信安全。
* AgentWeb v3.0.0以上版本更新了包名，混淆的朋友们，请更新你的混淆配置。
* 多进程无法取消下载，[解决方案](https://github.com/Justson/AgentWeb/issues/294)。

## 常见问题

#### 修改 AgentWeb 默认的背景色 
```java
		FrameLayout frameLayout = mAgentWeb.getWebCreator().getWebParentLayout();
		frameLayout.setBackgroundColor(Color.BLACK);
```


## AgentWeb 视图结构

在学习如何使用之前 ， 先来简单看下 AgentWeb 视图结构 ，其实 AgentWeb 视图结构并不复杂 。
```xml
 <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <WebView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

        </WebView>
         <!--进度条-->
        <com.just.library.BaseIndicatorView
            android:layout_width="match_parent"
            android:layout_height="2dp"
            >

        </com.just.library.BaseIndicatorView>
</FrameLayout>
```
```java
AgentWeb.with(this)//传入Activity
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go("http://www.jd.com");

```

setAgentWebParent 实质是传入了 上面 FrameLayout 的父控件 。





