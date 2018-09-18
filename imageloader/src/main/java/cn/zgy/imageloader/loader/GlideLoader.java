package cn.zgy.imageloader.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.zgy.imageloader.config.AnimationMode;
import cn.zgy.imageloader.config.GlobalConfig;
import cn.zgy.imageloader.config.PriorityMode;
import cn.zgy.imageloader.config.ScaleMode;
import cn.zgy.imageloader.config.ShapeMode;
import cn.zgy.imageloader.config.SingleConfig;
import cn.zgy.imageloader.utils.DownLoadImageService;
import cn.zgy.imageloader.utils.ImageUtil;


/**
 * Created by doudou on 2017/4/10.
 * 参考:
 * https://mrfu.me/2016/02/28/Glide_Sries_Roundup/
 */

public class GlideLoader implements ILoader {

    /**
     * @param context        上下文
     * @param cacheSizeInM   Glide默认磁盘缓存最大容量250MB
     * @param memoryCategory 调整内存缓存的大小 LOW(0.5f) ／ NORMAL(1f) ／ HIGH(1.5f);
     * @param isInternalCD   true 磁盘缓存到应用的内部目录 / false 磁盘缓存到外部存
     */
    @Override
    public void init(Context context, int cacheSizeInM, MemoryCategory memoryCategory, boolean isInternalCD) {
        Glide.get(context).setMemoryCategory(memoryCategory); //如果在应用当中想要调整内存缓存的大小，开发者可以通过如下方式：
        GlideBuilder builder = new GlideBuilder(context);
        if (isInternalCD) {
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, cacheSizeInM * 1024 * 1024));
        } else {
            builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, cacheSizeInM * 1024 * 1024));
        }
    }

    @Override
    public void request(final SingleConfig config) {
        RequestManager requestManager = Glide.with(config.getContext());
        DrawableTypeRequest request = getDrawableTypeRequest(config, requestManager);

        if (config.isAsBitmap()) {
            SimpleTarget target = new SimpleTarget<Bitmap>(config.getWidth(), config.getHeight()) {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    config.getBitmapListener().onSuccess(bitmap);
                }
            };

            setShapeModeAndBlur(config, request);

            if (config.getDiskCacheStrategy() != null) {
                request.diskCacheStrategy(config.getDiskCacheStrategy());
            }

            request.asBitmap().into(target);

        } else {

            if (request == null) {
                return;
            }

            if (ImageUtil.shouldSetPlaceHolder(config)) {
                request.placeholder(config.getPlaceHolderResId());
            }

            int scaleMode = config.getScaleMode();

            switch (scaleMode) {
                case ScaleMode.CENTER_CROP:
                    request.centerCrop();
                    break;
                case ScaleMode.FIT_CENTER:
                    request.fitCenter();
                    break;
                default:
                    request.fitCenter();
                    break;
            }

            setShapeModeAndBlur(config, request);

            //设置缩略图
            if (config.getThumbnail() != 0) {
                request.thumbnail(config.getThumbnail());
            }

            //设置图片加载的分辨 sp
            if (config.getoWidth() != 0 && config.getoHeight() != 0) {
                request.override(config.getoWidth(), config.getoHeight());
            }

            //是否跳过磁盘存储
            if (config.getDiskCacheStrategy() != null) {
                request.diskCacheStrategy(config.getDiskCacheStrategy());
            }

            //设置图片加载动画
            setAnimator(config, request);

            //设置图片加载优先级
            setPriority(config, request);

            if (config.getErrorResId() > 0) {
                request.error(config.getErrorResId());
            }

            if(config.isGif()){
                request.asGif();
            }

            if (config.getTarget() instanceof ImageView) {
                request.into((ImageView) config.getTarget());

            }
        }

    }

    /**
     * 设置加载优先级
     *
     * @param config
     * @param request
     */
    private void setPriority(SingleConfig config, DrawableTypeRequest request) {
        switch (config.getPriority()) {
            case PriorityMode.PRIORITY_LOW:
                request.priority(Priority.LOW);
                break;
            case PriorityMode.PRIORITY_NORMAL:
                request.priority(Priority.NORMAL);
                break;
            case PriorityMode.PRIORITY_HIGH:
                request.priority(Priority.HIGH);
                break;
            case PriorityMode.PRIORITY_IMMEDIATE:
                request.priority(Priority.IMMEDIATE);
                break;
            default:
                request.priority(Priority.IMMEDIATE);
                break;
        }
    }

    /**
     * 设置加载进入动画
     *
     * @param config
     * @param request
     */
    private void setAnimator(SingleConfig config, DrawableTypeRequest request) {
        if (config.getAnimationType() == AnimationMode.ANIMATIONID) {
            request.animate(config.getAnimationId());
        } else if (config.getAnimationType() == AnimationMode.ANIMATOR) {
            request.animate(config.getAnimator());
        } else if (config.getAnimationType() == AnimationMode.ANIMATION) {
            request.animate(config.getAnimation());
        }
    }

    @Nullable
    private DrawableTypeRequest getDrawableTypeRequest(SingleConfig config, RequestManager requestManager) {
        DrawableTypeRequest request = null;
        if (!TextUtils.isEmpty(config.getUrl())) {
            request = requestManager.load(ImageUtil.appendUrl(config.getUrl()));
        } else if (!TextUtils.isEmpty(config.getFilePath())) {
            request = requestManager.load(ImageUtil.appendUrl(config.getFilePath()));
        } else if (!TextUtils.isEmpty(config.getContentProvider())) {
            request = requestManager.loadFromMediaStore(Uri.parse(config.getContentProvider()));
        } else if (config.getResId() > 0) {
            request = requestManager.load(config.getResId());
        } else if(config.getFile() != null){
            request = requestManager.load(config.getFile());
        } else if(!TextUtils.isEmpty(config.getAssertspath())){
            request = requestManager.load(config.getAssertspath());
        } else if(!TextUtils.isEmpty(config.getRawPath())){
            request = requestManager.load(config.getRawPath());
        }
        return request;
    }

    /**
     * 设置图片滤镜和形状
     *此处用到了两个第三方库，
     * 'jp.wasabeef:glide-transformations:2.0.2'
       'jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1'
     用于渲染图片效果，为了封装的简便性，在此注释掉，如果需要，请打开注释内容，并添加库依赖
     * @param config
     * @param request
     */
    private void setShapeModeAndBlur(SingleConfig config, DrawableTypeRequest request) {

//        int count = 0;
//
//        Transformation[] transformation = new Transformation[statisticsCount(config)];
//
//        if (config.isNeedBlur()) {
//            transformation[count] = new BlurTransformation(config.getContext(), config.getBlurRadius());
//            count++;
//        }
//
//        if (config.isNeedBrightness()) {
//            transformation[count] = new BrightnessFilterTransformation(config.getContext(), config.getBrightnessLeve()); //亮度
//            count++;
//        }
//
//        if (config.isNeedGrayscale()) {
//            transformation[count] = new GrayscaleTransformation(config.getContext()); //黑白效果
//            count++;
//        }
//
//        if (config.isNeedFilteColor()) {
//            transformation[count] = new ColorFilterTransformation(config.getContext(), config.getFilteColor());
//            count++;
//        }
//
//        if (config.isNeedSwirl()) {
//            transformation[count] = new SwirlFilterTransformation(config.getContext(), 0.5f, 1.0f, new PointF(0.5f, 0.5f)); //漩涡
//            count++;
//        }
//
//        if (config.isNeedToon()) {
//            transformation[count] = new ToonFilterTransformation(config.getContext()); //油画
//            count++;
//        }
//
//        if (config.isNeedSepia()) {
//            transformation[count] = new SepiaFilterTransformation(config.getContext()); //墨画
//            count++;
//        }
//
//        if (config.isNeedContrast()) {
//            transformation[count] = new ContrastFilterTransformation(config.getContext(), config.getContrastLevel()); //锐化
//            count++;
//        }
//
//        if (config.isNeedInvert()) {
//            transformation[count] = new InvertFilterTransformation(config.getContext()); //胶片
//            count++;
//        }
//
//        if (config.isNeedPixelation()) {
//            transformation[count] =new PixelationFilterTransformation(config.getContext(), config.getPixelationLevel()); //马赛克
//            count++;
//        }
//
//        if (config.isNeedSketch()) {
//            transformation[count] =new SketchFilterTransformation(config.getContext()); //素描
//            count++;
//        }
//
//        if (config.isNeedVignette()) {
//            transformation[count] =new VignetteFilterTransformation(config.getContext(), new PointF(0.5f, 0.5f),
//                    new float[] { 0.0f, 0.0f, 0.0f }, 0f, 0.75f);//晕映
//            count++;
//        }
//
//        switch (config.getShapeMode()) {
//            case ShapeMode.RECT:
//
//                break;
//            case ShapeMode.RECT_ROUND:
//                transformation[count] = new RoundedCornersTransformation
//                        (config.getContext(), config.getRectRoundRadius(), 0, RoundedCornersTransformation.CornerType.ALL);
//                count++;
//                break;
//            case ShapeMode.OVAL:
//                transformation[count] = new CropCircleTransformation(config.getContext());
//                count++;
//                break;
//
//            case ShapeMode.SQUARE:
//                transformation[count] = new CropSquareTransformation(config.getContext());
//                count++;
//                break;
//        }
//
//        if (transformation.length != 0) {
//            request.bitmapTransform(transformation);
//
//        }

    }

    private int statisticsCount(SingleConfig config) {
        int count = 0;

        if (config.getShapeMode() == ShapeMode.OVAL || config.getShapeMode() == ShapeMode.RECT_ROUND || config.getShapeMode() == ShapeMode.SQUARE) {
            count++;
        }

        if (config.isNeedBlur()) {
            count++;
        }

        if (config.isNeedFilteColor()) {
            count++;
        }

        if (config.isNeedBrightness()) {
            count++;
        }

        if (config.isNeedGrayscale()) {
            count++;
        }

        if (config.isNeedSwirl()) {
            count++;
        }

        if (config.isNeedToon()) {
            count++;
        }

        if (config.isNeedSepia()) {
            count++;
        }

        if (config.isNeedContrast()) {
            count++;
        }

        if (config.isNeedInvert()) {
            count++;
        }

        if (config.isNeedPixelation()) {
            count++;
        }

        if (config.isNeedSketch()) {
            count++;
        }

        if (config.isNeedVignette()) {
            count++;
        }

        return count;
    }

    @Override
    public void pause() {
        Glide.with(GlobalConfig.context).pauseRequestsRecursive();

    }

    @Override
    public void resume() {
        Glide.with(GlobalConfig.context).resumeRequestsRecursive();
    }

    @Override
    public void clearDiskCache() {
        Glide.get(GlobalConfig.context).clearDiskCache();
    }

    @Override
    public void clearMomoryCache(View view) {
        Glide.clear(view);
    }

    @Override
    public void clearMomory() {
        Glide.get(GlobalConfig.context).clearMemory();
    }

    @Override
    public boolean isCached(String url) {
        return false;
    }

    @Override
    public void trimMemory(int level) {
        Glide.with(GlobalConfig.context).onTrimMemory(level);
    }

    @Override
    public void clearAllMemoryCaches() {
        Glide.with(GlobalConfig.context).onLowMemory();
    }

    @Override
    public void saveImageIntoGallery(DownLoadImageService downLoadImageService) {
        new Thread(downLoadImageService).start();
    }

}
