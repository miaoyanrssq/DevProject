# PhotoView 图片浏览缩放控件

一个流畅的photoview


#注意
由于facebook的Fresco图片加载组件所加载出来的drawable图片并非真实的drawable,无法直接获取图片真实宽高,也无法直接响应ImageMatrix的变换，
且根据Fresco文档的介绍,在后续的版本中,DraweeView会直接继承自View,所有暂不考虑支持Fresco。
对于其他第三方图片加载库如Glide,ImageLoader,xUtils都是支持的



2.xml添加
```xml
 <cn.zgy.photoview.PhotoView
     android:id="@+id/img"
     android:layout_width="match_parent"
     android:layout_height="match_parent"
     android:scaleType="centerInside"
     android:src="@drawable/bitmap1" />
```

3.java代码
```java
PhotoView photoView = (PhotoView) findViewById(R.id.img);
// 启用图片缩放功能
photoView.enable();
// 禁用图片缩放功能 (默认为禁用，会跟普通的ImageView一样，缩放功能需手动调用enable()启用)
photoView.disenable();
// 获取图片信息
Info info = photoView.getInfo();
// 从普通的ImageView中获取Info
Info info = PhotoView.getImageViewInfo(ImageView);
// 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
photoView.animaFrom(info);
// 从现在的图片变化到所给定的图片信息，用于图片放大后点击缩小到原来的位置，具体使用可以参照demo的使用
photoView.animaTo(info,new Runnable() {
       @Override
       public void run() {
           //动画完成监听
       }
   });
// 获取/设置 动画持续时间
photoView.setAnimaDuring(int during);
int d = photoView.getAnimaDuring();
// 获取/设置 最大缩放倍数
photoView.setMaxScale(float maxScale);
float maxScale = photoView.getMaxScale();
// 设置动画的插入器
photoView.setInterpolator(Interpolator interpolator);
```




   * 增加对普通ImageView的支持，可通过PhotoView的静态方法getImageViewInfo(ImageView)从一个普通的ImageView中获取Info，参照ImageViewActivity
   * 添加长按事件的监听，setOnLongClickListener()
   * 提高图片缩放到屏幕边缘的情况下滑动的流畅性
   * 新增get/setAnimaDuring() get/setMaxScale 获取设置动画的持续时间和图片最大缩放倍数
   * 通过setInterpolator可设置动画插入器
   
   * 增加图片的旋转功能

   * 宽高属性可以设置为wrap_content，添加对adjustViewBounds属性的支持
   * 增加对ScaleType.FIT_START,FIT_END对animaFrom的支持

   * 添加animaTo,animaFrom方法，支持图片点击放大缩小浏览功能
   * 添加enable()和disenable() 打开和关闭触摸缩放方法，默认打开 (当普通ImageView使用的时候建议关闭触摸缩放功能)
   * 支持所有ScaleType属性

