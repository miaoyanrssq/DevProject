#Nav路由

```java
 /**
     * 跳转
     *
     * @param url 完整url
     */
    public void to(String url) {
        Nav.with(this).to(url);
    }

    /**
     * 跳转
     *
     * @param path
     */
    public void toPath(String path) {
        Nav.with(this).toPath(path);
    }
```

androidManifest.xml中配置

```xml
<activity android:name=".GuideActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:host="www.devproject"
                    android:path="/GuideActivity"
                    android:scheme="http" />
            </intent-filter>
        </activity>
```

action  category   data都需要配置