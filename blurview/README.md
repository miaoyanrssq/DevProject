# RealtimeBlurView 毛玻璃效果

It's just a realtime blurring overlay like iOS UIVisualEffectView.


Just put the view in the layout xml, no Java code is required.

	// Views to be blurred
	<ImageView ../>
	
	<cn.zgy.blurview.RealtimeBlurView
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		app:realtimeBlurRadius="20dp"
		app:realtimeOverlayColor="#8000" />
	
	// Views above blurring overlay
	<Button ../>

Try the sample apk: [blurring.apk](imgs/blurring.apk)

# Adding to project

Add dependencies in your `build.gradle`(只能放在app的build.gradle中，不然会报错):

```groovy
	android {
		buildToolsVersion '24.0.2'                 // Use 23.0.3 or higher
		defaultConfig {
			minSdkVersion 15
			renderscriptTargetApi 19
			renderscriptSupportModeEnabled true    // Enable RS support
		}
	}
```

Add proguard rules if necessary:

```
-keep class android.support.v8.renderscript.** { *; }
```

# Limitations

- Adding multiple RealtimeBlurView (even not visible) may hurt drawing performance, like use it in ListView or RecyclerView.

- It will not work with SurfaceView / TextureView like VideoView, GoogleMapView

# Performance

RealtimeBlurView use RenderScript to blur the bitmap, just like [500px-android-blur](https://github.com/500px/500px-android-blur).

Everytime your window draw, it will render a blurred bitmap, so there is a performance cost. Set downsampleFactor>=4 will significantly reduce the render cost. However, if you just want to blur a static view, 500px-android-blur is good enough.

I've run the sample on some old phones like Samsung Galaxy S2, Samsung Galaxy S3, it runs at full FPS. Here is a performance chart while scrolling the list on Nexus 5.

![Nexus5](imgs/2.png)

